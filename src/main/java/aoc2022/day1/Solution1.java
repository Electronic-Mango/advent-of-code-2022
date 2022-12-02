package aoc2022.day1;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution1 {
    private static final String ELF_SEPARATOR = System.lineSeparator() + System.lineSeparator();
    private static final String FOOD_SEPARATOR = System.lineSeparator();

    public static void main(final String[] args) {
        final var input = InputLoader.read("day1");
        final var result = Arrays.stream(input.split(ELF_SEPARATOR))
                .map(elf -> elf.split(FOOD_SEPARATOR))
                .map(Arrays::stream)
                .map(elf -> elf.mapToInt(NumberUtils::toInt))
                .mapToInt(IntStream::sum)
                .max()
                .orElse(0);
        System.out.println(result);
    }
}
