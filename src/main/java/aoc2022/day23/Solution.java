package aoc2022.day23;

import aoc2022.input.InputLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Solution {
    private static final Set<Translation> DELTAS = Set.of(
            new Translation(-1, -1), new Translation(0, -1), new Translation(1, -1),
            new Translation(-1, 0), new Translation(1, 0),
            new Translation(-1, 1), new Translation(0, 1), new Translation(1, 1));
    private static final List<Move> POSSIBLE_MOVES = List.of(
            new Move(new Translation(0, -1), (current, adjacent) -> adjacent.y != current.y - 1),  // north
            new Move(new Translation(0, 1), (current, adjacent) -> adjacent.y != current.y + 1),   // south
            new Move(new Translation(-1, 0), (current, adjacent) -> adjacent.x != current.x - 1),  // west
            new Move(new Translation(1, 0), (current, adjacent) -> adjacent.x != current.x + 1));  // east
    private static final Translation STILL = new Translation(0, 0);

    public static void main(final String[] args) {
        final var elves = EntryStream.of(InputLoader.readLines("day23"))
                .flatMapValues(line -> EntryStream.of(line.chars().boxed().toList()).filterValues(c -> c == '#').keys())
                .mapKeyValue((y, x) -> new Point(x, y))
                .toSet();

        final var result1 = part1(Sets.newHashSet(elves), Lists.newArrayList(POSSIBLE_MOVES));
        System.out.println(result1);

        final var result2 = part2(Sets.newHashSet(elves), Lists.newArrayList(POSSIBLE_MOVES));
        System.out.println(result2);
    }

    private static int part1(final Set<Point> elves, final List<Move> moves) {
        IntStreamEx.range(10).forEach(iteration -> move(elves, moves));
        final var x = elves.stream().mapToInt(elf -> elf.x).summaryStatistics();
        final var y = elves.stream().mapToInt(elf -> elf.y).summaryStatistics();
        return (x.getMax() - x.getMin() + 1) * (y.getMax() - y.getMin() + 1) - elves.size();
    }

    private static int part2(final Set<Point> elves, final List<Move> moves) {
        return IntStreamEx.iterate(1, Math::incrementExact)
                .boxed()
                .mapToEntry(Function.identity(), iteration -> move(elves, moves))
                .findFirst(Map.Entry::getValue)
                .orElseThrow()
                .getKey();
    }

    private static boolean move(final Set<Point> elves, final List<Move> moves) {
        final var nextMoves = nextMoves(elves, moves);
        elves.clear();
        EntryStream.of(nextMoves)
                .flatMapKeyValue((next, current) -> current.size() == 1 ? Stream.of(next) : current.stream())
                .forEach(elves::add);
        Collections.rotate(moves, -1);
        return EntryStream.of(nextMoves).flatMapValues(List::stream).allMatch(Point::equals);
    }

    private static Map<Point, List<Point>> nextMoves(final Set<Point> elves, final List<Move> moves) {
        return StreamEx.of(elves)
                .mapToEntry(Function.identity(), Function.identity())
                .mapKeys(elf -> getAdjacent(elf, elves))
                .mapToKey((adjacent, current) -> getMove(adjacent, current, moves))
                .mapToKey((translation, current) -> new Point(current.x + translation.dx, current.y + translation.dy))
                .grouping();
    }

    private static Set<Point> getAdjacent(final Point elf, final Set<Point> elves) {
        return StreamEx.of(DELTAS).map(d -> new Point(elf.x + d.dx, elf.y + d.dy)).filter(elves::contains).toSet();
    }

    private static Translation getMove(final Set<Point> allAdjacent, final Point current, final List<Move> moves) {
        return allAdjacent.isEmpty() ? STILL : StreamEx.of(moves)
                .findFirst(move -> StreamEx.of(allAdjacent).allMatch(adjacent -> move.fits.test(current, adjacent)))
                .map(Move::translation)
                .orElse(STILL);
    }

    private record Move(Translation translation, BiPredicate<Point, Point> fits) { }

    private record Translation(int dx, int dy) { }
}
