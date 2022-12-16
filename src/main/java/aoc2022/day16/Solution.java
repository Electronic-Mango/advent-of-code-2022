package aoc2022.day16;

import aoc2022.input.InputLoader;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import lombok.Data;
import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.math.NumberUtils;
import org.paukov.combinatorics3.Generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Solution {
    private static final int MINUTES_ALONE = 30;
    private static final int MINUTES_TOGETHER = 26;
    private static final String INITIAL_VALVE = "AA";
    private static final Pattern VALVE_PATTERN =
            Pattern.compile("^Valve ([A-Z][A-Z]) .+rate=(\\d+);.*valves? ([A-Z ,]+)$");
    private static final Set<String> V = new HashSet<>();
    private static final Map<String, Long> F = new HashMap<>();
    private static final Table<String, String, Long> D = HashBasedTable.create();

    public static void main(String[] args) {
        final var ts = System.nanoTime();
        InputLoader.readLines("day16", "testinput")
                .stream()
                .map(VALVE_PATTERN::matcher)
                .flatMap(Matcher::results)
                .map(r -> new Input(r.group(1), r.group(2), r.group(3)))
                .forEach(input -> {
                    V.add(input.getName());
                    F.put(input.getName(), input.getRate());
                    input.getTargets().forEach(t -> D.put(t, input.getName(), 1L));
                });
        EntryStream.of(new HashMap<>(F)).filterValues(d -> d == 0).keys().forEach(F::remove);
        final var count = new AtomicInteger(0);
        Generator.permutation(V).withRepetitions(3).forEach(p -> {
            final var k = p.get(2);
            final var i = p.get(1);
            final var j = p.get(0);
            final var v1 = D.contains(i, j) ? D.get(i, j) : Integer.MAX_VALUE;
            final var v2 = D.contains(i, k) ? D.get(i, k) : Integer.MAX_VALUE;
            final var v3 = D.contains(k, j) ? D.get(k, j) : Integer.MAX_VALUE;
            final var v = Math.min(v1, v2 + v3);
            D.put(j, i, v);
            count.incrementAndGet();
        });
        System.out.println(search(MINUTES_ALONE, INITIAL_VALVE, F.keySet(), false));
        System.out.println(search(MINUTES_TOGETHER, INITIAL_VALVE, F.keySet(), true));
        final var te = System.nanoTime();
        System.out.println(te - ts);
    }

    private static long search(final long t, final String u, final Set<String> vs, final boolean together) {
        return StreamEx.of(vs)
                .parallel()
                .filter(v -> D.get(u, v) < t)
                .mapToLong(v -> nextIteration(v, t - D.get(u, v) - 1, Sets.newHashSet(vs), together))
                .append(together ? search(MINUTES_TOGETHER, INITIAL_VALVE, vs, false) : 0)
                .max()
                .orElse(0);
    }

    private static long nextIteration(final String v, final long tr, final Set<String> vs, final boolean together) {
        vs.remove(v);
        return F.get(v) * tr + search(tr, v, vs, together);
    }

    @Data
    private static final class Input {
        private final String name;
        private final long rate;
        private final List<String> targets;

        Input(final String name, final String rate, final String targets) {
            this.name = name;
            this.rate = NumberUtils.toLong(rate);
            this.targets = StreamEx.split(targets, ", ").toList();
        }
    }
}
