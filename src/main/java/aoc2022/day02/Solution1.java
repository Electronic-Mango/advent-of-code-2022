package aoc2022.day02;

import aoc2022.input.InputLoader;

import java.util.Arrays;
import java.util.stream.Stream;

public final class Solution1 {
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var result = InputLoader.readLines("day2").stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .map(Arrays::stream)
                .map(shapes -> shapes.map(Shape::parseShape))
                .map(Stream::toList)
                .mapToInt(shapes -> calculateScore(shapes.get(0), shapes.get(1)))
                .sum();
        System.out.println(result);
    }

    public static int calculateScore(final Shape opponent, final Shape mine) {
        return GameStates.getResult(opponent, mine).getScore() + mine.getScore();
    }
}
