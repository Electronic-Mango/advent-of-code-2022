package aoc2022.day12;

import aoc2022.input.InputLoader;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Solution {
    private static final int START_VALUE = 'S';
    private static final int END_VALUE = 'E';
    private static final int LOWEST_HEIGHT = 'a';
    private static final int TALLEST_HEIGHT = 'z';

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day12");
        final var heightMap = EntryStream.of(input)
                .mapValues(String::chars)
                .mapValues(IntStream::boxed)
                .mapValues(Stream::toList)
                .flatMapToKey((y, row) -> IntStreamEx.range(row.size()).mapToObj(x -> new Point(x, y)))
                .mapToValue((point, row) -> row.get(point.x))
                .toMap();
        final var start = getPointWithHeight(heightMap, START_VALUE);
        final var end = getPointWithHeight(heightMap, END_VALUE);
        heightMap.put(start, LOWEST_HEIGHT);
        heightMap.put(end, TALLEST_HEIGHT);

        final var distances = getDistances(heightMap, end);
        final var result1 = distances.get(start);
        System.out.println(result1);
        final var result2 = EntryStream.of(distances)
                .filterKeys(point -> heightMap.get(point).equals(LOWEST_HEIGHT))
                .values()
                .mapToInt(Integer::valueOf)
                .min()
                .orElseThrow();
        System.out.println(result2);
    }

    private static Point getPointWithHeight(final Map<Point, Integer> heightMap, final int target) {
        return EntryStream.of(heightMap).filterValues(height -> height.equals(target)).keys().findAny().orElseThrow();
    }

    private static Map<Point, Integer> getDistances(final Map<Point, Integer> heightMap, final Point start) {
        final var pointsToHandle = new LinkedHashSet<State>();
        pointsToHandle.add(new State(start, 0));
        while (pointsToHandle.stream().anyMatch(State::isOpen)) {
            final var state = pointsToHandle.stream().filter(State::isOpen).findFirst().orElseThrow();
            final var position = state.getPosition();
            Stream.of(Pair.of(-1, 0), Pair.of(1, 0), Pair.of(0, -1), Pair.of(0, 1))
                    .map(delta -> new Point(position.x + delta.getLeft(), position.y + delta.getRight()))
                    .filter(heightMap::containsKey)
                    .filter(point -> heightMap.get(point) + 1 >= heightMap.get(position))
                    .map(point -> new State(point, state.getSteps() + 1))
                    .forEach(pointsToHandle::add);
            state.setOpen(false);
        }
        return pointsToHandle.stream().collect(Collectors.toMap(State::getPosition, State::getSteps));
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static final class State {
        @EqualsAndHashCode.Include
        private final Point position;
        private final int steps;
        private boolean open = true;
    }
}
