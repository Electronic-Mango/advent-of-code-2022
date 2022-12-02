package aoc2022.day2;

import aoc2022.util.Input;

public final class Solution2 {
    private static final String INPUT = "inputs/day2/input";
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var input = Input.readLines(INPUT);
        final var result = input.stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .mapToInt(Solution2::evaluateScore)
                .sum();
        System.out.println(result);
    }

    private static int evaluateScore(final String[] input) {
        final var opponent = Shape.parseShape(input[0]);
        final var result = parseResult(input[1]);
        final var mine = evaluateShape(opponent, result);
        return result.score + mine.score;
    }

    private static GameResult parseResult(final String result) {
        return switch (result) {
            case "Y" -> GameResult.DRAW;
            case "Z" -> GameResult.WIN;
            default -> GameResult.LOSE;
        };
    }

    private static Shape evaluateShape(final Shape opponent, final GameResult gameResult) {
        return switch (opponent) {
            case ROCK -> switch (gameResult) {
                case WIN -> Shape.PAPER;
                case DRAW -> Shape.ROCK;
                case LOSE -> Shape.SCISSORS;
            };
            case PAPER -> switch (gameResult) {
                case WIN -> Shape.SCISSORS;
                case DRAW -> Shape.PAPER;
                case LOSE -> Shape.ROCK;
            };
            case SCISSORS -> switch (gameResult) {
                case WIN -> Shape.ROCK;
                case DRAW -> Shape.SCISSORS;
                case LOSE -> Shape.PAPER;
            };
        };
    }
}
