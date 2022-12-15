package aoc2022.day15;

import aoc2022.input.InputLoader;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import lombok.Data;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

public final class Solution {
    private static final Pattern SENSOR_PATTERN = Pattern.compile(".+=(-?\\d+).+=(-?\\d+).+=(-?\\d+).+=(-?\\d+)");
    private static final int ROW = 2_000_000;
    private static final int MAX = 4_000_000;

    public static void main(final String[] args) {
        final var sensors = StreamEx.of(InputLoader.readLines("day15"))
                .map(SENSOR_PATTERN::matcher)
                .filter(Matcher::find)
                .map(match -> StreamEx.of(match.group(1), match.group(2), match.group(3), match.group(4))
                        .map(Integer::parseInt)
                        .toListAndThen(list -> new Sensor(list.get(0), list.get(1), list.get(2), list.get(3))))
                .toSet();

        final var beacons = sensors.stream().filter(sensor -> sensor.getBeaconRow() == ROW).count();
        final var occupied = getRangeSet(sensors, ROW).asRanges()
                .stream()
                .mapToInt(range -> range.lowerEndpoint() - range.upperEndpoint())
                .map(Math::abs)
                .sum();
        final var result1 = occupied - beacons;
        System.out.println(result1);

        final var result2 = IntStreamEx.range(MAX)
                .boxed()
                .mapToEntry(Function.identity(), y -> getRangeSet(sensors, y))
                .filterValues(ranges -> !ranges.encloses(Range.closed(0, MAX)))
                .mapValues(range -> range.asRanges().iterator().next().upperEndpoint() + 1L)
                .mapKeyValue((y, x) -> (x * MAX) + y)
                .findFirst()
                .orElseThrow();
        System.out.println(result2);
    }

    private static RangeSet<Integer> getRangeSet(final Collection<Sensor> ranges, final int row) {
        return ranges.stream()
                .map(sensor -> sensor.searchedRange(row))
                .filter(Objects::nonNull)
                .collect(Collector.of(TreeRangeSet::create, RangeSet::add, (s1, s2) -> s1));
    }
}

@Data
final class Sensor {
    private final int x;
    private final int y;
    private final int distance;
    private final int beaconRow;

    Sensor(final int px, final int py, final int bx, final int by) {
        x = px;
        y = py;
        beaconRow = by;
        distance = Math.abs(x - bx) + Math.abs(y - by);
    }

    Range<Integer> searchedRange(final int row) {
        final var count = distance - Math.abs(y - row);
        return count < 0 ? null : Range.closed(x - count, x + count);
    }
}
