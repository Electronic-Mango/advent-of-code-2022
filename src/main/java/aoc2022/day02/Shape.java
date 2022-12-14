package aoc2022.day02;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
enum Shape {
    ROCK(1),
    PAPER(2),
    SCISSORS(3);

    private final int score;

    public static Shape parseShape(final String shape) {
        return switch (shape) {
            case "B", "Y" -> PAPER;
            case "C", "Z" -> SCISSORS;
            default -> ROCK;
        };
    }
}
