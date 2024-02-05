package ayaviri.project_two;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

public class RMDIROperation implements FTPOperation {
    // Represents the path of the directory to be removed 
    private final URL remotePath;

    private RMDIROperation(Builder b) {
        this.remotePath = b.remotePath().get();
    }

    public List<Step> produceStepSequence(ServerProxy proxy) {
        List<Step> steps = new ArrayList<>();
        steps.add(new ServerConnectionStep(proxy, this.remotePath));
        steps.add(new LoginStep(proxy, Optional.of(this.remotePath.getUserInfo())));
        steps.add(new ConnectionModeConfigurationStep(proxy));
        steps.add(new RMDCommandStep(proxy, this.remotePath.getPath()));
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
                throw new IllegalArgumentException("rmdir command given too many arguments");
            }
   
            try {
                this.remotePath = Optional.of(new URL(parameter));
                return this;
            } catch (MalformedURLException e) {
                throw new RuntimeException(String.format("Malformed URL given: %s", parameter));
            }
    
        }
    
        public RMDIROperation tryBuild() {
            if (this.remotePath.isEmpty()) {
                throw new IllegalArgumentException("rmdir command constructed without any arguments");
            }

            return new RMDIROperation(this);
        }

        public Optional<URL> remotePath() {
            return this.remotePath;
        }
    }
}

