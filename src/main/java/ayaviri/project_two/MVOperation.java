package ayaviri.project_two;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MVOperation implements FTPOperation {
    private final URL remotePath;
    private final Path localPath;
    private final boolean isSrcRemote;

    private MVOperation(Builder b) {
        this.remotePath = b.remotePath().get();
        this.localPath = b.localPath().get();
        this.isSrcRemote = b.isSrcRemote();
    }

    public List<Step> produceStepSequence(ServerProxy proxy) {
        System.out.println(this);
        List<Step> steps = new ArrayList<>();
        steps.add(new ServerConnectionStep(proxy, this.remotePath));
        steps.add(new LoginStep(proxy, Optional.of(this.remotePath.getUserInfo())));
        steps.add(new ConnectionModeConfigurationStep(proxy));
        steps.add(new DataChannelCreationStep(proxy));

        if (this.isSrcRemote) {
            steps.add(
                new RetrCommandStep(
                    proxy, 
                    this.remotePath.getPath(), 
                    this.localPath.toString()
                )
            );
            steps.add(new DeleCommandStep(proxy, this.remotePath.getPath()));
            steps.add(new DataChannelClosureStep(proxy));
        } else {
            steps.add(
                new StorCommandStep(
                    proxy, 
                    this.remotePath.getPath(), 
                    this.localPath.toString()
                )
            );
            steps.add(new DataChannelClosureStep(proxy));
            steps.add(new LocalFileDeletionStep(this.localPath.toString()));
        }

        steps.add(new ServerDisconnectionStep(proxy));

        return steps;
    }

    public String toString() {
        return String.format("{remotePath: %s, localPath: %s, isSrcRemote: %s}\n", this.remotePath, this.localPath, this.isSrcRemote);
    }

    public static class Builder implements OpBuilder {
        private Optional<URL> remotePath;
        private Optional<Path> localPath;
        // Relies on the tryBuild and addParameter methods to ensure it is populated by the time of construction of a MVOperation
        private boolean isSrcRemote; 
        private final String ftpProtocol = "ftp://";

        public Builder() {
            this.remotePath = Optional.empty();
            this.localPath = Optional.empty();
        }

        public Builder addParameter(String parameter) {
            boolean hasFTPProtocol = this.hasFTPProtocol(parameter);

            if (hasFTPProtocol && !this.remotePath.isPresent()) {
                this.setRemotePath(parameter);
            } else if (hasFTPProtocol && this.remotePath.isPresent()) {
                throw new RuntimeException("mv command cannot receive two remote paths");
            } else if (!hasFTPProtocol && !this.localPath.isPresent()) {
                this.setLocalPath(parameter);
            } else {
                throw new RuntimeException("mv command cannot receive two local paths");
            }

            return this;
        }

        private boolean hasFTPProtocol(String parameter) {
            return parameter.length() > this.ftpProtocol.length() &&
                parameter.startsWith(this.ftpProtocol);
        }

        private void setRemotePath(String parameter) {
            try {
                this.remotePath = Optional.of(new URL(parameter));
                this.isSrcRemote = !this.localPath.isPresent();
            } catch (MalformedURLException e) {
                throw new RuntimeException(String.format("Malformed URL given: %s", parameter));
            }
        }

        private void setLocalPath(String parameter) {
            this.localPath = Optional.of(Paths.get(parameter));
            this.isSrcRemote = this.remotePath.isPresent();
        }

        public MVOperation tryBuild() {
            if (this.remotePath.isPresent() && this.localPath.isPresent()) {
                return new MVOperation(this);
            }

            throw new RuntimeException("mv command must be constructed with one local and one remote path");
        }

        public Optional<URL> remotePath() {
            return this.remotePath;
        }

        public Optional<Path> localPath() {
            return this.localPath;
        }

        public boolean isSrcRemote() {
            return this.isSrcRemote;
        }
    }
}
