package aoc2022.day8;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import lombok.RequiredArgsConstructor;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import aoc2022.input.InputLoader;

public final class Solution {
    public static void main(final String[] args) {
        final var grid = InputLoader.readLines("day8").stream().map(String::toCharArray).map(Chars::asList).toList();
        final var trees = IntStreamEx.range(grid.size()).flatMapToObj(row -> prepareTreesRow(grid, row)).toList();
        final var visibleTrees = trees.stream().filter(Tree::isVisible).count();
        System.out.println(visibleTrees);
        final var maxScenicScore = trees.stream().mapToLong(Tree::scenicScore).max().orElseThrow();
        System.out.println(maxScenicScore);
    }

    private static Stream<Tree> prepareTreesRow(final List<List<Character>> grid, final int row) {
        return IntStreamEx.range(grid.get(row).size()).mapToObj(column -> prepareTree(grid, row, column));
    }

    private static Tree prepareTree(final List<List<Character>> grid, final int row, final int column) {
        final var left = Lists.reverse(grid.get(row).subList(0, column));
        final var right = grid.get(row).subList(column + 1, grid.size());
        final var up = Lists.reverse(IntStreamEx.range(row).mapToObj(i -> grid.get(i).get(column)).toList());
        final var down = IntStreamEx.range(row + 1, grid.size()).mapToObj(i -> grid.get(i).get(column)).toList();
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
