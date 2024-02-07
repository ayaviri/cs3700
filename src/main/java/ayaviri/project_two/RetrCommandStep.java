package ayaviri.project_two;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

// Step that calls the RETR FTP command with the given remote path,
// reads chunks of bytes from the data channel and writes them 
// into a file specified by the given local path until the data
// channel is closed
public class RetrCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String remotePath;
    private final String localPath;

    public RetrCommandStep(ServerProxy proxy, String rp, String lp) {
        this.serverProxy = proxy;
        this.remotePath = rp;
        this.localPath = lp;
    }

    public void execute() throws IOException {
        this.serverProxy.processRetrCommand(this.remotePath);
        OutputStream fos = new FileOutputStream(this.localPath, false);
        this.serverProxy.pipeAllInChunks(this.serverProxy.dataChannel(), fos);
        fos.close();
    }
}
