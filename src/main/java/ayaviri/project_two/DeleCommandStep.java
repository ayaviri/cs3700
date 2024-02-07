package ayaviri.project_two;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class DeleCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String remotePath;

    public DeleCommandStep(ServerProxy proxy, String remotePath) {
        this.serverProxy = proxy;
        this.remotePath = remotePath;
    }

    public void execute() throws IOException {
        this.serverProxy.processDeleCommand(this.remotePath);
    }
}
