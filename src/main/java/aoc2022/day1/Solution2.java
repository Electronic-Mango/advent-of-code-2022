package aoc2022.day1;

import java.util.Comparator;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution2 {
    private static final String ELF_SEPARATOR = System.lineSeparator() + System.lineSeparator();
    private static final int ELVES_TO_CALCULATE = 3;

    public static void main(final String[] args) {
        final var input = InputLoader.read("day1");
        final var result = Splitter.on(ELF_SEPARATOR).splitToStream(input)
                .map(elf -> elf.lines().mapToInt(NumberUtils::toInt).sum())
                .sorted(Comparator.reverseOrder())
                .limit(ELVES_TO_CALCULATE)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println(result);
    }
}
