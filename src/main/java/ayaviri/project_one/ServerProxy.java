package ayaviri.project_one;

import java.net.Socket;
import java.io.IOException;
import java.util.Optional;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

// Represents a proxy with an already-established connection to the 
// Wordle server that can:
// 1) Greet it 
// 2) Play a game with it
// 3) Bid farewell to it
public class ServerProxy {
    // Represents the TCP connection (using JSON) with the server
    private final ServerConnection connection;
    // An ID assigned to the given game. Must be sent in every request to server.
    private String gameId; 

    public ServerProxy(Socket socket) throws IOException {
        this.connection = new ServerConnection(socket);
    }

    // Sends a greeting message to the server using the given username in 
    // the request, receives and sets this proxy's game ID in response
    public void greetServer(String username) throws IOException {
        this.connection.write(this.constructGreeting(username));
        System.out.println("Hello message written");
        Optional<JsonElement> response = this.connection.readElement();
        System.out.println("Start message received");
    
        if (this.greetingResponseIsValid(response)) {
            this.gameId = this.getGameIdFrom(response.get());
        } else {
            // TODO: How should I deal with this ?
            throw new RuntimeException("Invalid greeting response from server");
        }
    }

    public GameResult playGameWith(Player player) throws IOException {
        Optional<JsonElement> response = Optional.empty();
        TurnResult turnResult = TurnResult.empty();

        do {
            this.updateGuessHistory(response, turnResult.guessHistory());
            JsonElement guessJson = this.getGuess(player, turnResult);
            this.connection.write(guessJson);
            response = this.connection.readElement();
            turnResult = this.constructTurnResult(response);
        } while (!turnResult.gameOver());

        return GameResult.fromTurnResult(turnResult);
    }

    // Closes the connection between this proxy and the server
    public void bidFarewellToServer() throws IOException {
        this.connection.close();
    }

    // Constructs the greeting to the server with the given username
    private JsonObject constructGreeting(String username) {
        JsonObject greeting = new JsonObject();
        greeting.addProperty("type", "hello");
        greeting.addProperty("northeastern_username", username);

        return greeting;
    }

    private boolean greetingResponseIsValid(Optional<JsonElement> response) {
        return true;
        // TODO: implement + abstract validity checks for all types of messages
        // this.checkPresence(response);
        // JsonElement presentResponse = response.get();
    }

    private String getGameIdFrom(JsonElement response) {
        return response.getAsJsonObject().get("id").getAsString();
    }

    // Updates the given guess history with the new guess IFF 
    // the response is a retry
    private void updateGuessHistory(Optional<JsonElement> response, List<Guess> guessHistory) {
        if (response.isEmpty()) {
            return;
        }

        JsonObject responseJson = response.get().getAsJsonObject();

        if (responseJson.get("type").equals("retry")) {
            JsonArray guessesJson = responseJson.get("guesses").getAsJsonArray();
            guessHistory = Guesses.fromJson(guessesJson);
        }
    }

    private JsonElement getGuess(Player player, TurnResult turnResult) {
        String guess = player.guess(turnResult.guessHistory());
        JsonObject guessJson = new JsonObject();
        guessJson.addProperty("type", "guess");
        guessJson.addProperty("id", this.gameId);
        guessJson.addProperty("word", guess);

        return guessJson;
    }

    private TurnResult constructTurnResult(Optional<JsonElement> response) {
        if (response.isEmpty()) {
            return TurnResult.ofFailure("Lost connection to server");
        }

        JsonObject responseJson = response.get().getAsJsonObject();
        String type = responseJson.get("type").getAsString();

        switch (type) {
            case "retry":
                List<Guess> guessHistory = Guesses.fromJson(responseJson.get("guesses").getAsJsonArray());
                return TurnResult.ofRetry(guessHistory);
            case "bye":
                String secretFlag = responseJson.get("flag").getAsString();
                return TurnResult.ofSuccess(secretFlag);
            case "error":
                String message = responseJson.get("message").getAsString();
                return TurnResult.ofFailure(message);
            default:
                throw new RuntimeException("Invalid response received from server");
        }
    }
}
