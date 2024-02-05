package ayaviri.project_two;

import java.io.IOException;

// Sends the following commands to the server. These all change the 
// mode of connection between server and client
// - TYPE I (8-bit binary mode)
// - MODE S (Stream mode)
// - STRU F (File-oriented mode)
public class ConnectionModeConfigurationStep implements Step {
    private final ServerProxy serverProxy;

    public ConnectionModeConfigurationStep(ServerProxy proxy) {
        this.serverProxy = proxy;
    }

    public void execute() throws IOException {
        this.serverProxy.processTypeCommand();
        this.serverProxy.processModeCommand();
        this.serverProxy.processStruCommand();
    }
}
