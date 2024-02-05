package ayaviri.project_two;

import java.io.IOException;

public class FTPClient {
    private final ServerProxy serverProxy;
    private final FTPOperation op;

    public FTPClient(FTPOperation op) {
        this.serverProxy = new ServerProxy(); // Not yet connected
        this.op = op;
    }

    public void performOperation() throws IOException {
        for (Step step : this.op.produceStepSequence(this.serverProxy)) {
            step.execute();
        }
    }
}
