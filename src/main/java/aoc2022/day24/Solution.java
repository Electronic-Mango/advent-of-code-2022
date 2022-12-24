package aoc2022.day24;

import aoc2022.input.InputLoader;
import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
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

        final var result1 = move(start, end, map, x, y);
        System.out.println(result1);

        final var result2 = result1 + move(end, start, map, x, y) + move(start, end, map, x, y);
        System.out.println(result2);
    }

    private static BlizzardsMap prepareMap(final List<String> input, final int rows) {
        return EntryStream.of(input)
                .skip(1)
                .limit(rows + 1)
                .mapValues(line -> line.replace("#", ""))
                .flatMapValues(line -> EntryStream.of(Chars.asList(line.toCharArray())).filterValues(c -> c >= '<'))
                .mapToKey((y, entry) -> new Point(entry.getKey(), y - 1))
                .mapValues(Map.Entry::getValue)
                .mapKeyValue(Map::entry)
                .mapToEntry(Map.Entry::getValue, Map.Entry::getKey)
                .grouping(BlizzardsMap::new);
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

    private static int move(final Point start, final Point end, final BlizzardsMap map, final int x, final int y) {
        final var moves = preparePossibleMoves(x, y, start, end);
        var states = Sets.newHashSet(start);
        var i = 0;
        while (states.stream().noneMatch(position -> position.equals(end))) {
            map.forEach((type, positions) -> moveBlizzard(type, positions, x, y));
            states = getStates(StreamEx.of(map.values()).flatMap(List::stream).toSet(), moves, states);
            i++;
        }
        return i;
    }

    private static HashSet<Point> getStates(final Set<Point> taken, final Set<Point> moves, final Set<Point> states) {
        final var currentMoves = Sets.difference(moves, taken);
        return StreamEx.of(states).flatMap(state -> nextMoves(state, currentMoves)).toSetAndThen(HashSet::new);
    }

    private static void moveBlizzard(final char type, final List<Point> positions, final int x, final int y) {
        switch (type) {
            case '^' -> positions.forEach(position -> position.translate(0, position.y > 0 ? -1 : y));
            case 'v' -> positions.forEach(position -> position.translate(0, position.y < y ? 1 : -y));
            case '<' -> positions.forEach(position -> position.translate(position.x > 0 ? -1 : x, 0));
            case '>' -> positions.forEach(position -> position.translate(position.x < x ? 1 : -x, 0));
        }
    }

    private static Stream<Point> nextMoves(final Point state, final Set<Point> moves) {
        return StreamEx.of(DELTAS).map(d -> new Point(d.x + state.x, d.y + state.y)).filter(moves::contains);
    }

    private static final class BlizzardsMap extends HashMap<Character, List<Point>> { }
}
