package aoc2022.day2v2;

import java.util.Arrays;

import aoc2022.util.Input;

public final class Solution1 {
    private static final String INPUT = "src/main/resources/inputs/day2/input";
    private static final String SHAPE_SEPARATOR = " ";

    public static void main(final String[] args) {
        final var result = Input.readLines(INPUT).stream()
                .map(shapes -> shapes.split(SHAPE_SEPARATOR))
                .peek(shapes -> System.out.printf(Arrays.toString(shapes) + " "))
                .mapToInt(Solution1::calculateResult)
                .peek(System.out::println)
                .sum();
        System.out.println(result);
    }

    private static int calculateResult(final String[] shapes) {
        final var opponentShape = shapes[0].charAt(0) - 'A';
        final var myShape = shapes[1].charAt(0) - 'X';
        final var myShapePoints = myShape + 1;
        final var resultPoints = Math.floorMod(myShape - opponentShape + 1, 3) * 3;
        return myShapePoints + resultPoints;
    }
}
