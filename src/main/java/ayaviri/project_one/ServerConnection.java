package ayaviri.project_one;

import java.net.Socket;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import com.google.gson.JsonStreamParser;
import com.google.gson.JsonElement;

// Wraps a socket, representing a TCP connection using JSON as the 
// language of data interchange. Can write to, read from, and close
// the connection
public class ServerConnection {
    private final Socket socket;
    private final Writer writer;
    private final JsonStreamParser parser;

    public ServerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.writer = new BufferedWriter(
            new OutputStreamWriter(this.socket.getOutputStream())
        );
        this.parser = new JsonStreamParser(
            new InputStreamReader(this.socket.getInputStream())
        );
    }

    // Writes the given JSON element to this connection's underlying
    // output stream. Flushes afterwards
    public void write(JsonElement message) throws IOException {
        System.out.println(String.format("Writing to server: %s", message.toString()));
        this.writer.write(String.format("%s\n", message.toString()));
        this.writer.flush();
    }

    // Attempts to read the next JSON element from this connection's underlying
    // input stream. Returns the read JSON element or an empty Optional
    // if there is none (ie. the stream is closed)
    public Optional<JsonElement> readElement() {
        Optional<JsonElement> elementRead = Optional.empty();

        if (this.parser.hasNext()) {
            elementRead = Optional.of(this.parser.next());
        }

        return elementRead;
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
