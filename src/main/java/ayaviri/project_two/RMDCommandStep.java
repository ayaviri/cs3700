package ayaviri.project_two;

// Sends the RMD command to the server with the given directory path
public class RmdCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String directoryPath;

    public RmdCommandStep(ServerProxy proxy, String directoryPath) {
        this.serverProxy = proxy;
        this.directoryPath = directoryPath;
    }

    public void execute() {
        this.serverProxy.processRmdCommand(this.directoryPath);
    }
}
