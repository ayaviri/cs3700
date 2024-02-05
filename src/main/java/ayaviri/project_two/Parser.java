package ayaviri.project_two;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

// Component for parsing the command line argument for the FTP client.
// Responsible for validation of the number of command line arguments 
// received (during instantiation).
public class Parser {
    // Small number of (mostly) homogeneous arguments expected, so an
    // iterator over the iterable is retrieved
    private final Iterator<String> argIterator;

    // Constructs a Parser from the given list of command line arguments. 
    // Validates number of command line arguments
    public Parser(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Program requires operation");
        }

        if (args.length > 3) {
            throw new IllegalArgumentException("Only operations with two parameters are currently supported");
        }

        this.argIterator = new ArrayList<>(Arrays.asList(args)).iterator();
    }

    public FTPOperation parseArgs() {
        OpBuilder builder = OpFactory.create(this.argIterator.next());

        while (this.argIterator.hasNext()) {
            builder.addParameter(this.argIterator.next());
        }

        return builder.tryBuild();
    }
}
