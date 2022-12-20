package aoc2022.day20;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;

import java.util.List;

public final class Solution {
    public static void main(String[] args) {
        final var input = InputLoader.readLines("day20").stream().map(Integer::parseInt).toList();

        final var result1 = solve(input, 1L, 1);
        System.out.println(result1);

        final var result2 = solve(input, 811589153L, 10);
        System.out.println(result2);
    }

    private static long solve(final List<Integer> input, final long multiplier, final int mixes) {
        final var keyedInput = EntryStream.of(input)
                .mapValues(value -> value * multiplier)
                .mapKeyValue(Number::new)
                .toMutableList();
        IntStreamEx.range(mixes).forEach(mix -> mixInput(keyedInput));
        final var zeroIndex = StreamEx.of(keyedInput).map(Number::value).indexOf(value -> value == 0).orElseThrow();
        return LongStreamEx.rangeClosed(1000, 3000, 1000)
                .map(i -> i + zeroIndex)
                .mapToInt(i -> Math.floorMod(i, input.size()))
                .mapToObj(keyedInput::get)
                .mapToLong(Number::value)
                .sum();
    }

    private static void mixInput(final List<Number> input) {
        for (int originalIndex = 0; originalIndex < input.size(); ++originalIndex) {
            final var currentIndex = getCurrentIndex(input, originalIndex);
            final var number = input.remove(currentIndex);
            final var newIndex = Math.floorMod(currentIndex + number.value(), input.size());
            input.add(newIndex, number);
        }
    }

    private static int getCurrentIndex(final List<Number> input, final int originalIndex) {
        return (int) StreamEx.of(input).indexOf(number -> number.originalIndex() == originalIndex).orElseThrow();
    }

    private record Number(int originalIndex, long value) { }
}
