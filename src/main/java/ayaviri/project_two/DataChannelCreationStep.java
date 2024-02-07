package ayaviri.project_two;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

// Creates a second connection to the server to server as the
// data channel and adds it to the given proxy
public class DataChannelCreationStep implements Step {
    private final ServerProxy serverProxy;

    public DataChannelCreationStep(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    public void execute() {
        // The method below creates the socket from the response and adds
        // it as the data channel to the server proxy
        this.serverProxy.processPasvCommand();
    }
}
