package aoc2022.day2;

enum GameResult {
    WIN(6), DRAW(3), LOSE(0);

    public final int score;

    GameResult(final int score) {
        this.score = score;
    }
}
