package ayaviri.project_two;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

// Represents a connection over TCP with a remote host.
// The incoming and outgoing streams of bytes are not altered.
public class ByteStreamConnection {
    private final Socket socket;
    private final OutputStream outputStream;
    private final InputStream inputStream;

    public ByteStreamConnection(Socket s) throws IOException {
        this.socket = s;
        this.outputStream = this.socket.getOutputStream();
        this.inputStream = this.socket.getInputStream();
    }

    public void write(int b) {
        throw new UnsupportedOperationException("Bytes can only be written from buffer");
    }

    public int read() {
        throw new UnsupportedOperationException("Bytes can only be read into buffer");
    }

    public void write(byte[] byteArray, int offset, int length) throws IOException {
        this.outputStream.write(byteArray, offset, length);
        this.outputStream.flush();
    }

    // Returns the number of bytes read, -1 if the stream is closed
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return this.inputStream.read(buffer, offset, length);
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
