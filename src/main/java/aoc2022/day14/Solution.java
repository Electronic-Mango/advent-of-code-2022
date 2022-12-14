package aoc2022.day14;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.function.ToBooleanBiFunction;
import org.javatuples.Pair;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class Solution {
    private static final int FLOOR_DISTANCE = 2;
    private static final int SAND_START_X = 500;
    private static final int SAND_START_Y = 0;

    public static void main(final String[] args) {
        final var occupied = LinkedHashMultimap.<Integer, Integer>create();
        InputLoader.readLines("day14").stream()
                .flatMap(line -> StreamEx.split(line, " -> ")
                        .map(point -> StreamEx.split(point, ",").map(Integer::parseInt).toList())
                        .map(Pair::fromCollection)
                        .pairMap(Pair::add))
                .flatMap(line -> expandLine(line.getValue0(), line.getValue1(), line.getValue2(), line.getValue3()))
                .forEach(point -> occupied.put(point.getValue0(), point.getValue1()));
        final var height = Collections.max(occupied.values()) + FLOOR_DISTANCE;
        expandLine(SAND_START_X - height, height, SAND_START_X + height, height)
                .forEach(point -> occupied.put(point.getValue0(), point.getValue1()));

        var count = 0;
        while (simulate(occupied, (x, y) -> y <= height - FLOOR_DISTANCE, SAND_START_X, SAND_START_Y)) {
            count++;
        }
        System.out.println(count);

        do {
            count++;
        } while (simulate(occupied, (x, y) -> x != SAND_START_X || y != SAND_START_Y, SAND_START_X, SAND_START_Y));
        System.out.println(count + 1);
    }

    private static Stream<Pair<Integer, Integer>> expandLine(final int x1, final int y1, final int x2, final int y2) {
        final var span = (int) Point.distance(x1, y1, x2, y2);
        return StreamEx.zip(range(x1, x2, span), range(y1, y2, span), Pair::with);
    }

    private static List<Integer> range(final int p1, final int p2, final int span) {
        final var cyclicIterator = IntStreamEx.rangeClosed(Math.min(p1, p2), Math.max(p1, p2))
                .boxed().toListAndThen(Iterables::cycle).iterator();
        return Lists.newArrayList(Iterators.limit(cyclicIterator, span + 1));
    }

    private static boolean simulate(final Multimap<Integer, Integer> occupied,
                                    final ToBooleanBiFunction<Integer, Integer> restPointCondition,
                                    final int x,
                                    final int y) {
        if (!occupied.containsEntry(x, y + 1)) {
            return simulate(occupied, restPointCondition, x, y + 1);
        } else if (!occupied.containsEntry(x - 1, y + 1)) {
            return simulate(occupied, restPointCondition, x - 1, y + 1);
        } else if (!occupied.containsEntry(x + 1, y + 1)) {
            return simulate(occupied, restPointCondition, x + 1, y + 1);
        }
        occupied.put(x, y);
        return restPointCondition.applyAsBoolean(x, y);
    }
}
