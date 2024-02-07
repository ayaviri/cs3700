package ayaviri.project_two;

// Component responsible for creating a specific OpBuilder 
// (eg. an LSBuilder)
public class OpFactory {
    public static OpBuilder create(String possibleOp) {
        switch (possibleOp) {
            case "ls":
                return new LSOperation.Builder();
            case "mkdir":
                return new MKDIROperation.Builder();
            case "rm":
                return new RMOperation.Builder();
            case "rmdir":
                return new RMDIROperation.Builder();
            case "cp":
                return new CPOperation.Builder();
            case "mv":
                return new MVOperation.Builder();
            default:
                throw new IllegalArgumentException(String.format("Invalid operation received: %s", possibleOp));
        }
    }
}
