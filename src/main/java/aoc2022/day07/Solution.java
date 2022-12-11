package aoc2022.day07;

import aoc2022.input.InputLoader;
import com.google.common.collect.Lists;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.LinkedList;
import java.util.stream.IntStream;

public final class Solution {
    private static final int MAX_DIRECTORY_SIZE = 100_000;
    private static final int TOTAL_MEMORY = 70_000_000;
    private static final int REQUIRED_MEMORY = 30_000_000;

    public static void main(final String[] args) {
        final var input = Lists.newLinkedList(InputLoader.readLines("day7"));
        final var root = traverseFileSystem(input);
        final var result1 = fullTreeDataStream(root).filter(size -> size <= MAX_DIRECTORY_SIZE).sum();
        System.out.println(result1);
        final var missingMemory = REQUIRED_MEMORY - (TOTAL_MEMORY - root.data());
        final var result2 = fullTreeDataStream(root).filter(size -> size > missingMemory).min().orElseThrow();
        System.out.println(result2);
    }

    private static TreeNode<Integer> traverseFileSystem(final LinkedList<String> commands) {
        final var node = new ArrayMultiTreeNode<>(0);
        for (var command = ""; command != null && !command.contains("$ cd .."); command = commands.pollFirst()) {
            if (command.contains("$ cd")) {
                node.add(traverseFileSystem(commands));
            } else {
                node.setData(node.data() + NumberUtils.toInt(command.replaceAll("\\D+", "")));
            }
        }
        node.setData(node.data() + node.subtrees().stream().mapToInt(TreeNode::data).sum());
        return node;
    }

    private static IntStream fullTreeDataStream(final TreeNode<Integer> node) {
        return node.preOrdered().stream().mapToInt(TreeNode::data);
    }
}
