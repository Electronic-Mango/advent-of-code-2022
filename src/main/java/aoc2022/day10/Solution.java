package aoc2022.day10;

import aoc2022.input.InputLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import one.util.streamex.EntryStream;
import one.util.streamex.IntStreamEx;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

public final class Solution {
    public static void main(final String[] args) {
        final var instructions = InputLoader.readLines("day10");
        final var registers = new LinkedList<Integer>();
        registers.add(1);
        for (final var instruction : instructions) {
            registers.add(registers.getLast());
            if (!instruction.equals("noop")) {
                registers.add(registers.getLast() + Integer.parseInt(instruction.split(" ")[1]));
            }
        }
        final var totalStrength = IntStreamEx.rangeClosed(20, 220, 40).map(cycle -> registers.get(cycle) * cycle).sum();
        System.out.println(totalStrength);
        EntryStream.of(registers)
                .mapKeys(cycle -> cycle % 40)
                .mapValues(register -> Range.closed(register - 1, register + 1))
                .mapKeyValue((column, sprite) -> sprite.contains(column))
                .map(visible -> visible ? '█' : ' ')
                .toListAndThen(list -> Lists.partition(list, 40))
                .stream()
                .map(list -> StringUtils.join(list, ""))
                .forEach(System.out::println);
    }
}
