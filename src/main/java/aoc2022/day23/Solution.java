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
import java.util.function.Function;
import java.util.stream.Stream;

public final class Solution {
    private static final Set<Point> ADJACENT = Set.of(new Point(-1, -1), new Point(0, -1), new Point(1, -1),
            new Point(-1, 0), new Point(1, 0), new Point(-1, 1), new Point(0, 1), new Point(1, 1));
    private static final List<Move> MOVES = List.of(new Move(Direction.NORTH, 0, -1), new Move(Direction.SOUTH, 0, 1),
            new Move(Direction.WEST, -1, 0), new Move(Direction.EAST, 1, 0));
    private static final Move STILL = new Move(null, 0, 0);

    public static void main(final String[] args) {
        final var elves = EntryStream.of(InputLoader.readLines("day23"))
                .flatMapValues(line -> EntryStream.of(line.chars().boxed().toList()).filterValues(c -> c == '#').keys())
                .mapKeyValue((y, x) -> new Point(x, y))
                .toSet();

        final var result1 = part1(Sets.newHashSet(elves), Lists.newArrayList(MOVES));
        System.out.println(result1);

        final var result2 = part2(Sets.newHashSet(elves), Lists.newArrayList(MOVES));
        System.out.println(result2);
    }

    private static int part1(final Set<Point> elves, final List<Move> moves) {
        IntStreamEx.range(10).forEach(iteration -> move(elves, moves));
        final var minX = StreamEx.of(elves).minBy(Point::getX).orElseThrow().x;
        final var maxX = StreamEx.of(elves).maxBy(Point::getX).orElseThrow().x;
        final var minY = StreamEx.of(elves).minBy(Point::getY).orElseThrow().y;
        final var maxY = StreamEx.of(elves).maxBy(Point::getY).orElseThrow().y;
        return (Math.abs(maxX - minX + 1) * Math.abs(maxY - minY + 1)) - elves.size();
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
                .mapKeys(elf -> StreamEx.of(ADJACENT)
                        .map(adjacent -> new Point(elf.x + adjacent.x, elf.y + adjacent.y))
                        .filter(elves::contains)
                        .toSet())
                .mapToKey(Solution::emptySides)
                .mapKeys(side -> side.size() == moves.size() ? STILL : StreamEx.of(moves)
                        .filter(move -> side.contains(move.direction))
                        .findFirst()
                        .orElse(STILL))
                .mapToKey((next, current) -> new Point(current.x + next.dx, current.y + next.dy))
                .grouping();
    }

    private static Set<Direction> emptySides(final Set<Point> adjacent, final Point current) {
        final var directionMap = Map.of(
                Direction.NORTH, adjacent.stream().noneMatch(p -> p.y == current.y - 1),
                Direction.SOUTH, adjacent.stream().noneMatch(p -> p.y == current.y + 1),
                Direction.WEST, adjacent.stream().noneMatch(p -> p.x == current.x - 1),
                Direction.EAST, adjacent.stream().noneMatch(p -> p.x == current.x + 1)
        );
        return EntryStream.of(directionMap).filterValues(Boolean::booleanValue).keys().toSet();
    }

    private enum Direction {NORTH, SOUTH, WEST, EAST}

    private record Move(Direction direction, int dx, int dy) {}
}
