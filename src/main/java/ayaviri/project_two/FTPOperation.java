package ayaviri.project_two;

import java.util.List;

// Represents an operation a user can perform on an FTP server
public interface FTPOperation {

    // Produces the ordered sequence of steps that this operation
    // must perform on the given proxy
    List<Step> produceStepSequence(ServerProxy proxy);
}
