package aoc2022.day14;

import aoc2022.input.InputLoader;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day14");
        final var lines = StreamEx.of(input)
                .map(line -> line.split(" -> "))
                .map(Arrays::asList)
                .flatMap(points -> StreamEx.of(points)
                        .map(point -> point.split(","))
                        .map(Arrays::asList)
                        .map(point -> point.stream().map(Integer::parseInt).toList())
                        .map(point -> new Point(point.get(0), point.get(1)))
                        .pairMap(Line2D.Double::new))
                .toMutableList();
        final var xStats = lines.stream()
                .flatMap(line -> Stream.of(line.x1, line.x2))
                .mapToInt(Double::intValue)
                .summaryStatistics();
        final var minX = xStats.getMin() - 1000;
        final var maxX = xStats.getMax() + 1000;
        final var maxY = lines.stream()
                .flatMap(line -> Stream.of(line.y1, line.y2))
                .mapToInt(Double::intValue)
                .max()
                .orElseThrow();
        final var sandStartLocation = 500 - minX;
        final var grid1 = prepareGrid(lines, maxY, minX, maxX);
        var count1 = 0;
        while (!simulateSand(grid1, sandStartLocation, 0)) {
            count1++;
        }
        System.out.println(count1);

        lines.add(new Line2D.Double(minX, maxY + 2, maxX, maxY + 2));
        final var grid2 = prepareGrid(lines, maxY, minX, maxX);
        var count2 = 0;
        do {
            count2++;
        } while (!simulateSand(grid2, sandStartLocation, 0));
        System.out.println(count2);
    }

    private static List<List<Character>> prepareGrid(final List<Line2D.Double> lines,
                                                     final int maxY,
                                                     final int minX,
                                                     final int maxX) {
        final var grid = IntStreamEx.range(maxY + 2)
                .mapToObj(y -> IntStreamEx.generate(() -> ' ')
                        .limit(maxX - minX + 1)
                        .mapToObj(i -> (char) i)
                        .toMutableList())
                .toMutableList();
        for (final var line : lines) {
            for (double x = Math.min(line.x1, line.x2); x <= Math.max(line.x1, line.x2); ++x) {
                for (double y = Math.min(line.y1, line.y2); y <= Math.max(line.y1, line.y2); ++y) {
                    grid.get((int) y - 1).set((int) x - minX, '#');
                }
            }
        }
        return grid;
    }

    private static boolean simulateSand(final List<List<Character>> grid, int startX, int startY) {
        for (int y = startY; y < grid.size(); ++y) {
            final var cell = grid.get(y).get(startX);
            if (cell == ' ') {
                continue;
            } else if (grid.get(y).get(startX - 1) == ' ') {
                return simulateSand(grid, startX - 1, y + 1);
            } else if (grid.get(y).get(startX + 1) == ' ') {
                return simulateSand(grid, startX + 1, y + 1);
            } else if (y > 0) {
                grid.get(y - 1).set(startX, 'o');
                return false;
            }
            return true;
        }
        return true;
    }
}
