package aoc2022.day24;

import aoc2022.input.InputLoader;
import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Solution {
    private static final Set<Point> DELTAS = Set.of(
            new Point(0, -1),
            new Point(0, 1),
            new Point(-1, 0),
            new Point(1, 0),
            new Point(0, 0));

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day24");
        final var y = input.size() - 3;
        final var x = input.stream().mapToInt(String::length).max().orElseThrow() - 3;
        final var map = prepareMap(input, y);
        final var start = new Point(0, -1);
        final var end = new Point(x, y + 1);
        final var moves = preparePossibleMoves(x, y, start, end);

        final var result1 = move(start, end, map, moves, x, y);
        System.out.println(result1);

        final var result2 = result1 + move(end, start, map, moves, x, y) + move(start, end, map, moves, x, y);
        System.out.println(result2);
    }

    private static Map<Character, List<Point>> prepareMap(final List<String> input, final int rows) {
        return EntryStream.of(input)
                .skip(1)
                .limit(rows + 1)
                .mapValues(line -> line.replace("#", ""))
                .flatMapValues(line -> EntryStream.of(Chars.asList(line.toCharArray())).filterValues(c -> c >= '<'))
                .mapToKey((y, entry) -> new Point(entry.getKey(), y - 1))
                .mapValues(Map.Entry::getValue)
                .mapKeyValue(Map::entry)
                .mapToEntry(Map.Entry::getValue, Map.Entry::getKey)
                .grouping();
    }

    private static Set<Point> preparePossibleMoves(final int x, final int y, final Point start, final Point end) {
        return IntStreamEx.rangeClosed(0, x)
                .boxed()
                .mapToEntry(Function.identity(), i -> IntStreamEx.rangeClosed(0, y))
                .flatMapValues(IntStreamEx::boxed)
                .mapKeyValue(Point::new)
                .append(start, end)
                .toSet();
    }

    private static int move(final Point start,
                            final Point end,
                            final Map<Character, List<Point>> map,
                            final Set<Point> allMoves,
                            final int x,
                            final int y) {
        int i = 0;
        Set<Point> states = Sets.newHashSet(start);
        while (states.stream().noneMatch(e -> e.equals(end))) {
            i++;
            map.forEach((type, positions) -> moveBlizzard(type, positions, x, y));
            states = getStates(StreamEx.of(map.values()).flatMap(List::stream).toSet(), allMoves, states);
        }
        return i;
    }

    private static Set<Point> getStates(final Set<Point> blizzards, final Set<Point> moves, final Set<Point> states) {
        final var currentMoves = Sets.difference(moves, blizzards);
        return StreamEx.of(states).flatMap(state -> nextMoves(state, currentMoves)).toSet();
    }

    private static void moveBlizzard(final char type, final List<Point> blizzards, final int x, final int y) {
        switch (type) {
            case '^' -> blizzards.forEach(p -> p.translate(0, p.y > 0 ? -1 : y));
            case 'v' -> blizzards.forEach(p -> p.translate(0, p.y < y ? 1 : -y));
            case '<' -> blizzards.forEach(p -> p.translate(p.x > 0 ? -1 : x, 0));
            case '>' -> blizzards.forEach(p -> p.translate(p.x < x ? 1 : -x, 0));
        }
    }

    private static Stream<Point> nextMoves(final Point state, final Set<Point> moves) {
        return StreamEx.of(DELTAS).map(d -> new Point(d.x + state.x, d.y + state.y)).filter(moves::contains);
    }
}
