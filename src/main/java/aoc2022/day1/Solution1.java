package aoc2022.day1;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution1 {
    private static final String ELF_SEPARATOR = System.lineSeparator() + System.lineSeparator();

    public static void main(final String[] args) {
        final var input = InputLoader.read("day1");
        final var result = Splitter.on(ELF_SEPARATOR).splitToStream(input)
                .mapToInt(elf -> elf.lines().mapToInt(NumberUtils::toInt).sum())
                .max()
                .orElseThrow();
        System.out.println(result);
    }
}
