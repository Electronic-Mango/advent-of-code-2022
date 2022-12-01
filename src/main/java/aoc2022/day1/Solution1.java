package aoc2022.day1;

import aoc2022.util.Input;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Solution1 {
    private static final String INPUT = "src/main/java/aoc2022/day1/input";
    private static final String ELF_SEPARATOR = System.lineSeparator() + System.lineSeparator();
    private static final String FOOD_SEPARATOR = System.lineSeparator();

    public static void main(String[] args) {
        final var input = Input.readFile(INPUT);
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
