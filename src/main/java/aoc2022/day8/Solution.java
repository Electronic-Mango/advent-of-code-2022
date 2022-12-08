package aoc2022.day8;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    public static void main(final String[] args) {
        final var treeHeightGrid = InputLoader.readLines("day8").stream()
                .map(line -> line.chars().mapToObj(Character::toString).map(Integer::parseInt).toList())
                .toList();
        final var trees = new ArrayList<Tree>();
        final var rowLength = treeHeightGrid.size();
        final var columnLength = treeHeightGrid.get(0).size();
        for (int row = 1; row < rowLength - 1; ++row) {
            for (int column = 1; column < columnLength - 1; ++column) {
                final var tree = new Tree(row, column, treeHeightGrid.get(column).get(row));
                trees.add(tree);
                checkTreeParameters(tree, treeHeightGrid, rowLength, columnLength);
            }
        }
        var visibleOutsideTrees = (rowLength * 2) + ((columnLength - 2) * 2);
        final var visibleTrees = visibleOutsideTrees + trees.stream()
                .map(Tree::isVisible)
                .filter(BooleanUtils::isTrue)
                .count();
        final var maxScenicScore = trees.stream().mapToInt(Tree::getScenicScore).max().orElseThrow();
        System.out.println(visibleTrees);
        System.out.println(maxScenicScore);
    }

    private static void checkTreeParameters(final Tree tree,
                                            final List<List<Integer>> treeHeightGrid,
                                            final int rowLength,
                                            final int columnLength) {
        var visibleTop = true;
        var scenicScoreTop = 0;
        for (int row = tree.x - 1; row >= 0; --row) {
            scenicScoreTop++;
            final var otherTree = new Tree(row, tree.y, treeHeightGrid.get(tree.y).get(row));
            if (otherTree.height >= tree.height) {
                visibleTop = false;
                break;
            }
        }
        var visibleBottom = true;
        var scenicScoreBottom = 0;
        for (int row = tree.x + 1; row < columnLength; ++row) {
            scenicScoreBottom++;
            final var otherTree = new Tree(row, tree.y, treeHeightGrid.get(tree.y).get(row));
            if (otherTree.height >= tree.height) {
                visibleBottom = false;
                break;
            }
        }
        var visibleRight = true;
        var scenicScoreLeft = 0;
        for (int column = tree.y - 1; column >= 0; --column) {
            scenicScoreLeft++;
            final var otherTree = new Tree(tree.x, column, treeHeightGrid.get(column).get(tree.x));
            if (otherTree.height >= tree.height) {
                visibleRight = false;
                break;
            }
        }
        var visibleLeft = true;
        var scenicScoreRight = 0;
        for (int column = tree.y + 1; column < rowLength; ++column) {
            scenicScoreRight++;
            final var otherTree = new Tree(tree.x, column, treeHeightGrid.get(column).get(tree.x));
            if (otherTree.height >= tree.height) {
                visibleLeft = false;
                break;
            }
        }
        tree.visible = visibleLeft || visibleBottom || visibleRight || visibleTop;
        tree.scenicScore = scenicScoreTop * scenicScoreRight * scenicScoreBottom * scenicScoreLeft;
    }
}

@Getter
final class Tree extends Point {
    final int height;
    boolean visible;
    int scenicScore;


    Tree(final int x, final int y, final int height) {
        super(x, y);
        this.height = height;
        this.visible = true;
        this.scenicScore = 0;
    }
}
