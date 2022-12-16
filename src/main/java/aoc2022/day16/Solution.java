package aoc2022.day16;

import aoc2022.input.InputLoader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;
import org.paukov.combinatorics3.Generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Solution {
    private static final Pattern VALVE_PATTERN = Pattern.compile("Valve ([A-Z][A-Z]).+rate=(\\d+);.*valves? (.+)");
    private static final Map<String, Integer> RATES = new HashMap<>();
    private static final Table<String, String, Integer> DISTANCES = HashBasedTable.create();
    private static final String INITIAL_VALVE = "AA";
    private static final int MINUTES_ALONE = 30;
    private static final int MINUTES_TOGETHER = 26;

    public static void main(String[] args) {
        VALVE_PATTERN.matcher(InputLoader.read("day16")).results().forEach(match -> {
            RATES.put(match.group(1), NumberUtils.toInt(match.group(2)));
            StreamEx.split(match.group(3), ", ").forEach(next -> DISTANCES.put(match.group(1), next, 1));
        });
        Generator.permutation(RATES.keySet()).withRepetitions(3).forEach(Solution::populateDistances);
        EntryStream.of(Map.copyOf(RATES)).filterValues(d -> d == 0).keys().forEach(RATES::remove);

        System.out.println(search(MINUTES_ALONE, INITIAL_VALVE, RATES.keySet(), false));
        System.out.println(search(MINUTES_TOGETHER, INITIAL_VALVE, RATES.keySet(), true));
    }

    private static void populateDistances(final List<String> permutation) {
        final var start = permutation.get(0);
        final var end = permutation.get(1);
        final var through = permutation.get(2);
        final var directDistance = distance(start, end);
        final var indirectDistance = distance(end, through) + distance(through, start);
        DISTANCES.put(start, end, Math.min(directDistance, indirectDistance));
    }

    private static int search(final int time, final String current, final Set<String> next, final boolean together) {
        return StreamEx.of(next)
                .unordered()
                .parallel()
                .filter(valve -> distance(current, valve) < time)
                .mapToEntry(Function.identity(), Function.identity())
                .mapKeys(valve -> RATES.get(valve) * timeLeft(time, current, valve))
                .mapValues(valve -> search(timeLeft(time, current, valve), valve, remove(next, valve), together))
                .mapKeyValue(Math::addExact)
                .mapToInt(Integer::valueOf)
                .append(together ? search(MINUTES_TOGETHER, INITIAL_VALVE, next, false) : 0)
                .max()
                .orElse(0);
    }

    private static int timeLeft(final int time, final String currentValve, final String nextValve) {
        return time - distance(currentValve, nextValve) - 1;
    }

    private static Set<String> remove(final Set<String> set, final String value) {
        return Sets.difference(set, Set.of(value));
    }

    private static int distance(final String a, final String b) {
        return DISTANCES.contains(a, b) ? DISTANCES.get(a, b) : Byte.MAX_VALUE;
    }
}
