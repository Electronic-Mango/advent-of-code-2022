package aoc2022.day2;

import java.util.Arrays;
import java.util.stream.Stream;

import aoc2022.util.Input;

public final class Solution1 {
    private static final String INPUT = "inputs/day2/input";
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var input = Input.readLines(INPUT);
        final var result = input.stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .map(Arrays::stream)
                .map(shapes -> shapes.map(Shape::parseShape))
                .map(Stream::toList)
                .mapToInt(shapes -> calculateScore(shapes.get(0), shapes.get(1)))
                .sum();
        System.out.println(result);
    }

    public static int calculateScore(final Shape opponent, final Shape mine) {
        final var matchResult = switch (mine) {
            case ROCK -> switch (opponent) {
                case ROCK -> GameResult.DRAW;
                case PAPER -> GameResult.LOSE;
                case SCISSORS -> GameResult.WIN;
            };
            case PAPER -> switch (opponent) {
                case ROCK -> GameResult.WIN;
                case PAPER -> GameResult.DRAW;
                case SCISSORS -> GameResult.LOSE;
            };
            case SCISSORS -> switch (opponent) {
                case ROCK -> GameResult.LOSE;
                case PAPER -> GameResult.WIN;
                case SCISSORS -> GameResult.DRAW;
            };
        };
        return matchResult.score + mine.score;
    }
}
