package ayaviri.project_two;

// Builder for operations
public interface OpBuilder {
    // Throws exception if this operation does not receive parameters
    OpBuilder addParameter(String parameter);

    // Throws exception if a URL or file path could not be constructed
    // from added parameters
    FTPOperation tryBuild() throws RuntimeException;
}
