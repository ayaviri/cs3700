package ayaviri.project_two;

import java.net.Socket;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Optional;

// TODO: Determine the data representation this connection uses to encode from/
// decode to
public class ServerConnection {
    private final Socket socket;
    private final Writer writer;
    private final BufferedReader reader;

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new BufferedWriter(
            new OutputStreamWriter(this.socket.getOutputStream())
        );
        this.reader = new BufferedReader(
            new InputStreamReader(this.socket.getInputStream())
        );
    }

    public void write(String message) {
        try {
            System.out.println(String.format("Writing to server: %s", message));
            this.writer.write(message, 0, message.length());
            this.writer.flush();
        } catch (IOException e) {
            // I fucking checked exceptions
            throw new RuntimeException(e.getMessage());
        }
    }

    public String readLine() {
        try {
            String message = this.reader.readLine();
            System.out.println(String.format("Read from server: %s", message));

            return message;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
