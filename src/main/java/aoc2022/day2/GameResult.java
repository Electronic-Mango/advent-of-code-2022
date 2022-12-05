package aoc2022.day2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
enum GameResult {
    WIN(6), DRAW(3), LOSE(0);

    private final int score;

    public static GameResult parseResult(final String result) {
        return switch (result) {
            case "Y" -> GameResult.DRAW;
            case "Z" -> GameResult.WIN;
            default -> GameResult.LOSE;
        };
    }
}
