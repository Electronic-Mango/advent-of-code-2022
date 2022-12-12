package aoc2022.day12;

import aoc2022.input.InputLoader;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.EntryStream;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//370
//363
public final class Solution {
    private static final int START_LOCATION = 'S';
    private static final int END_LOCATION = 'E';
    private static final int LOWEST_HEIGHT = 'a';
    private static final int TALLEST_HEIGHT = 'z';

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
        final var startPoint = getPointWithHeight(heightMap, START_LOCATION);
        final var endPoint = getPointWithHeight(heightMap, END_LOCATION);
        heightMap.put(startPoint, LOWEST_HEIGHT);
        heightMap.put(endPoint, TALLEST_HEIGHT);
        State.setEndPoint(endPoint);

        final var result1 = solve(heightMap, startPoint);
        System.out.println(result1);
        System.out.println();
        final var result2 = EntryStream.of(heightMap)
                .filterValues(height -> height == LOWEST_HEIGHT)
                .keys()
                .mapToInt(start -> solve(heightMap, start))
                .peek(System.out::println)
                .min()
                .orElseThrow();
        System.out.println(result2);
    }

    private static Point getPointWithHeight(final Map<Point, Integer> heightMap, final int targetHeight) {
        return EntryStream.of(heightMap).filterValues(height -> height == targetHeight).keys().findAny().orElseThrow();
    }

    private static int solve(final Map<Point, Integer> heightMap, final Point startPoint) {
        final var visitedLocations = new HashSet<State>();
        visitedLocations.add(new State(startPoint));
        while (!visitedLocations.stream().allMatch(State::isDone)) {
            for (final var state : visitedLocations.stream().filter(state -> !state.isDone()).toList()) {
                final var position = state.getPosition();
                final var steps = state.getSteps();
                final var height = heightMap.get(position);
                final var neighbouringPoints = List.of(
                        new Point(position.x - 1, position.y),
                        new Point(position.x + 1, position.y),
                        new Point(position.x, position.y - 1),
                        new Point(position.x, position.y + 1)
                );
                for (final var neighbour : neighbouringPoints.stream().filter(heightMap::containsKey).toList()) {
                    final var neighbourHeight = heightMap.get(neighbour);
                    if (neighbourHeight > (height + 1)) {
                        continue;
                    }
                    final var newState = new State(neighbour, steps + 1);
                    final var differentRoutes = visitedLocations.stream()
                            .filter(s -> s.isHere(newState.getPosition()))
                            .toList();
                    var bestRoute = true;
                    for (final var differentRoute : differentRoutes) {
                        if (differentRoute.getSteps() > newState.getSteps()) {
                            differentRoute.setDone(true);
                        } else {
                            bestRoute = false;
                        }
                    }
                    if (bestRoute) {
                        visitedLocations.add(newState);
                    }
                }
                state.setDone(true);
            }
        }
        return visitedLocations.stream()
                .filter(State::isFinished)
                .mapToInt(State::getSteps)
                .min()
                .orElse(Integer.MAX_VALUE);
    }
}

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
final class State {
    private static Point END_POINT = new Point(0, 0);
    private final Point position;
    private final int steps;
    private boolean done;

    State(final Point position) {
        this(position, 0);
    }

    State(final Point position, final int steps) {
        this.position = position;
        this.steps = steps;
        this.done = isFinished();
    }

    static void setEndPoint(final Point endPoint) {
        END_POINT = endPoint;
    }

    boolean isFinished() {
        return position.equals(END_POINT);
    }

    boolean isHere(final Point position) {
        return this.position.equals(position);
    }
}
