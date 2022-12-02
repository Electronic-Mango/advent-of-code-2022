package aoc2022.day2;

import java.util.Arrays;
import java.util.stream.Stream;

import aoc2022.util.Input;

enum Shape {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    public final int score;

    Shape(final int score) {
        this.score = score;
    }

    public static Shape parseShape(final String shape) {
        return switch (shape) {
            case "B", "Y" -> PAPER;
            case "C", "Z" -> SCISSORS;
            default -> ROCK;
        };
    }
}

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
                .map(shapes -> new Round(shapes.get(0), shapes.get(1)))
                .mapToInt(Round::score)
                .sum();
        System.out.println(result);
    }
}

final class Round {
    private static final int LOSE_SCORE = 0;
    private static final int DRAW_SCORE = 3;
    private static final int VICTORY_SCORE = 6;
    public final Shape opponent;
    public final Shape mine;

    public Round(final Shape opponent, final Shape mine) {
        this.opponent = opponent;
        this.mine = mine;
    }

    public int score() {
        return matchScore() + mine.score;
    }

    private int matchScore() {
        return switch (mine) {
            case ROCK -> switch (opponent) {
                case ROCK -> DRAW_SCORE;
                case PAPER -> LOSE_SCORE;
                case SCISSORS -> VICTORY_SCORE;
            };
            case PAPER -> switch (opponent) {
                case ROCK -> VICTORY_SCORE;
                case PAPER -> DRAW_SCORE;
                case SCISSORS -> LOSE_SCORE;
            };
            case SCISSORS -> switch (opponent) {
                case ROCK -> LOSE_SCORE;
                case PAPER -> VICTORY_SCORE;
                case SCISSORS -> DRAW_SCORE;
            };
        };
    }
}
