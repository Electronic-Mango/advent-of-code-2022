package aoc2022.day15;

import aoc2022.input.InputLoader;
import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;
import lombok.Data;

import java.awt.Point;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Solution {
    private static final int ROW = 2_000_000;
    private static final int MAX = 4_000_000;
    private static final Pattern SENSOR_PATTERN = Pattern.compile(".+=(-?\\d+).+=(-?\\d+).+=(-?\\d+).+=(-?\\d+)");

    public static void main(final String[] args) {
        final var sensors = InputLoader.readLines("day15").stream()
                .map(SENSOR_PATTERN::matcher)
                .filter(Matcher::find)
                .map(match -> Stream.of(match.group(1), match.group(2), match.group(3), match.group(4))
                        .map(Integer::parseInt)
                        .toList())
                .map(values -> new Sensor(values.get(0), values.get(1), values.get(2), values.get(3)))
                .toList();
        sensors.forEach(System.out::println);
        final var result1 = sensors.stream()
                .map(sensor -> sensor.detectedInRow(ROW))
                .flatMap(Set::stream).distinct().count();
        final var beacons = sensors.stream()
                .map(Sensor::getBeacon)
                .distinct()
                .filter(sensor -> sensor.y == ROW)
                .count();
        System.out.println(result1 - beacons);
        for (int y = 0; y < MAX; ++y) {
            final int finalY = y;
            final var scanned = TreeRangeSet.<Integer>create();
            sensors.stream().map(sensor -> sensor.rangeInRow(finalY)).filter(Objects::nonNull).forEach(scanned::add);
            if (!scanned.encloses(Range.closed(0, MAX))) {
                final var x = scanned.complement()
                        .asDescendingSetOfRanges()
                        .stream()
                        .skip(1)
                        .findFirst()
                        .orElseThrow()
                        .lowerEndpoint() + 1;
                final var xBi = BigInteger.valueOf(x);
                System.out.println(xBi.multiply(BigInteger.valueOf(MAX)).add(BigInteger.valueOf(y)));
                break;
            }
        }
    }
}

@Data
final class Sensor {
    private final Point position;
    private final Point beacon;
    private final int distance;

    Sensor(final int px, final int py, final int bx, final int by) {
        position = new Point(px, py);
        beacon = new Point(bx, by);
        distance = Math.abs(position.x - beacon.x) + Math.abs(position.y - beacon.y);
    }

    int distance() {
        return Math.abs(position.x - beacon.x) + Math.abs(position.y - beacon.y);
    }

    Range<Integer> rangeInRow(final int y) {
        final var dy = Math.abs(position.y - y);
        final var count = distance - dy;
        if (count < 0) {
            return null;
        }
        final var start = position.x - count;
        final var end = position.x + count;
        return Range.closed(start, end);
    }

    Set<Point> detectedInRow(final int y) {
        final var dy = Math.abs(position.y - y);
        final var count = distance - dy;
        if (count < 0) {
            return Collections.emptySet();
        }
        final var start = position.x - count;
        final var end = position.x + count;
        return IntStream.rangeClosed(start, end).mapToObj(x -> new Point(x, y)).collect(Collectors.toSet());
    }
}