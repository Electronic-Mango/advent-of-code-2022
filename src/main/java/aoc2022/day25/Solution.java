package aoc2022.day25;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;

import java.util.Map;

public final class Solution {
    private static final Map<Character, Long> DECODE = Map.of('2', 2L, '1', 1L, '0', 0L, '-', -1L, '=', -2L);
    private static final Map<Long, Character> ENCODE = EntryStream.of(DECODE).invert().toMap();

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day25").stream().mapToLong(Solution::toDec).sum();
        final var result = Solution.fromDec(input);
        System.out.println(result);
    }

    private static long toDec(final String snafu) {
        var result = 0L;
        for (final var digit : snafu.toCharArray()) {
            result = result * 5 + DECODE.get(digit);
        }
        return result;
    }

    private static String fromDec(final long decimal) {
        if (decimal == 0) {
            return "";
        }
        final var reminder = (decimal + 2) % 5 - 2;
        final var quotient = (decimal + 2) / 5;
        return fromDec(quotient) + ENCODE.get(reminder);
    }
}