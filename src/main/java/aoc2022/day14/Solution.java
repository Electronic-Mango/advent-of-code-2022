package aoc2022.day14;

import aoc2022.input.InputLoader;
import lombok.Data;
import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.Range;
import org.javatuples.Pair;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public final class Solution {
    public static void main(final String[] args) {
        final var input = InputLoader.readLines("day14", "testinput");
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
//        System.out.println(sandStartLocation);
        final var grid1 = IntStreamEx.range(maxY)
                .mapToObj(y -> IntStreamEx.generate(() -> ' ')
                        .limit(maxX - minX + 1)
                        .mapToObj(i -> (char) i)
                        .toMutableList())
                .toMutableList();
//        grid1.forEach(System.out::println);
//        System.out.println(grid1.size());
//        System.out.println(grid1.get(0).size());
        for (final var line : lines) {
            for (double x = Math.min(line.x1, line.x2); x <= Math.max(line.x1, line.x2); ++x) {
                for (double y = Math.min(line.y1, line.y2); y <= Math.max(line.y1, line.y2); ++y) {
                    grid1.get((int) y - 1).set((int) x - minX, '#');
                }
            }
        }
//        grid1.forEach(System.out::println);
        var count1 = 0;
        while (true) {
            if (fallNewSand1(grid1, sandStartLocation, 0)) {
                break;
            }
            count1++;
//            System.out.println();
//            grid1.forEach(System.out::println);
        }
        System.out.println(count1);

        lines.add(new Line2D.Double(minX, maxY + 2, maxX, maxY + 2));
//        lines.stream().map(Line2D.Double::getBounds2D).forEach(System.out::println);
        final var grid2 = IntStreamEx.range(maxY + 2)
                .mapToObj(y -> IntStreamEx.generate(() -> ' ')
                        .limit(maxX - minX + 1)
                        .mapToObj(i -> (char) i)
                        .toMutableList())
                .toMutableList();
        for (final var line : lines) {
            for (double x = Math.min(line.x1, line.x2); x <= Math.max(line.x1, line.x2); ++x) {
                for (double y = Math.min(line.y1, line.y2); y <= Math.max(line.y1, line.y2); ++y) {
                    grid2.get((int) y - 1).set((int) x - minX, '#');
                }
            }
        }
//        grid2.forEach(System.out::println);
        var count2 = 0;
        while (true) {
            count2++;
            if (fallNewSand2(grid2, sandStartLocation, 0)) {
                break;
            }
//            System.out.println();
//            grid2.forEach(System.out::println);
        }
        System.out.println(count2);
    }

    private static boolean fallNewSand1(final List<List<Character>> grid, int startX, int startY) {
        while(true) {
            for (int y = startY; y < grid.size(); ++y) {
                final var cell = grid.get(y).get(startX);
                if (cell == ' ' && y == grid.size() - 1) {
                    return true;
                } else if (cell == ' ') {
                    continue;
                } else if (grid.get(y).get(startX - 1) == ' ') {
                    return fallNewSand1(grid, startX - 1, y + 1);
                } else if (grid.get(y).get(startX + 1) == ' ') {
                    return fallNewSand1(grid, startX + 1, y + 1);
                } else {
                    grid.get(y - 1).set(startX, 'o');
                    return false;
                }
            }
            return true;
        }
    }

    private static boolean fallNewSand2(final List<List<Character>> grid, int startX, int startY) {
        while(true) {
            for (int y = startY; y < grid.size(); ++y) {
                final var cell = grid.get(y).get(startX);
                if (cell == ' ' && y == grid.size() - 1) {
                    return true;
                } else if (cell == ' ') {
                    continue;
                } else if (grid.get(y).get(startX - 1) == ' ') {
                    return fallNewSand2(grid, startX - 1, y + 1);
                } else if (grid.get(y).get(startX + 1) == ' ') {
                    return fallNewSand2(grid, startX + 1, y + 1);
                } else if (y > 0) {
                    grid.get(y - 1).set(startX, 'o');
                    return false;
                } else {
                    return true;
                }
            }
            return true;
        }
    }
}

@Data
final class Line {
    private final Range<Integer> x;
    private final Range<Integer> y;

    Line(final Pair<Point, Point> span) {
        x = Range.between(span.getValue0().x, span.getValue1().x);
        y = Range.between(span.getValue0().y, span.getValue1().y);
    }
}
