package aoc2022.day4;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import org.apache.commons.lang3.math.NumberUtils;
import org.javatuples.Pair;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final Splitter PAIR_SPLITTER = Splitter.on(",");
    private static final Splitter RANGE_SPLITTER = Splitter.on("-");

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day4");
        final var resultPart1 = countOccurrences(input, Solution::containsAll);
        final var resultPart2 = countOccurrences(input, Solution::contains);
        System.out.println(resultPart1);
        System.out.println(resultPart2);
    }

    private static long countOccurrences(final List<String> input,
                                         final Predicate<Pair<Range<Integer>, Range<Integer>>> filter) {
        return input.stream()
                .map(PAIR_SPLITTER::splitToStream)
                .map(pair -> pair.map(RANGE_SPLITTER::splitToStream))
                .map(pair -> pair.map(range -> range.map(NumberUtils::toInt)))
                .map(pair -> pair.map(Stream::toList))
                .map(pair -> pair.map(Range::encloseAll))
                .map(Stream::toList)
                .map(Pair::fromCollection)
                .filter(filter)
                .count();
    }

    private static boolean containsAll(final Pair<Range<Integer>, Range<Integer>> ranges) {
        final var range1 = ranges.getValue0();
        final var range2 = ranges.getValue1();
        return range1.encloses(range2) || range2.encloses(range1);
    }

    private static boolean contains(final Pair<Range<Integer>, Range<Integer>> ranges) {
        final var range1 = ranges.getValue0();
        final var range2 = ranges.getValue1();
        return range1.isConnected(range2);
    }
}
