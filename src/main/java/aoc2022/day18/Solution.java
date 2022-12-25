package aoc2022.day18;

import aoc2022.input.InputLoader;
import com.google.common.collect.Sets;
import one.util.streamex.StreamEx;
import org.electronicmango.zipper.Zipper;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Stream;

public final class Solution {
    private static final int CUBE_SIDES = 6;

    public static void main(final String[] args) {
        final var allCubes = InputLoader.readLines("day18")
                .stream()
                .map(line -> StreamEx.split(line, ",").map(Integer::parseInt).toList())
                .collect(Cube.collector());

        System.out.println(part1(allCubes));

        System.out.println(part2(allCubes));
    }

    private static long part1(final Set<Cube> allCubes) {
        return allCubes.stream()
                .mapToLong(cube -> cube.adjacent().stream().filter(allCubes::contains).count())
                .map(adjacent -> CUBE_SIDES - adjacent)
                .sum();
    }

    private static long part2(final Set<Cube> all) {
        final var maxX = getMaxCoordinate(all, Cube::x);
        final var maxY = getMaxCoordinate(all, Cube::y);
        final var maxZ = getMaxCoordinate(all, Cube::z);
        final var checked = new HashSet<Cube>();
        final var flood = new LinkedList<Cube>();
        flood.add(new Cube(0, 0, 0));
        var exposedExternalFaces = 0L;
        while (!flood.isEmpty()) {
            final var floodCube = flood.pop();
            checked.add(floodCube);
            final var adjacent = floodCube.adjacent().stream().filter(validFloodCube(maxX, maxY, maxZ)).toList();
            adjacent.stream()
                    .filter(cube -> Stream.of(all, flood, checked).noneMatch(collection -> collection.contains(cube)))
                    .forEach(flood::add);
            exposedExternalFaces += adjacent.stream().filter(all::contains).count();
        }
        return exposedExternalFaces;
    }

    private static int getMaxCoordinate(final Collection<Cube> allCubes, final ToIntFunction<Cube> extractor) {
        return allCubes.stream().mapToInt(extractor).max().orElseThrow() + 1;
    }

    private static Predicate<Cube> validFloodCube(final int x, final int y, final int z) {
        return cube -> valid(cube.x(), x) && valid(cube.y(), y) && valid(cube.z(), z);
    }

    private static boolean valid(final int coordinate, final int max) {
        return coordinate >= -1 && coordinate <= max;
    }
}

record Cube(int x, int y, int z) {
    private static final Set<List<Integer>> ADJACENT_DELTAS = Set.of(
            List.of(1, 0, 0), List.of(-1, 0, 0),
            List.of(0, 1, 0), List.of(0, -1, 0),
            List.of(0, 0, 1), List.of(0, 0, -1));

    static Collector<List<Integer>, Set<Cube>, Set<Cube>> collector() {
        return Collector.of(HashSet::new, (s, c) -> s.add(new Cube(c.get(0), c.get(1), c.get(2))), Sets::union);
    }

    Set<Cube> adjacent() {
        return StreamEx.of(ADJACENT_DELTAS)
                .map(delta -> Zipper.zip(delta, List.of(x, y, z))
                        .stream()
                        .map(coordinates -> coordinates.stream().mapToInt(Integer::valueOf).sum())
                        .toList())
                .collect(collector());
    }
}
