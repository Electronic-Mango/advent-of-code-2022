package aoc2022.day17;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.ToString;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.Collections;
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
        final var input = InputLoader.read("day17", "input");
        final var directions = input.chars().boxed().toList();
        final var dirIterator = Iterables.cycle(directions).iterator();
        final Set<Point> solidPoints = Sets.newHashSet(new Bottom().getPoints());
        System.out.println(solidPoints);
        final var iterations = SHAPES.size() * directions.size() * 4;
        System.out.println("Iterations " + iterations);

        fall(dirIterator, solidPoints, 2022);
        var result1 = solidPoints.stream().mapToLong(p -> p.y).max().orElse(0);
        System.out.println(result1);
    }

    private static int fall(final Iterator<Integer> direction, final Set<Point> solidPoints, final int iterations) {
        var shapeCount = 0;
        for (long i = 0; i < iterations; ++i) {
            final var startRow = solidPoints.stream().mapToInt(p -> p.y).max().orElse(0) + 4;
            final var shape = SHAPES_CYCLE.next().get();
            shapeCount++;
            shape.initialize(startRow);
            while (true) {
                shape.moveHorizontal(direction.next(), solidPoints);
                if (shape.atRest(solidPoints)) {
                    break;
                }
                shape.moveDown();
            }
            solidPoints.addAll(shape.getPoints());
        }
        System.out.println("Shape count: " + shapeCount);
        return shapeCount;
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

    void moveHorizontal(final int direction, final Set<Point> solidPoints) {
        move(direction == '>' ? 1 : -1, 0);
        if (getPoints().stream().anyMatch(p -> solidPoints.contains(p) || p.x > MAX_X || p.x < MIN_X)) {
            move(direction == '>' ? -1 : 1, 0);
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
final class Line extends Shape {
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
final class Cross extends Shape {
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
final class L extends Shape {
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
final class I extends Shape {
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
final class Square extends Shape {
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

@ToString
final class Bottom extends Shape {
    private final Set<Point> points = Set.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0),
            new Point(4, 0), new Point(5, 0), new Point(6, 0));

    @Override
    Set<Point> getPoints() {
        return points;
    }

    @Override
    int edgeRight() {
        return 0;
    }
}

final class Printer {
    static void print(final Set<Point> solidPoints) {
        final var points = EntryStream.of(StreamEx.of(solidPoints).groupingBy(p -> p.y)).sortedBy(Map.Entry::getKey)
                .mapValues(l -> l.stream().map(Point::getX).map(Double::intValue).toList())
                .mapValues(Printer::lineToString)
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