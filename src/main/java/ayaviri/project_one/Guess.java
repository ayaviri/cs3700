package ayaviri.project_one;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

// Represents a guess previously submitted by the client to the server
public class Guess {
    private final String word;
    // Represents the presence of the ith character in the secret word. A list of 
    // the same length as the guessed word. For the ith character, a 0 indicates
    // its absences, a 1 its presence but in a different position, a 2 its presence
    // in the correct posotion
    private final List<Integer> marks;

    private Guess(String word, List<Integer> marks) {
        this.word = word;
        this.marks = new ArrayList<>(marks);
    }

    public static Guess fromJson(JsonElement json) {
        JsonObject guessJson = json.getAsJsonObject();
        String word = guessJson.get("word").getAsString();
        List<Integer> marks = new ArrayList<>();
        Iterator<JsonElement> marksIterator = guessJson.get("marks").getAsJsonArray().iterator();

        while (marksIterator.hasNext()) {
            marks.add(marksIterator.next().getAsInt());
        }

        return new Guess(word, marks);
    }

    public String toString() {
        return String.format("{word: %s, marks: %s}", this.word, this.marks);
    }
}
