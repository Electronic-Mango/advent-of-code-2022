package aoc2022.day17;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import lombok.ToString;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Solution {
    private static final List<Supplier<Shape>> SHAPES = List.of(Line::new, Cross::new, L::new, I::new, Square::new);
    private static final Iterator<Supplier<Shape>> SHAPES_CYCLE = Iterables.cycle(SHAPES).iterator();

    public static void main(String[] args) {
        final var times = BigDecimal.valueOf(1000000000000L)
                .add(BigDecimal.valueOf(-15))
                .divide(BigDecimal.valueOf(35), RoundingMode.DOWN);
        System.out.println(times);
        System.out.println(BigInteger.valueOf(1000000000000L).mod(times.toBigIntegerExact()));
//        System.out.println( % times);
        System.out.println(25 + (53L * times.longValue()));
        System.out.println();

        System.exit(-1);

        final var input = InputLoader.read("day17", "testinput");
        final var directions = input.chars().boxed().toList();
        final var dirIterator = Iterables.cycle(directions).iterator();
        Set<Point> solidPoints = new HashSet<>();
        for (int i = 0; i < 7; ++i) {
            solidPoints.add(new Point(i, 0));
        }
        System.out.println(solidPoints);
        final var iterations = SHAPES.size() * directions.size() * 4;
        System.out.println("Iterations " + iterations);

        solidPoints = getPoints(dirIterator, solidPoints, 15);
        var maxHeight1 = solidPoints.stream().mapToLong(p -> p.y).max().orElse(0);
        System.out.println(maxHeight1);

        solidPoints = getPoints(dirIterator, solidPoints, 35);
        solidPoints = getPoints(dirIterator, solidPoints, 35);
        printSolidPoints(solidPoints);
        System.out.println(solidPoints.stream().mapToLong(p -> p.y).max().orElse(0));

//        var previousMaxHeight = solidPoints.stream().mapToLong(p -> p.y).max().orElse(0);
//        System.out.println("initial prev " + previousMaxHeight);
//        for (int i = 0; i < 100; ++i) {
//            solidPoints = getPoints(dirIterator, solidPoints, iterations);
//            final var maxHeight = solidPoints.stream().mapToLong(p -> p.y).max().orElse(0);
//            System.out.println("maxHeight=" + maxHeight);
//            System.out.println("maxHeight - prev = " + (maxHeight - previousMaxHeight));
//            System.out.println();
//            previousMaxHeight = maxHeight;
//        }


//        final var times = 1000000000000.0 / iterations;
//        System.out.println(BigDecimal.valueOf(maxHeight1 * times).toPlainString());
//        printSolidPoints(solidPoints);
    }

    private static Set<Point> getPoints(Iterator<Integer> dirIterator, Set<Point> solidPoints, int iterations) {
        var shapeCount = 0;
        for (long i = 0; i < iterations; ++i) {
            if (i % 1000 == 0) {
                System.out.println(i + " " + solidPoints.size());
            }
            final var startRow = solidPoints.stream().mapToInt(p -> p.y).max().orElse(0) + 4;
            final var shape = SHAPES_CYCLE.next().get();
            shapeCount++;
            shape.initialize(startRow);
            final var spaceLeft = -2;
            final var maxSpace = shape.edgeRight();
            var dx = 0;
            for (int j = 0; j < 3; ++j) {
                dx += dirIterator.next() == '>' ? 1 : -1;
                dx = Math.min(maxSpace, dx);
                dx = Math.max(spaceLeft, dx);
            }
            shape.move(dx, -3);
            while (true) {
                shape.moveHorizontal(dirIterator.next(), solidPoints);
                if (shape.atRest(solidPoints)) {
                    break;
                }
                shape.moveDown();
            }
//            System.out.println(shape);
            solidPoints.addAll(shape.getPoints());
            if (shape.getPoints().stream().anyMatch(p -> ((p.y) % 25) == 0)) {
                System.out.println("COUNT=" + shapeCount + " ITERATION=" + i);
//                printSolidPoints(solidPoints);
            }
//            final var rows = StreamEx.of(solidPoints).groupingBy(p -> p.y);
//            if (rows.values().stream().anyMatch(l -> l.size() == 7)) {
//                final var maxRow = EntryStream.of(rows).filterValues(l -> l.size() == 7).keys().mapToLong
//                (Long::valueOf).max().orElseThrow();
//                solidPoints = solidPoints.stream().filter(p -> p.y >= maxRow).collect(Collectors.toSet());
//            }
//            System.out.println(solidPoints.size());
        }
        System.out.println(shapeCount);
        return solidPoints;
    }

    private static void printSolidPoints(final Set<Point> solidPoints) {
        final var points = EntryStream.of(StreamEx.of(solidPoints).groupingBy(p -> p.y)).sortedBy(Map.Entry::getKey)
                .mapValues(l -> l.stream().map(Point::getX).map(Double::intValue).toList())
                .mapValues(Solution::lineToString)
                .values()
                .map(s -> String.format("|%s|", s))
                .collect(Collectors.toList());
        Collections.reverse(points);
        points.forEach(System.out::println);
    }

    private static String lineToString(final List<Integer> line) {
        final var s = IntStream.generate(() -> '-').limit(7).boxed().collect(Collectors.toList());
        for (final var i : line) {
            s.set(i, (int) '#');
        }
        return StreamEx.of(s).map(i -> (char) i.intValue()).joining();
    }
}

abstract class Shape {
    private static final int MAX_X = 6;
    private static final int MIN_X = 0;

    void initialize(final int y) {
        getPoints().forEach(p -> p.translate(2, y));
    }

    abstract Set<Point> getPoints();

    abstract int edgeRight();

    void move(final int dx, final int dy) {
        getPoints().forEach(p -> p.translate(dx, dy));
    }

    void moveHorizontal(final int direction, Set<Point> solidPoints) {
        getPoints().forEach(p -> p.translate(direction == '>' ? 1 : -1, 0));
        if (getPoints().stream().anyMatch(p -> solidPoints.contains(p) || p.x > MAX_X || p.x < MIN_X)) {
            getPoints().forEach(p -> p.translate(direction == '>' ? -1 : 1, 0));
        }
    }

    void moveDown() {
        getPoints().forEach(point -> point.translate(0, -1));
    }

    boolean atRest(final Set<Point> solidPoints) {
        getPoints().forEach(p -> p.translate(0, -1));
        final var atRest = getPoints().stream().anyMatch(solidPoints::contains);
        getPoints().forEach(p -> p.translate(0, 1));
        return atRest;
    }
}

@ToString
class Line extends Shape {
    private final Set<Point> points = Set.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 1;
    }
}

@ToString
class Cross extends Shape {
    private final Set<Point> points = Set.of(new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1),
            new Point(1, 2));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 2;
    }

}

@ToString
class L extends Shape {
    private final Set<Point> points = Set.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(2, 1),
            new Point(2, 2));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 2;
    }

}

@ToString
class I extends Shape {
    private final Set<Point> points = Set.of(new Point(0, 0), new Point(0, 1), new Point(0, 2), new Point(0, 3));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 4;
    }

}

@ToString
class Square extends Shape {
    private final Set<Point> points = Set.of(new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 3;
    }

}
