package ayaviri.project_two;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

// Connects to the given FTP server using the given proxy
public class ServerConnectionStep implements Step {
    private final ServerProxy serverProxy;
    private final URL serverURL;
    private final String expectedHelloMessage = "foobar";
    private final int undefinedPort = -1;
    private final int defaultPort = 21;

    public ServerConnectionStep(ServerProxy serverProxy, URL serverURL) {
        this.serverProxy = serverProxy;
        this.serverURL = serverURL;
    }

    public void execute() throws IOException {
        ServerConnection c = this.establishControlChannel();
        this.serverProxy.addControlChannel(c);
        this.serverProxy.expectResponse(this.expectedHelloMessage);
    }

    private ServerConnection establishControlChannel() throws IOException {
        Socket s = this.createSocket();

        return new ServerConnection(s);
    }

    private Socket createSocket() throws IOException {
        int portNumber = this.serverURL.getPort() == this.undefinedPort ? this.defaultPort : this.serverURL.getPort();

        return new Socket(this.serverURL.getHost(), portNumber);
    }
}
