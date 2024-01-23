package ayaviri.project_one;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.JsonElement;

// Utility functions for collections of guesses
public class Guesses {
    public static List<Guess> fromJson(JsonElement json) {
        List<Guess> guesses = new ArrayList<>();
        Iterator<JsonElement> iterator = json.getAsJsonArray().iterator();

        while (iterator.hasNext()) {
            guesses.add(Guess.fromJson(iterator.next()));
        }

        return guesses;
    }
}
