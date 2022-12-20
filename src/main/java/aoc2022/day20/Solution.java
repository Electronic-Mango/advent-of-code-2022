package aoc2022.day20;

import aoc2022.input.InputLoader;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.javatuples.Pair;

import java.util.List;
import java.util.stream.Stream;

public final class Solution {
    public static void main(String[] args) {
        final var input = EntryStream.of(InputLoader.readLines("day20", "input"))
                .mapValues(Long::parseLong)
                .mapKeyValue(Pair::with)
                .toList();

        final var result1 = solve(input, 1, 1);
        System.out.println(result1);

        final var result2 = solve(input, 811589153, 10);
        System.out.println(result2);
    }

    private static long solve(final List<Pair<Integer, Long>> input, final int multiplier, final int mixes) {
        final var multipliedInput = StreamEx.of(input)
                .mapToEntry(Pair::getValue0, Pair::getValue1)
                .mapValues(value -> value * multiplier)
                .mapKeyValue(Pair::with)
                .toMutableList();
        for (int mix = 0; mix < mixes; ++mix) {
            for (int i = 0; i < multipliedInput.size(); ++i) {
                for (int j = 0; j < multipliedInput.size(); ++j) {
                    if (multipliedInput.get(j).getValue0() == i) {
                        final var curr = multipliedInput.remove(j);
                        final var newIndex = Math.floorMod(j + curr.getValue1(), multipliedInput.size());
                        multipliedInput.add(newIndex, Pair.with(i, curr.getValue1()));
                        break;
                    }
                }
            }
        }
        final var zeroIndex = multipliedInput.stream().map(Pair::getValue1).toList().indexOf(0L);
        assert zeroIndex >= 0;
        return Stream.of(
                multipliedInput.get(Math.floorMod(zeroIndex + 1000, input.size())),
                multipliedInput.get(Math.floorMod(zeroIndex + 2000, input.size())),
                multipliedInput.get(Math.floorMod(zeroIndex + 3000, input.size()))
        ).mapToLong(Pair::getValue1).sum();
    }
}
