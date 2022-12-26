package aoc2022.day17;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import lombok.Getter;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public final class Solution {
    private static final long ITERATIONS_1 = 2022L;
    private static final long ITERATIONS_2 = 1_000_000_000_000L;
    private static final List<Function<Integer, Shape>> SHAPES = List.of(
            y -> new Shape(2, y, 3, y, 4, y, 5, y),
            y -> new Shape(3, y, 2, 1 + y, 3, 1 + y, 4, 1 + y, 3, 2 + y),
            y -> new Shape(2, y, 3, y, 4, y, 4, 1 + y, 4, 2 + y),
            y -> new Shape(2, y, 2, 1 + y, 2, 2 + y, 2, 3 + y),
            y -> new Shape(2, y, 3, y, 2, 1 + y, 3, 1 + y)
    );
    private static final Iterator<Function<Integer, Shape>> SHAPES_CYCLE = Iterables.cycle(SHAPES).iterator();

    public static void main(String[] args) {
        final var directions = Iterables.cycle(InputLoader.read("day17").chars().boxed().toList()).iterator();
        final var board = new Board();
        board.add(new Shape(0, 0, 1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0));

        part1(directions, board, ITERATIONS_1);
        var result1 = board.top();
        System.out.println(result1);

        final var cycle = part2(directions, board);
        final var repeating = ITERATIONS_2 - ITERATIONS_1;
        final var cycleCount = (repeating / cycle.getShapes().size());
        final var remaining = repeating % cycleCount;
        part1(directions, board, remaining);
        final var result2 = board.top() + ((cycleCount - 2) * (cycle.top() - cycle.bottom()));
        System.out.println(result2);
    }

    private static void part1(final Iterator<Integer> directions, final Board board, final long iterations) {
        LongStreamEx.range(iterations).mapToObj(i -> fall(directions, board.getPoints())).forEach(board::add);
    }

    private static Board part2(final Iterator<Integer> direction, final Board board) {
        final var cycle1 = new Board();
        final var cycle2 = new Board();
        final var firstShape = fall(direction, board.getPoints());
        board.add(firstShape);
        cycle1.add(firstShape);
        while (!Solution.cycle(cycle1, cycle2)) {
            final var shape = fall(direction, board.getPoints());
            board.add(shape);
            cycle2.add(shape);
            final var shapeShifted = new Shape(shape);
            shapeShifted.moveVertical(cycle1.bottom() - cycle2.bottom());
            if (!cycle1.getPoints().containsAll(shapeShifted.getPoints())) {
                cycle2.getShapes().forEach(cycle1::add);
                cycle2.clear();
            }
        }
        return cycle1;
    }

    private static Shape fall(final Iterator<Integer> direction, final Set<Point> solidPoints) {
        final var startRow = solidPoints.stream().mapToInt(p -> p.y).max().orElse(0) + 4;
        final var shape = SHAPES_CYCLE.next().apply(startRow);
        shape.moveHorizontal(direction.next(), solidPoints);
        while (shape.moveVertical(solidPoints)) {
            shape.moveHorizontal(direction.next(), solidPoints);
        }
        return shape;
    }

    private static boolean cycle(final Board b1, final Board b2) {
        final var offset = b2.bottom() - b1.bottom();
        return StreamEx.of(b2.getPoints()).map(p -> new Point(p.x, p.y - offset)).toSet().equals(b1.getPoints());
    }
}

@Getter
final class Board {
    private final Set<Shape> shapes = new HashSet<>();
    private final Set<Point> points = new HashSet<>();

    void add(final Shape shape) {
        shapes.add(shape);
        points.addAll(shape.getPoints());
    }

    void clear() {
        shapes.clear();
        points.clear();
    }

    int top() {
        return edge(Integer::max);
    }

    int bottom() {
        return edge(Integer::min);
    }

    private int edge(final IntBinaryOperator reduce) {
        return points.stream().mapToInt(p -> p.y).reduce(reduce).orElse(0);
    }
}

@Getter
final class Shape {
    private static final int MAX_X = 6;
    private static final int MIN_X = 0;
    private final Set<Point> points;

    Shape(final int... coordinates) {
        points = IntStreamEx.range(0, coordinates.length, 2)
                .mapToObj(i -> new Point(coordinates[i], coordinates[i + 1]))
                .toSet();
    }

    Shape(final Shape shape) {
        points = StreamEx.of(shape.points).map(Point::new).toSet();
    }

    void moveHorizontal(final int direction, final Set<Point> solidPoints) {
        final var dx = direction == '>' ? 1 : -1;
        move(dx, 0);
        if (points.stream().anyMatch(p -> solidPoints.contains(p) || p.x > MAX_X || p.x < MIN_X)) {
            move(-dx, 0);
        }
    }

    boolean moveVertical(final Set<Point> solidPoints) {
        move(0, -1);
        final var overlaps = points.stream().anyMatch(solidPoints::contains);
        if (overlaps) {
            move(0, 1);
        }
        return !overlaps;
    }

    void moveVertical(final int dy) {
        move(0, dy);
    }

    private void move(final int dx, final int dy) {
        points.forEach(p -> p.translate(dx, dy));
    }
}
