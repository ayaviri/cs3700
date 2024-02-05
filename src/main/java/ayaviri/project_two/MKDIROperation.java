package ayaviri.project_two;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class MKDIROperation implements FTPOperation {
    // Represents the path of the directory to be created
    private final URL remotePath;

    private MKDIROperation(Builder b) {
        this.remotePath = b.remotePath().get();
    }

    public List<Step> produceStepSequence(ServerProxy proxy) {
        List<Step> steps = new ArrayList<>();
        steps.add(new ServerConnectionStep(proxy, this.remotePath));
        steps.add(new LoginStep(proxy, Optional.of(this.remotePath.getUserInfo())));
        steps.add(new ConnectionModeConfigurationStep(proxy));
        steps.add(new MKDCommandStep(proxy, this.remotePath.getPath()));
        steps.add(new ServerDisconnectionStep(proxy));

        return steps;
    }

    public static class Builder implements OpBuilder {
        private Optional<URL> remotePath;

        public Builder() {
            this.remotePath = Optional.empty();
        }
    
        public Builder addParameter(String parameter) {
            if (!this.remotePath.isEmpty()) {
                throw new IllegalArgumentException("mkdir command given too many arguments");
            }
   
            try {
                this.remotePath = Optional.of(new URL(parameter));
                return this;
            } catch (MalformedURLException e) {
                throw new RuntimeException(String.format("Malformed URL given: %s", parameter));
            }
    
        }
    
        public MKDIROperation tryBuild() {
            if (this.remotePath.isEmpty()) {
                throw new IllegalArgumentException("mkdir command constructed without any arguments");
            }

            return new MKDIROperation(this);
        }

        public Optional<URL> remotePath() {
            return this.remotePath;
        }
    }
}

