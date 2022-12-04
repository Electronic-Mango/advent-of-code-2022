package aoc2022.day4;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    private static final Splitter PAIR_SPLITTER = Splitter.on(",");
    private static final Splitter RANGE_SPLITTER = Splitter.on("-");

    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day4");
        final var resultPart1 = countOccurrences(input, (r1, r2) -> r1.encloses(r2) || r2.encloses(r1));
        final var resultPart2 = countOccurrences(input, Range::isConnected);
        System.out.println(resultPart1);
        System.out.println(resultPart2);
    }

    private static long countOccurrences(final List<String> input,
                                         final BiFunction<Range<Integer>, Range<Integer>, Boolean> mapper) {
        return input.stream()
                .map(line -> PAIR_SPLITTER.splitToStream(line)
                        .map(pair -> RANGE_SPLITTER.splitToStream(pair).map(NumberUtils::toInt))
                        .map(Stream::toList)
                        .map(Range::encloseAll))
                .map(Stream::toList)
                .filter(ranges -> StreamEx.ofPairs(ranges, mapper).allMatch(BooleanUtils::isTrue))
                .count();
    }
}
