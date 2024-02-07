package ayaviri.project_two;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class ListCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String directoryPath;

    public ListCommandStep(ServerProxy proxy, String directoryPath) {
        this.serverProxy = proxy;
        this.directoryPath = directoryPath;
    }

    public void execute() throws IOException {
        this.serverProxy.processListCommand(this.directoryPath);
        OutputStream out = new ByteArrayOutputStream();
        this.serverProxy.pipeAllInChunks(this.serverProxy.dataChannel(), out);
        System.out.println(out.toString());
    }
}
