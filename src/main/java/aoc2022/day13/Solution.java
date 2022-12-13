package aoc2022.day13;

import aoc2022.input.InputLoader;
import lombok.Data;
import lombok.NoArgsConstructor;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.read("day13");
        final var packets = StreamEx.split(input, System.lineSeparator() + System.lineSeparator())
                .map(packetPair -> packetPair.lines().toList())
                .map(packetPair -> packetPair.stream().map(Solution::parsePackets).toList())
                .map(packetPair -> Pair.of(packetPair.get(0), packetPair.get(1)))
                .toList();

        final var result1 = EntryStream.of(packets)
                .filterValues(packetPair -> packetPair.getLeft().compareTo(packetPair.getRight()) < 0)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(i -> i + 1)
                .sum();
        System.out.println(result1);

        final var dividerPacket2 = new Packet();
        final var dividerPacket2Value = new Packet();
        dividerPacket2Value.setValue(Optional.of(2));
        dividerPacket2.getPackets().add(dividerPacket2Value);

        final var dividerPacket6 = new Packet();
        final var dividerPacket6Value = new Packet();
        dividerPacket6Value.setValue(Optional.of(6));
        dividerPacket6.getPackets().add(dividerPacket6Value);

        final var sortedPackets = StreamEx.of(packets)
                .flatMap(pair -> Stream.of(pair.getLeft(), pair.getRight()))
                .append(dividerPacket2, dividerPacket6)
                .sorted()
                .toList();
        sortedPackets.forEach(System.out::println);
        final var result2 = EntryStream.of(sortedPackets)
                .filterValues(packet -> packet == dividerPacket2 || packet == dividerPacket6)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(i -> i + 1)
                .reduce(Math::multiplyExact);
        System.out.println(result2);
    }

    private static Packet parsePackets(final String rawPacket) {
        final var symbolQueue = rawPacket.chars()
                .limit(rawPacket.length() - 1)
                .skip(1)
                .mapToObj(Character::toString)
                .collect(Collectors.toCollection(LinkedList::new));
        return parsePacket(symbolQueue);
    }

    private static Packet parsePacket(final Deque<String> characters) {
        final var packet = new Packet();
        while (!characters.isEmpty()) {
            var symbol = characters.pollFirst();
            if (StringUtils.isNumeric(symbol)) {
                final var symbolBuilder = new StringBuilder();
                symbolBuilder.append(symbol);
                while (!characters.isEmpty() && StringUtils.isNumeric(characters.getFirst())) {
                    symbolBuilder.append(characters.pollFirst());
                }
                final var numberPacket = new Packet();
                numberPacket.setValue(Optional.of(Integer.parseInt(symbolBuilder.toString())));
                packet.getPackets().add(numberPacket);
            } else if (symbol.contains("[")) {
                packet.getPackets().add(parsePacket(characters));
            } else if (symbol.contains("]")) {
                return packet;
            }
        }
        return packet;
    }
}

@Data
@NoArgsConstructor
final class Packet implements Comparable<Packet> {
    private Optional<Integer> value = Optional.empty();
    private List<Packet> packets = new LinkedList<>();

    @Override
    public int compareTo(final Packet right) {
        final var leftValue = this.getValue();
        final var rightValue = right.getValue();
        if (leftValue.isPresent() && rightValue.isPresent() && !rightValue.get().equals(leftValue.get())) {
            return leftValue.get() - rightValue.get();
        } else if (leftValue.isPresent() && rightValue.isPresent()) {
            return 0;
        } else if (leftValue.isPresent()) {
            final var wrappedValue = new Packet();
            wrappedValue.setValue(leftValue);
            final var wrappedLeft = new Packet();
            wrappedLeft.getPackets().add(wrappedValue);
            return wrappedLeft.compareTo(right);
        } else if (rightValue.isPresent()) {
            final var wrappedValue = new Packet();
            wrappedValue.setValue(rightValue);
            final var wrappedRight = new Packet();
            wrappedRight.getPackets().add(wrappedValue);
            return this.compareTo(wrappedRight);
        } else {
            final var maxSize = Math.min(this.getPackets().size(), right.getPackets().size());
            for (int i = 0; i < maxSize; ++i) {
                final var leftSubpacket = this.getPackets().get(i);
                final var rightSubpacket = right.getPackets().get(i);
                final var subPacketsEqual = leftSubpacket.compareTo(rightSubpacket);
                if (subPacketsEqual != 0) {
                    return subPacketsEqual;
                }
            }
            if (this.getPackets().size() == right.getPackets().size()) {
                return 0;
            } else {
                return this.getPackets().size() - right.getPackets().size();
            }
        }
    }
}