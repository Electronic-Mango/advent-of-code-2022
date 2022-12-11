package aoc2022.day08;

import aoc2022.input.InputLoader;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import lombok.RequiredArgsConstructor;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.electronicmango.zipper.Zipper;
import org.paukov.combinatorics3.Generator;

import java.util.List;

public final class Solution {
    public static void main(final String[] args) {
        final var grid = InputLoader.readLines("day8").stream().map(String::toCharArray).map(Chars::asList).toList();
        final var trees = Generator.permutation(IntStreamEx.range(grid.size()).boxed().toList()).withRepetitions(2)
                .stream().map(point -> prepareTree(grid, point.get(0), point.get(1))).toList();
        final var visibleTrees = trees.stream().filter(Tree::isVisible).count();
        System.out.println(visibleTrees);
        final var maxScenicScore = trees.stream().mapToLong(Tree::scenicScore).max().orElseThrow();
        System.out.println(maxScenicScore);
    }

    private static Tree prepareTree(final List<List<Character>> grid, final int row, final int column) {
        final var left = Lists.reverse(grid.get(row).subList(0, column));
        final var right = grid.get(row).subList(column + 1, grid.get(row).size());
        final var up = Lists.reverse(Zipper.zip(grid).get(column).subList(0, row));
        final var down = Zipper.zip(grid).get(column).subList(row + 1, grid.size());
        return new Tree(grid.get(row).get(column), List.of(left, right, up, down));
    }
}

@RequiredArgsConstructor
final class Tree {
    private final char height;
    private final List<List<Character>> sides;

    boolean isVisible() {
        return sides.stream().anyMatch(side -> side.stream().allMatch(other -> other < height));
    }

    long scenicScore() {
        return sides.stream()
                .mapToLong(side -> StreamEx.of(side).takeWhileInclusive(other -> other < height).count())
                .reduce(Math::multiplyExact)
                .orElseThrow();
    }
}
