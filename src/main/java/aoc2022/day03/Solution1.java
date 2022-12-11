package aoc2022.day03;

import aoc2022.input.InputLoader;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import com.google.common.primitives.Chars;
import one.util.streamex.MoreCollectors;

public final class Solution1 {
    public static void main(final String[] args) {
        final var result = InputLoader.readLines("day3").stream()
                .map(contents -> Splitter.fixedLength(contents.length() / 2).split(contents))
                .map(Streams::stream)
                .map(contents -> contents.map(String::toCharArray))
                .map(contents -> contents.map(Chars::asList))
                .map(contents -> contents.collect(MoreCollectors.intersecting()))
                .map(Iterables::getOnlyElement)
                .mapToInt(commonItem -> commonItem - (commonItem < 'a' ? 'A' - 27 : 'a' - 1))
                .sum();
        System.out.println(result);
    }
}
