package ayaviri.project_two;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

// Step that calls the STOR FTP command with the given remote path,
// reads chunks of bytes from a file specified by the local path and
// writes them to the data channel until the file is completely uploaded
public class StorCommandStep implements Step {
    private final ServerProxy serverProxy;
    private final String remotePath;
    private final String localPath;

    public StorCommandStep(ServerProxy proxy, String rp, String lp) {
        this.serverProxy = proxy;
        this.remotePath = rp;
        this.localPath = lp;
    }

    public void execute() throws IOException {
        this.serverProxy.processStorCommand(this.remotePath);
        InputStream fis = new FileInputStream(new File(this.localPath));
        this.serverProxy.pipeAllInChunks(fis, this.serverProxy.dataChannel());
        fis.close();
    }
}
