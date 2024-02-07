package ayaviri.project_two;

import java.io.File;

// Deletes the file specified by the given local path
public class LocalFileDeletionStep implements Step {
    private final String localPath;

    public LocalFileDeletionStep(String lp) {
        this.localPath = lp;
    }

    public void execute() {
        new File(this.localPath).delete();
    }
}
