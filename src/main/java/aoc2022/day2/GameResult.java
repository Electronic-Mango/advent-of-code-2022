package aoc2022.day2;

enum GameResult {
    WIN(6), DRAW(3), LOSE(0);

    public final int score;

    GameResult(final int score) {
        this.score = score;
    }

    public static GameResult parseResult(final String result) {
        return switch (result) {
            case "Y" -> GameResult.DRAW;
            case "Z" -> GameResult.WIN;
            default -> GameResult.LOSE;
        };
    }
}
