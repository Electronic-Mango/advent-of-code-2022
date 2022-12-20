package aoc2022.day20;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
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
                .toList();
        final var mixedInput = new ArrayList<>(keyedInput);
        IntStreamEx.range(mixes).forEach(mix -> mixInput(keyedInput, mixedInput));
        final var zeroIndex = StreamEx.of(mixedInput).map(Number::value).indexOf(value -> value == 0).orElseThrow();
        return LongStreamEx.rangeClosed(1000, 3000, 1000)
                .map(i -> i + zeroIndex)
                .mapToInt(i -> Math.floorMod(i, input.size()))
                .mapToObj(mixedInput::get)
                .mapToLong(Number::value)
                .sum();
    }

    private static void mixInput(final List<Number> original, final List<Number> mixed) {
        for (final var originalNumber : original) {
            final var currentIndex = mixed.indexOf(originalNumber);
            final var number = mixed.remove(currentIndex);
            final var newIndex = Math.floorMod(currentIndex + number.value(), mixed.size());
            mixed.add(newIndex, number);
        }
    }

    private record Number(int originalIndex, long value) { }
}
