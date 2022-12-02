package aoc2022.day2;

import aoc2022.util.Input;

public final class Solution2 {
    private static final String INPUT = "src/main/resources/inputs/day2/input";
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var result = Input.readLines(INPUT).stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .mapToInt(Solution2::evaluateScore)
                .sum();
        System.out.println(result);
    }

    private static int evaluateScore(final String[] input) {
        final var opponent = Shape.parseShape(input[0]);
        final var result = GameResult.parseResult(input[1]);
        final var mine = GameStates.getMyShape(opponent, result);
        return result.score + mine.score;
    }
}
