package ayaviri.project_one;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

// Reponsible for: 
// 1) Connection to the Wordle server based on the given configuration during
// instantiation
// 2) Management of the Wordle player
// 3) Creation of and delegation to a proxy that manages all communication with
// the Wordle server
public class Client {
    private final ServerProxy serverProxy;
    private final Player player;
    private final String username;

    private final String wordListPath = "project1-words.txt";

    // Constructs a client AND connects to the server
    // at the hostname and port number specified by the
    // given configuration
    public Client(ClientConfig config) throws IOException, FileNotFoundException {
        // TODO: The connection to server should not really happen during the 
        // construction of the client
        Socket socket = this.connectToServer(config);
        System.out.println("Connected to server");
        this.serverProxy = new ServerProxy(socket);
        this.player = new Player(this.readInWordList());
        System.out.println("Word list read in");
        this.username = config.username();
    }

    // Plays a complete game of Wordle with the remote server and returns
    // the result of the game. Closes the connection with the server at 
    // the end of the game
    public GameResult playGame() throws IOException {
        this.serverProxy.greetServer(this.username);
        System.out.println("Server greeted");
        GameResult result = this.serverProxy.playGameWith(this.player);
        this.serverProxy.bidFarewellToServer();
        System.out.println("Server bid farewell to");

        return result;
    }

    // Connects to the server specified by the hostname and port number in 
    // the given configuration. Returns the socket representing that connection
    private Socket connectToServer(ClientConfig config) throws IOException {
        Socket socket = new Socket(
            config.hostname(),
            config.portNumber()
        );
        System.out.println("Socket created");

        if (config.useEncryptedSocket()) {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = socketFactory.createSocket(
                socket,
                config.hostname(),
                config.portNumber(),
                true // Auto-closes underlying socket when encrypted socket is closed
            );
        }

        return socket;
    }

    private List<String> readInWordList() throws FileNotFoundException {
        List<String> wordList = new ArrayList<>();
        Scanner scanner = new Scanner(new File(this.wordListPath));

        while (scanner.hasNextLine()) {
            wordList.add(scanner.nextLine());
        }

        scanner.close();

        return wordList;
    }
}
