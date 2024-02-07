package ayaviri.project_two;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;

public class RMOperation implements FTPOperation {
    // Represents the path of the file to be deleted
    private final URL remotePath;

    private RMOperation(Builder b) {
        this.remotePath = b.remotePath().get();
    }

    public List<Step> produceStepSequence(ServerProxy proxy) {
        List<Step> steps = new ArrayList<>();
        steps.add(new ServerConnectionStep(proxy, this.remotePath));
        steps.add(new LoginStep(proxy, Optional.of(this.remotePath.getUserInfo())));
        steps.add(new ConnectionModeConfigurationStep(proxy));
        steps.add(new DataChannelCreationStep(proxy));
        steps.add(new DeleCommandStep(proxy, this.remotePath.getPath()));
        steps.add(new DataChannelClosureStep(proxy));
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
                throw new IllegalArgumentException("rm command given too many arguments");
            }
   
            try {
                this.remotePath = Optional.of(new URL(parameter));
                return this;
            } catch (MalformedURLException e) {
                throw new RuntimeException(String.format("Malformed URL given: %s", parameter));
            }
    
        }
    
        public RMOperation tryBuild() {
            if (this.remotePath.isEmpty()) {
                throw new IllegalArgumentException("rm command constructed without any arguments");
            }

            return new RMOperation(this);
        }

        public Optional<URL> remotePath() {
            return this.remotePath;
        }
    }
}
