package aoc2022.day17;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
        final var directions = InputLoader.read("day17").chars().boxed().toList();
        final var directionCycle = Iterables.cycle(directions).iterator();
        final var board = new Board();
        board.add(new Shape(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0), new Point(4, 0),
                new Point(5, 0), new Point(6, 0)));

        part1(directionCycle, board, 2022);
        var result1 = board.height();
        System.out.println(result1);
    }

    private static void part1(final Iterator<Integer> directions, final Board board, final int iterations) {
        for (int i = 0; i < iterations; ++i) {
            final var newShape = fall(directions, board.getSolidPoints());
            board.add(newShape);
        }
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
}

@EqualsAndHashCode
@Getter
final class Board {
    private final Set<Shape> shapes = new HashSet<>();
    private final Set<Point> solidPoints = new HashSet<>();

    void add(final Shape shape) {
        shapes.add(shape);
        solidPoints.addAll(shape.getPoints());
    }

    int height() {
        return getSolidPoints().stream().mapToInt(p -> p.y).max().orElse(0);
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

    void moveHorizontal(final int direction, final Set<Point> solidPoints) {
        final var dx = direction == '>' ? 1 : -1;
        move(dx, 0);
        if (getPoints().stream().anyMatch(p -> solidPoints.contains(p) || p.x > MAX_X || p.x < MIN_X)) {
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

    private void move(final int dx, final int dy) {
        points.forEach(p -> p.translate(dx, dy));
    }
}
