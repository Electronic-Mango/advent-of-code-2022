package aoc2022.day2;

import java.util.Map;

public final class GameStates {
    private static final Map<Shape, Map<Shape, GameResult>> GAME_STATES = Map.of(
            Shape.ROCK, Map.of(
                    Shape.ROCK, GameResult.DRAW,
                    Shape.PAPER, GameResult.WIN,
                    Shape.SCISSORS, GameResult.LOSE
            ),
            Shape.PAPER, Map.of(
                    Shape.ROCK, GameResult.LOSE,
                    Shape.PAPER, GameResult.DRAW,
                    Shape.SCISSORS, GameResult.WIN
            ),
            Shape.SCISSORS, Map.of(
                    Shape.ROCK, GameResult.WIN,
                    Shape.PAPER, GameResult.LOSE,
                    Shape.SCISSORS, GameResult.DRAW
            )
    );

    public static GameResult getResult(final Shape opponent, final Shape mine) {
        return GAME_STATES.get(opponent).get(mine);
    }

    public static Shape getMyShape(final Shape opponent, final GameResult result) {
        return GAME_STATES.get(opponent).entrySet().stream()
                .filter(entry -> entry.getValue().equals(result))
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow();
    }
}
