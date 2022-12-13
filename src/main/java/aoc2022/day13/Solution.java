package aoc2022.day13;

import aoc2022.input.InputLoader;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.read("day13");
        final var packets = StreamEx.split(input, System.lineSeparator() + System.lineSeparator())
                .map(packetPair -> packetPair.lines()
                        .map(Solution::parseCharQueue)
                        .map(Solution::parsePacketTree)
                        .toList())
                .map(Pair::fromCollection)
                .toList();

        final var result1 = EntryStream.of(packets)
                .filterValues(packetPair -> compare(packetPair.getValue0(), packetPair.getValue1()) < 0)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(i -> i + 1)
                .sum();
        System.out.println(result1);

        final var dividerPacket2 = new ArrayMultiTreeNode<Integer>(null);
        dividerPacket2.add(new ArrayMultiTreeNode<>(2));

        final var dividerPacket6 = new ArrayMultiTreeNode<Integer>(null);
        dividerPacket6.add(new ArrayMultiTreeNode<>(6));

        final var sortedPackets = StreamEx.of(packets)
                .flatMap(pair -> Stream.of(pair.getValue0(), pair.getValue1()))
                .append(dividerPacket2, dividerPacket6)
                .sorted(Solution::compare)
                .toList();
        final var result2 = EntryStream.of(sortedPackets)
                .filterValues(packet -> packet == dividerPacket2 || packet == dividerPacket6)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(i -> i + 1)
                .reduce(Math::multiplyExact)
                .orElseThrow();
        System.out.println(result2);
    }

    private static Deque<String> parseCharQueue(final String raw) {
        return raw.chars()
                .limit(raw.length() - 1)
                .skip(1)
                .mapToObj(Character::toString)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private static ArrayMultiTreeNode<Integer> parsePacketTree(final Deque<String> characters) {
        final var node = new ArrayMultiTreeNode<Integer>(null);
        while (!characters.isEmpty()) {
            var symbol = characters.pollFirst();
            if (StringUtils.isNumeric(symbol)) {
                final var symbolBuilder = new StringBuilder();
                symbolBuilder.append(symbol);
                while (!characters.isEmpty() && StringUtils.isNumeric(characters.getFirst())) {
                    symbolBuilder.append(characters.pollFirst());
                }
                final var numberNode = new ArrayMultiTreeNode<>(Integer.parseInt(symbolBuilder.toString()));
                node.add(numberNode);
            } else if (symbol.contains("[")) {
                node.add(parsePacketTree(characters));
            } else if (symbol.contains("]")) {
                break;
            }
        }
        return node;
    }

    private static int compare(final TreeNode<Integer> left, final TreeNode<Integer> right) {
        final var leftValue = left.data();
        final var rightValue = right.data();
        if (leftValue != null && rightValue != null) {
            return leftValue - rightValue;
        } else if (leftValue != null) {
            final var wrappedNode = new ArrayMultiTreeNode<Integer>(null);
            wrappedNode.add(new ArrayMultiTreeNode<>(leftValue));
            return compare(wrappedNode, right);
        } else if (rightValue != null) {
            final var wrappedNode = new ArrayMultiTreeNode<Integer>(null);
            wrappedNode.add(new ArrayMultiTreeNode<>(rightValue));
            return compare(left, wrappedNode);
        } else {
            final var maxSize = Math.min(left.subtrees().size(), right.subtrees().size());
            for (int i = 0; i < maxSize; ++i) {
                final var leftSubTree = left.subtrees().stream().skip(i).findFirst().orElseThrow();
                final var rightSubTree = right.subtrees().stream().skip(i).findFirst().orElseThrow();
                final var subPacketsEqual = compare(leftSubTree, rightSubTree);
                if (subPacketsEqual != 0) {
                    return subPacketsEqual;
                }
            }
            return left.subtrees().size() - right.subtrees().size();
        }
    }
}
