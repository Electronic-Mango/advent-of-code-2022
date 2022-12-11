package aoc2022.day03;

import aoc2022.input.InputLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import one.util.streamex.MoreCollectors;

import java.util.Collection;

public final class Solution2 {
    private static final int GROUP_SIZE = 3;

    public static void main(final String[] args) {
        final var result = Lists.partition(InputLoader.readLines("day3"), GROUP_SIZE).stream()
                .map(Collection::stream)
                .map(contents -> contents.map(String::toCharArray))
                .map(contents -> contents.map(Chars::asList))
                .map(contents -> contents.collect(MoreCollectors.intersecting()))
                .map(Iterables::getOnlyElement)
                .mapToInt(commonItem -> commonItem - (commonItem < 'a' ? 'A' - 27 : 'a' - 1))
                .sum();
        System.out.println(result);
    }
}
