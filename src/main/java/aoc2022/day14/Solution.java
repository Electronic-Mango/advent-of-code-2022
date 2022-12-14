package aoc2022.day14;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;

import java.awt.Point;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Solution {
    private static final Point START = new Point(500, 0);
    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day14");
        final var occupiedPoints = StreamEx.of(input)
                .map(line -> StreamEx.split(line, " -> "))
                .flatMap(points -> points
                        .map(point -> StreamEx.split(point, ",").map(Integer::parseInt).toList())
                        .map(point -> new Point(point.get(0), point.get(1)))
                        .pairMap(Pair::with))
                .flatMap(line -> expandLine(line.getValue0(), line.getValue1()))
                .toMutableSet();
        final var maxY = occupiedPoints.stream().map(Point::getY).mapToInt(Double::intValue).max().orElseThrow();
        final var bottomLine = expandLine(new Point(START.x - 2 * maxY, maxY + 2), new Point(START.x + 2 * maxY, maxY + 2));
        occupiedPoints.addAll(bottomLine.toList());

        var count = 0;
        while (simulate(occupiedPoints, START, endPoint -> endPoint.y > maxY)) {
            count++;
        }
        System.out.println(count);

        do {
            count++;
        } while (simulate(occupiedPoints, START, START::equals));
        System.out.println(count + 1);
    }

    private static Stream<Point> expandLine(final Point start, final Point end) {
        final var span = Math.max(Math.abs(start.x - end.x), Math.abs(start.y - end.y));
        final var xSpan = range(start.x, end.x, span);
        final var ySpan = range(start.y, end.y, span);
        return StreamEx.zip(xSpan, ySpan, Point::new);
    }

    private static List<Integer> range(final int p1, final int p2, final int span) {
        final var cycle = IntStreamEx.rangeClosed(Math.min(p1, p2), Math.max(p1, p2)).boxed().toList();
        return Lists.newArrayList(Iterators.limit(Iterables.cycle(cycle).iterator(), span + 1));
    }

    private static boolean simulate(final Set<Point> occupied, final Point pos, final Function<Point, Boolean> cond) {
        if (!occupied.contains(new Point(pos.x, pos.y + 1))) {
            return simulate(occupied, new Point(pos.x, pos.y + 1), cond);
        } else if (!occupied.contains(new Point(pos.x - 1, pos.y + 1))) {
            return simulate(occupied, new Point(pos.x - 1, pos.y + 1), cond);
        } else if (!occupied.contains(new Point(pos.x + 1, pos.y + 1))) {
            return simulate(occupied, new Point(pos.x + 1, pos.y + 1), cond);
        } else {
            occupied.add(pos);
            return !cond.apply(pos);
        }
    }
}
