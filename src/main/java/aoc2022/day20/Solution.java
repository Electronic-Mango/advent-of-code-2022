package aoc2022.day20;

import aoc2022.input.InputLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.IntStreamEx;
import one.util.streamex.LongStreamEx;
import one.util.streamex.StreamEx;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Solution {
    public static void main(String[] args) {
        final var input = InputLoader.readLines("day20").stream().map(Integer::parseInt).toList();

        final var result1 = solve(input, 1L, 1);
        System.out.println(result1);

        final var result2 = solve(input, 811589153L, 10);
        System.out.println(result2);
    }

    private static long solve(final List<Integer> input, final long multiplier, final int mixes) {
        final var originalInput = input.stream().map(value -> value * multiplier).map(Number::new).toList();
        final var mixedInput = new ArrayList<>(originalInput);
        IntStreamEx.range(mixes).forEach(mix -> mixInput(originalInput, mixedInput));
        final var zeroIndex = StreamEx.of(mixedInput).map(Number::getValue).indexOf(value -> value == 0).orElseThrow();
        return LongStreamEx.rangeClosed(1000, 3000, 1000)
                .map(i -> i + zeroIndex)
                .mapToInt(i -> Math.floorMod(i, input.size()))
                .mapToObj(mixedInput::get)
                .mapToLong(Number::getValue)
                .sum();
    }

    private static void mixInput(final List<Number> originalInput, final List<Number> mixedInput) {
        StreamEx.of(originalInput)
                .mapToEntry(mixedInput::indexOf, Function.identity())
                .peekValues(mixedInput::remove)
                .mapToKey((index, number) -> Math.floorMod(index + number.getValue(), mixedInput.size()))
                .forKeyValue(mixedInput::add);
    }

    @Getter(AccessLevel.PRIVATE)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class Number {
        private final long value;
    }
}
