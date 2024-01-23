package ayaviri.project_one;

import java.io.IOException;
import java.io.FileNotFoundException;

public class EntryPoint {
    public static void main(String[] args) throws IOException, FileNotFoundException {
        ClientConfig config = new ClientConfig(args);
        config.parseArgs();
        System.out.println(String.format("Arguments parsed, config received: %s", config));

        Client client = new Client(config);
        System.out.println("Client created");
        GameResult gameResult = client.playGame();

        if (gameResult.didWin()) {
            System.out.println(gameResult.secretFlag().get());
        } else {
            System.out.println(gameResult.errorMessage().get());
        }
    }
}
