package ayaviri.project_one;

import java.util.Optional;

public class GameResult {
    private final boolean won;
    private final Optional<String> secretFlag;
    private final Optional<String> errorMessage;

    private GameResult(boolean won, Optional<String> secretFlag, Optional<String> errorMessage) {
        this.won = won;
        this.secretFlag = secretFlag;
        this.errorMessage = errorMessage;
    }

    public static GameResult ofSuccess(String secretFlag) {
        return new GameResult(true, Optional.of(secretFlag), Optional.empty());
    }

    public static GameResult ofFailure(String message) {
        return new GameResult(false , Optional.empty(), Optional.of(message));
    }

    // SHOULD ONLY EVER BE CALLED IF THE TURN RESULT REPRESENTS AN END OF GAME
    public static GameResult fromTurnResult(TurnResult turnResult) {
        return turnResult.gameResult().get();
    }

    public boolean didWin() {
        return this.won;
    }

    public Optional<String> secretFlag() {
        return this.secretFlag;
    }

    public Optional<String> errorMessage() {
        return this.errorMessage;
    }
}
