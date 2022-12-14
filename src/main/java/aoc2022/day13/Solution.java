package aoc2022.day13;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;
import org.json.JSONArray;

import java.util.Set;

public final class Solution {
    public static void main(String[] args) {
        final var input = InputLoader.read("day13");
        final var packets = StreamEx.split(input, System.lineSeparator() + System.lineSeparator())
                .map(packetPair -> packetPair.lines().map(JSONArray::new).toList())
                .map(Pair::fromCollection)
                .toList();

        final var result1 = EntryStream.of(packets)
                .filterValues(packetPair -> compare(packetPair.getValue0(), packetPair.getValue1()) < 0)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(Math::incrementExact)
                .sum();
        System.out.println(result1);

        final var dividerPackets = Set.of(array("[2]"), array("[6]"));
        final var result2 = StreamEx.of(packets)
                .flatMap(pair -> pair.toList().stream())
                .append(dividerPackets)
                .sorted(Solution::compare)
                .toListAndThen(EntryStream::of)
                .filterValues(dividerPackets::contains)
                .keys()
                .mapToInt(Integer::valueOf)
                .map(Math::incrementExact)
                .reduce(Math::multiplyExact)
                .orElseThrow();
        System.out.println(result2);
    }

    private static int compare(final Object left, final Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left - (Integer) right;
        } else if (left instanceof Integer) {
            return compare(array(left), right);
        } else if (right instanceof Integer) {
            return compare(left, array(right));
        } else {
            final var leftArray = (JSONArray) left;
            final var rightArray = (JSONArray) right;
            final var maxSize = Math.min(leftArray.length(), rightArray.length());
            for (int i = 0; i < maxSize; ++i) {
                final var subPacketsEqual = compare(leftArray.get(i), rightArray.get(i));
                if (subPacketsEqual != 0) {
                    return subPacketsEqual;
                }
            }
            return leftArray.length() - rightArray.length();
        }
    }

    private static JSONArray array(final Object object) {
        return new JSONArray("[" + object + "]");
    }
}
