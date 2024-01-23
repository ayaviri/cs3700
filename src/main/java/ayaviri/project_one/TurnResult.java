package ayaviri.project_one;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class TurnResult {
    private boolean gameOver;
    private boolean error;
    private Optional<GameResult> gameResult;
    private List<Guess> guessHistory;

    private TurnResult(boolean gameOver, boolean error, Optional<GameResult> gameResult, List<Guess> guessHistory) {
        this.gameOver = gameOver;
        this.error = error;
        this.gameResult = gameResult;
        this.guessHistory = guessHistory;
    }

    public static TurnResult ofSuccess(String secretFlag) {
        return new TurnResult(true, false, Optional.of(GameResult.ofSuccess(secretFlag)), new ArrayList<>());
    }

    public static TurnResult ofFailure(String message) {
        return new TurnResult(true, true, Optional.of(GameResult.ofFailure(message)), new ArrayList<>());
    }

    public static TurnResult ofRetry(List<Guess> guessHistory) {
        return new TurnResult(false, false, Optional.empty(), new ArrayList<>(guessHistory));
    }

    // Constructs a new TurnResult that represents the start of the game
    public static TurnResult empty() {
        return new TurnResult(false, false, Optional.empty(), new ArrayList<>());
    }

    public boolean gameOver() {
        return this.gameOver;
    }

    public List<Guess> guessHistory() {
        return new ArrayList<>(this.guessHistory);
    }

    public Optional<GameResult> gameResult() {
        return this.gameResult;
    }
}
