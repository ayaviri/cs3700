package ayaviri.project_two;

import java.io.IOException;

public class EntryPoint {
    public static void main(String[] args) throws IOException {
        FTPOperation op = new Parser(args).parseArgs();
        FTPClient client = new FTPClient(op);
        client.performOperation();
    }
}
