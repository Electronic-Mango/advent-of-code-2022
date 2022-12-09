package aoc2022.day9;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import aoc2022.input.InputLoader;

public final class Solution {
    public static void main(final String[] args) {
        final var movements = InputLoader.readLines("day9").stream()
                .flatMap(move -> StreamEx.constant(move.charAt(0), NumberUtils.toInt(StringUtils.getDigits(move))))
                .toList();
        moveRope(movements, 2);
        moveRope(movements, 10);
    }

    private static void moveRope(final List<Character> moves, final int numberOfKnots) {
        final var knots = StreamEx.generate(Point::new).limit(numberOfKnots)
                .collect(Collectors.toCollection(ArrayDeque::new));
        final var tailPositions = Sets.newHashSet(knots.getLast().clone());
        for (final var direction : moves) {
            moveHead(knots.getFirst(), direction);
            StreamEx.of(knots).forPairs(Solution::moveKnotToTarget);
            tailPositions.add(knots.getLast().clone());
        }
        System.out.println(tailPositions.size());
    }

    private static void moveHead(final Point point, final char direction) {
        switch (direction) {
            case 'R' -> point.x += 1;
            case 'L' -> point.x += -1;
            case 'U' -> point.y += 1;
            case 'D' -> point.y += -1;
        }
    }

    private static void moveKnotToTarget(final Point target, final Point knot) {
        if (target.distance(knot) >= 2) {
            knot.translate(Integer.compare(target.x, knot.x), Integer.compare(target.y, knot.y));
        }
    }
}
