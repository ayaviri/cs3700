package ayaviri.project_two;

import java.io.IOException;

// Represents an atomic, reusable unit of work that can be performed on an 
// FTP server. Essentially a function object that does not receive or produce
// anything. Each implementation of a step usually receives a ServerProxy with
// which it communicates to the FTP server
public interface Step {
    void execute() throws IOException;
}
