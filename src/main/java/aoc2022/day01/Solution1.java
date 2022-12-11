package aoc2022.day01;

import aoc2022.input.InputLoader;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;

public final class Solution1 {
    private static final String ELF_SEPARATOR = System.lineSeparator() + System.lineSeparator();

    public static void main(final String[] args) {
        final var input = InputLoader.read("day1");
        final var result = StreamEx.split(input, ELF_SEPARATOR)
                .mapToInt(elf -> elf.lines().mapToInt(NumberUtils::toInt).sum())
                .max()
                .orElseThrow();
        System.out.println(result);
    }
}
