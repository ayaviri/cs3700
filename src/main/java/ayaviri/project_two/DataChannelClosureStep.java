package ayaviri.project_two;

import java.io.IOException;

public class DataChannelClosureStep implements Step {
    private final ServerProxy serverProxy;

    public DataChannelClosureStep(ServerProxy proxy) {
        this.serverProxy = proxy;
    }

    public void execute() throws IOException {
        this.serverProxy.closeDataChannel();
    }
}
