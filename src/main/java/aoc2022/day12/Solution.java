package aoc2022.day12;

import aoc2022.input.InputLoader;
import lombok.Data;
import one.util.streamex.EntryStream;
import one.util.streamex.MoreCollectors;
import org.electronicmango.zipper.Zipper;

import java.awt.Point;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Solution {
    private static final int START_LOCATION = 'S';
    private static final int END_LOCATION = 'E';
    private static final int LOWEST_HEIGHT = 'a';
    private static final int TALLEST_HEIGHT = 'z';

    private static int ITER = 1;

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day12").stream()
                .map(String::chars)
                .map(IntStream::boxed)
                .map(Stream::toList)
                .collect(Zipper.zipCollector());
        final var startPoint = EntryStream.of(input)
                .mapValues(row -> row.indexOf(START_LOCATION))
                .filterValues(y -> y != -1)
                .mapKeyValue(Point::new)
                .collect(MoreCollectors.onlyOne())
                .orElseThrow();
        final var endPoint = EntryStream.of(input)
                .mapValues(row -> row.indexOf(END_LOCATION))
                .filterValues(y -> y != -1)
                .mapKeyValue(Point::new)
                .collect(MoreCollectors.onlyOne())
                .orElseThrow();
        State.setEndPoint(endPoint);
        EntryStream.of(input)
                .filterKeys(x -> x == startPoint.x)
                .values()
                .forEach(column -> column.set(startPoint.y, LOWEST_HEIGHT));
        EntryStream.of(input)
                .filterKeys(x -> x == endPoint.x)
                .values()
                .forEach(column -> column.set(endPoint.y, TALLEST_HEIGHT));
        final var result1 = solve(input, startPoint);
        System.out.println(result1);
        System.out.println();
        final var result2 = EntryStream.of(input)
                .flatMapValues(EntryStream::of)
                .filterValues(row -> row.getValue() == LOWEST_HEIGHT)
                .mapValues(Map.Entry::getKey)
                .mapKeyValue(Point::new)
                .mapToInt(start -> solve(input, start))
                .min()
                .orElseThrow();
        System.out.println(result2);
    }

    private static int solve(final List<List<Integer>> grid, final Point startPoint) {
        final var visitedLocations = new HashSet<State>();
        visitedLocations.add(new State(startPoint, 0));
        while (!visitedLocations.stream().allMatch(State::isDone)) {
            for (final var state : new HashSet<>(visitedLocations)) {
                if (state.isDone() || state.isFinished()) {
                    continue;
                }
                final var position = state.getPosition();
                final var steps = state.getSteps();
                final var height = (char) grid.get(position.x).get(position.y).intValue();
                final var deltas = List.of(List.of(-1, 0), List.of(1, 0), List.of(0, -1), List.of(0, 1));
                for (final var delta : deltas) {
                    final var newX = position.x + delta.get(0);
                    final var newY = position.y + delta.get(1);
                    if (newX < 0 || newY < 0 || newX >= grid.size() || newY >= grid.get(newX).size()) {
                        continue;
                    }
                    final var newHeight = (char) grid.get(newX).get(newY).intValue();
                    if (newHeight > (height + 1)) {
                        continue;
                    }
                    final var newState = new State(new Point(newX, newY), steps + 1);
                    if (newState.isFinished()) {
                        newState.setDone(true);
                        visitedLocations.add(newState);
                        continue;
                    }
                    final var differentRoutes = visitedLocations.stream()
                            .filter(s -> s.isHere(newState.getPosition()))
                            .toList();
                    for (final var differentRoute : differentRoutes) {
                        if (differentRoute.getSteps() > newState.getSteps()) {
                            differentRoute.setDone(true);
                        } else {
                            newState.setDone(true);
                        }
                    }
                    if (!newState.isDone()) {
                        visitedLocations.add(newState);
                    }
                }
                state.setDone(true);
            }
        }
        final var result = visitedLocations.stream()
                .filter(State::isFinished)
                .mapToInt(State::getSteps)
                .min()
                .orElse(Integer.MAX_VALUE);
        System.out.println(ITER++ + " startState=" + startPoint + " result=" + result);
        return result;
    }
}

@Data
final class State {
    private static Point END_POINT = new Point(0, 0);
    private final Point position;
    private final int steps;
    private boolean done = false;

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
