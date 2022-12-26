package aoc2022.day17;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;

public final class Solution {
    private static final List<Function<Integer, Shape>> SHAPES = List.of(
            y -> new Shape(new Point(2, y), new Point(3, y), new Point(4, y), new Point(5, y)),
            y -> new Shape(new Point(3, y), new Point(2, 1 + y), new Point(3, 1 + y), new Point(4, 1 + y), new Point(3, 2 + y)),
            y -> new Shape(new Point(2, y), new Point(3, y), new Point(4, y), new Point(4, 1 + y), new Point(4, 2 + y)),
            y -> new Shape(new Point(2, y), new Point(2, 1 + y), new Point(2, 2 + y), new Point(2, 3 + y)),
            y -> new Shape(new Point(2, y), new Point(3, y), new Point(2, 1 + y), new Point(3, 1 + y))
    );
    private static final Iterator<Function<Integer, Shape>> SHAPES_CYCLE = Iterables.cycle(SHAPES).iterator();

    public static void main(String[] args) {
        final var directions = InputLoader.read("day17", "input").chars().boxed().toList();
        final var directionCycle = Iterables.cycle(directions).iterator();
        final var board = new Board();
        board.add(new Shape(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(4, 0),
                new Point(5, 0), new Point(6, 0)));

        part1(directionCycle, board, 2022);
        var result1 = board.top();
        System.out.println(result1);

        final var cycle = part2(directionCycle, board);
        final var repeating = 1_000_000_000_000L - 2022;
        final var cycleCount = (repeating / cycle.getShapes().size());
        final var mod = repeating % cycleCount;
        part1(directionCycle, board, mod);
        final var result2 = board.top() + ((cycleCount - 2) * (cycle.top() - cycle.bottom()));
        System.out.println(result2);
    }

    private static void part1(final Iterator<Integer> directions, final Board board, final long iterations) {
        for (long i = 0; i < iterations; ++i) {
            final var newShape = fall(directions, board.getPoints());
            board.add(newShape);
        }
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
            shapeShifted.move(0, cycle1.bottom() - cycle2.bottom());
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
        while (!shape.atRest(solidPoints)) {
            shape.moveDown();
            shape.moveHorizontal(direction.next(), solidPoints);
        }
        return shape;
    }

    private static boolean cycle(final Board b1, final Board b2) {
        final var offset = b2.bottom() - b1.bottom();
        return StreamEx.of(b2.getPoints()).map(p -> new Point(p.x, p.y - offset)).toSet().equals(b1.getPoints());
    }
}

@EqualsAndHashCode
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

@EqualsAndHashCode
@Getter
final class Shape {
    private static final int MAX_X = 6;
    private static final int MIN_X = 0;
    private final Set<Point> points;

    Shape(final Point... points) {
        this.points = Sets.newHashSet(points);
    }

    Shape(final Shape shape) {
        this.points = StreamEx.of(shape.points).map(Point::new).toSet();
    }

    void moveHorizontal(final int direction, final Set<Point> solidPoints) {
        final var dx = direction == '>' ? 1 : -1;
        move(dx, 0);
        if (points.stream().anyMatch(p -> solidPoints.contains(p) || p.x > MAX_X || p.x < MIN_X)) {
            move(-dx, 0);
        }
    }

    void moveDown() {
        move(0, -1);
    }

    boolean atRest(final Set<Point> solidPoints) {
        points.forEach(p -> p.translate(0, -1));
        final var atRest = points.stream().anyMatch(solidPoints::contains);
        points.forEach(p -> p.translate(0, 1));
        return atRest;
    }

    void move(final int dx, final int dy) {
        points.forEach(p -> p.translate(dx, dy));
    }
}
