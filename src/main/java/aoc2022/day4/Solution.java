package aoc2022.day4;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;
import org.javatuples.Pair;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final String PAIR_SEPARATOR = ",";
    private static final String RANGE_SEPARATOR = "-";

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day4");
        final var resultPart1 = countOccurrences(input, Range::overlapFully);
        final var resultPart2 = countOccurrences(input, Range::overlapAtAll);
        System.out.println(resultPart1);
        System.out.println(resultPart2);
    }

    private static long countOccurrences(final List<String> input, final Predicate<Pair<Range, Range>> filter) {
        return input.stream()
                .map(line -> line.split(PAIR_SEPARATOR))
                .map(Arrays::stream)
                .map(pair -> pair.map(range -> range.split(RANGE_SEPARATOR)))
                .map(pair -> pair.map((Arrays::stream)))
                .map(pair -> pair.map(range -> range.map(NumberUtils::toInt)))
                .map(pair -> pair.map(Stream::toList))
                .map(pair -> pair.map(Pair::fromCollection))
                .map(pair -> pair.map(Range::new))
                .map(Stream::toList)
                .map(Pair::fromCollection)
                .filter(filter)
                .count();
    }
}

final class Range {
    private final int start;
    private final int end;

    public Range(final Pair<Integer, Integer> range) {
        this.start = range.getValue0();
        this.end = range.getValue1();
    }

    public static boolean overlapAtAll(final Pair<Range, Range> ranges) {
        final var range1 = ranges.getValue0();
        final var range2 = ranges.getValue1();
        return !(range1.end < range2.start || range1.start > range2.end);
    }

    public static boolean overlapFully(final Pair<Range, Range> ranges) {
        final var range1 = ranges.getValue0();
        final var range2 = ranges.getValue1();
        return range1.contains(range2) || range2.contains(range1);
    }

    private boolean contains(final Range other) {
        return this.start <= other.start && this.end >= other.end;
    }
}
