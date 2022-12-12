package aoc2022.day12;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Solution {
    private static final Integer START = (int) 'S';
    private static final Integer END = (int) 'E';
    private static final Integer LOWEST_HEIGHT = (int) 'a';
    private static final Integer TALLEST_HEIGHT = (int) 'z';

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day12");
        final var heightMap = EntryStream.of(input)
                .mapValues(String::chars)
                .mapValues(IntStream::boxed)
                .mapValues(Stream::toList)
                .flatMapValues(EntryStream::of)
                .mapToKey((y, xHeightEntry) -> new Point(xHeightEntry.getKey(), y))
                .mapValues(Map.Entry::getValue)
                .toMap();
        final var startPoint = EntryStream.of(heightMap).filterValues(START::equals).keys().findAny().orElseThrow();
        final var endPoint = EntryStream.of(heightMap).filterValues(END::equals).keys().findAny().orElseThrow();
        heightMap.put(startPoint, LOWEST_HEIGHT);
        heightMap.put(endPoint, TALLEST_HEIGHT);

        final var result1 = solve(heightMap, startPoint, endPoint);
        System.out.println(result1);
        System.out.println();
        final var result2 = EntryStream.of(heightMap)
                .filterValues(LOWEST_HEIGHT::equals)
                .keys()
                .mapToInt(start -> solve(heightMap, start, endPoint))
                .peek(System.out::println)
                .min()
                .orElseThrow();
        System.out.println(result2);
    }

    private static int solve(final Map<Point, Integer> heightMap, final Point start, final Point end) {
        final var visitedPoints = new HashMap<>(Map.of(start, 0, end, Integer.MAX_VALUE));
        final var nextPoints = new HashMap<Point, Integer>();
        do {
            nextPoints.clear();
            for (final var point : StreamEx.of(visitedPoints.keySet()).filterBy(end::equals, false).toList()) {
                final var height = heightMap.get(point) + 1;
                final var steps = visitedPoints.get(point) + 1;
                Stream.of(new Point(-1, 0), new Point(1, 0), new Point(0, -1), new Point(0, 1))
                        .map(delta -> new Point(point.x - delta.x, point.y - delta.y))
                        .filter(heightMap::containsKey)
                        .filter(next -> heightMap.get(next) <= height)
                        .filter(next -> visitedPoints.get(next) == null || visitedPoints.get(next) > steps)
                        .forEach(next -> nextPoints.put(next, steps));
            }
            visitedPoints.putAll(nextPoints);
        } while (visitedPoints.get(end) == Integer.MAX_VALUE && !nextPoints.isEmpty());
        return visitedPoints.get(end);
    }
}
