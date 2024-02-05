package ayaviri.project_two;

import java.io.IOException;

// Sends the QUIT command to the server and closes the
// control channel
public class ServerDisconnectionStep implements Step {
    private final ServerProxy serverProxy;
    private final String quitCommand = "QUIT\r\n";

    public ServerDisconnectionStep(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    public void execute() throws IOException {
        this.serverProxy.controlChannel().write(this.quitCommand);
        this.serverProxy.controlChannel().close();
    }
}
