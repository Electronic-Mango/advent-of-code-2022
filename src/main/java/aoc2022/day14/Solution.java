package aoc2022.day14;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.function.ToBooleanBiFunction;
import org.javatuples.Pair;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Solution {
    private static final int EXTRA_FLOOR_DISTANCE = 2;
    private static final int SAND_START_X = 500;
    private static final int SAND_START_Y = 0;

    public static void main(final String[] args) {
        final var occupied = LinkedHashMultimap.<Integer, Integer>create();
        StreamEx.of(InputLoader.readLines("day14"))
                .flatMap(line -> StreamEx.split(line, " -> ")
                        .map(point -> StreamEx.split(point, ",").map(Integer::parseInt).toList())
                        .map(Pair::fromCollection)
                        .pairMap(Pair::add))
                .flatMap(line -> expandLine(line.getValue0(), line.getValue1(), line.getValue2(), line.getValue3()))
                .mapToEntry(Map.Entry::getKey, Map.Entry::getValue)
                .forKeyValue(occupied::put);
        final var height = Collections.max(occupied.values()) + EXTRA_FLOOR_DISTANCE;
        expandLine(SAND_START_X - height, height, SAND_START_X + height, height).forKeyValue(occupied::put);

        var count = 0;
        while (simulate(occupied, (x, y) -> y <= height - EXTRA_FLOOR_DISTANCE, SAND_START_X, SAND_START_Y)) {
            count++;
        }
        System.out.println(count);

        do {
            count++;
        } while (simulate(occupied, (x, y) -> x != SAND_START_X || y != SAND_START_Y, SAND_START_X, SAND_START_Y));
        System.out.println(count + 1);
    }

    private static EntryStream<Integer, Integer> expandLine(final int x1, final int y1, final int x2, final int y2) {
        final var span = (int) Point.distance(x1, y1, x2, y2);
        return EntryStream.zip(range(x1, x2, span), range(y1, y2, span));
    }

    private static List<Integer> range(final int p1, final int p2, final int span) {
        final var cyclicIterator = IntStreamEx.rangeClosed(Math.min(p1, p2), Math.max(p1, p2))
                .boxed().toListAndThen(Iterables::cycle).iterator();
        return Lists.newArrayList(Iterators.limit(cyclicIterator, span + 1));
    }

    private static boolean simulate(final Multimap<Integer, Integer> occupied,
                                    final ToBooleanBiFunction<Integer, Integer> restCondition,
                                    final int x,
                                    final int y) {
        if (!occupied.containsEntry(x, y + 1)) {
            return simulate(occupied, restCondition, x, y + 1);
        } else if (!occupied.containsEntry(x - 1, y + 1)) {
            return simulate(occupied, restCondition, x - 1, y + 1);
        } else if (!occupied.containsEntry(x + 1, y + 1)) {
            return simulate(occupied, restCondition, x + 1, y + 1);
        }
        occupied.put(x, y);
        return restCondition.applyAsBoolean(x, y);
    }
}
