package ayaviri.project_one;

import java.util.Random;
import java.util.List;

public class Player {
    // Represents the possible lists of words the server 
    // can choose from
    private final List<String> wordList;
    private final Random random;

    public Player(List<String> wordList) {
        this.wordList = wordList;
        this.random = new Random();
    }

    public String guess(List<Guess> guessHistory) {
        String guess = this.removeRandom();

        return guess;
    }

    private String removeRandom() {
        int index = this.random.nextInt(this.wordList.size());
        String removed = this.wordList.remove(index);

        return removed;
    }
}
