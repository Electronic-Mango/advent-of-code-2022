package aoc2022.day2;

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
