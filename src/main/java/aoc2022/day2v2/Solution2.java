package aoc2022.day2v2;

import aoc2022.util.Input;

public final class Solution2 {
    private static final String INPUT = "src/main/resources/inputs/day2/input";
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var result = Input.readLines(INPUT).stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .mapToInt(Solution2::calculateResult)
                .sum();
        System.out.println(result);
    }

    private static int calculateResult(final String[] shapes) {
        final var opponentShape = shapes[0].charAt(0) - 'A';
        final var result = shapes[1].charAt(0) - 'X';
        final var myShapePoints = Math.floorMod(opponentShape + result - 1, 3) + 1;
        final var resultPoints = result * 3;
        return myShapePoints + resultPoints;
    }
}
