package ayaviri.project_two;

// Sends the MKD command to the server with the given directory path
public class MKDCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String directoryPath;

    public MKDCommandStep(ServerProxy proxy, String directoryPath) {
        this.serverProxy = proxy;
        this.directoryPath = directoryPath;
    }

    public void execute() {
        this.serverProxy.processMkdCommand(this.directoryPath);
    }
}
