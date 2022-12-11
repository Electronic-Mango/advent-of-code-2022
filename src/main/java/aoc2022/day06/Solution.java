package aoc2022.day06;

import aoc2022.input.InputLoader;
import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Set;

public final class Solution {
    private static final int START_OF_PACKET_SIZE_1 = 4;
    private static final int START_OF_PACKET_SIZE_2 = 14;

    public static void main(final String[] args) {
        final var buffer = InputLoader.read("day6").strip().chars().boxed().toList();
        final var startPacket1 = calculateStartOfPacketIndex(buffer, START_OF_PACKET_SIZE_1);
        final var startPacket2 = calculateStartOfPacketIndex(buffer, START_OF_PACKET_SIZE_2);
        System.out.println(startPacket1);
        System.out.println(startPacket2);
    }

    private static long calculateStartOfPacketIndex(final List<Integer> buffer, final int startOfPacketSize) {
        return StreamEx.ofSubLists(buffer, startOfPacketSize, 1)
                .map(Set::copyOf)
                .map(Set::size)
                .indexOf(size -> size == startOfPacketSize)
                .orElseThrow() + startOfPacketSize;
    }
}
