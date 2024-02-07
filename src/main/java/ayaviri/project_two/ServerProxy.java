package ayaviri.project_two;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class ServerProxy {
    private Optional<ServerConnection> controlChannel;
    private Optional<ByteStreamConnection> dataChannel; 
    private final String whitespaceRegex = "\\s+";
    private final int chunkSize = 1024;

    // Creates a proxy with no control or data channel
    public ServerProxy() {
        this.controlChannel = Optional.empty();
        this.dataChannel = Optional.empty();
    }

    // MY BYTESTREAMCONNECTION CAN'T EXTEND BOTH THE INPUTSTREAM AND OUTPUTSTREAM
    // ABSTRACT CLASSES, SO THE NEXT TWO METHODS ARE IDENTICAL. At least I can 
    // fucking overload a method name

    // Reads all of the bytes from the given input stream in chunks and write them
    // to the given output stream
    public void pipeAllInChunks(ByteStreamConnection in, OutputStream out) throws IOException {
        // TODO: Revisit and see if this can be written with do-while
        while (true) {
            byte[] buffer = new byte[this.chunkSize];
            int bytesRead = in.read(buffer, 0, this.chunkSize);

            if (bytesRead == -1) {
                break;
            } else {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void pipeAllInChunks(InputStream in, ByteStreamConnection out) throws IOException {
        while (true) {
            byte[] buffer = new byte[this.chunkSize];
            int bytesRead = in.read(buffer, 0, this.chunkSize);

            if (bytesRead == -1) {
                break;
            } else {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void closeControlChannel() throws IOException {
        this.controlChannel().close();
        this.controlChannel = Optional.empty();
    }

    public void closeDataChannel() throws IOException {
        this.dataChannel().close();
        this.dataChannel = Optional.empty();
    }

    public void addControlChannel(ServerConnection c) {
        if (!this.controlChannel.isEmpty()) {
            throw new RuntimeException("Only one control channel can be managed at a time");
        }

        this.controlChannel = Optional.of(c);
    }

    public void addDataChannel(ByteStreamConnection c) {
        if (!this.dataChannel.isEmpty()) {
            throw new RuntimeException("Only one data channel can be managed at a time");
        }

        this.dataChannel = Optional.of(c);
    }

    // Connects to the host with the IP address and host number in the response
    // from the PASV FTP command
    private ByteStreamConnection establishDataChannel(String pasvResponse) {
        List<String> addressTokens = this.getAddressTokensFromPasvResponse(pasvResponse);
        String ip = this.getIpFromAddressTokens(addressTokens);
        int port = this.getPortFromAddressTokens(addressTokens);

        try {
            Socket s = new Socket(ip, port);

            return new ByteStreamConnection(s);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Parses the server response from the PASV command into a list of 
    // tokens containing the IP address and port number for the client to 
    // connect to and construct a data channel from
    private List<String> getAddressTokensFromPasvResponse(String r) {
        List<String> responseTokens = new ArrayList<>(Arrays.asList(r.split(this.whitespaceRegex)));
        String unparsedAddress = responseTokens.get(4);
        String strippedAddress = unparsedAddress.replaceAll("[\\(\\)\\.]", "");
        List<String> addressTokens = new ArrayList<>(Arrays.asList(strippedAddress.split(",")));

        return addressTokens;
    }

    // Constructs a string representation of an IP address from a list of tokens that
    // contain both the IP address and the port number. The first 4 numbers represent
    // the IP address (eg. [129,10,114,26,83,55])
    private String getIpFromAddressTokens(List<String> addressTokens) {
        List<String> ipTokens = addressTokens.subList(0, 4); // TODO: Magic number
        String ip = String.join(".", ipTokens);

        return ip;
    }

    // Gets the 16-bit port number from the last two 8-bit integer strings of the 
    // given list of tokens. The 5th number represents the upper 8 bits, the 6th
    // the lower 8 bits
    private int getPortFromAddressTokens(List<String> addressTokens) {
        List<String> portTokens = addressTokens.subList(4, 6);
        int upperHalf = Integer.valueOf(portTokens.get(0)) << 8;
        int lowerHalf = Integer.valueOf(portTokens.get(1));

        return upperHalf + lowerHalf;
    }

    // In this context, to "process" a command will refer to sending a request to perform
    // a given command AND expect the appropriate response. The following methods are the 
    // command this proxy supports processing
    
    public void processUserCommand(String username) {
        this.processCommand(
            String.format("USER %s\r\n", username),
            () -> {
                this.expectPrelimSuccessResponse();
                return null;
            }
        );
    }

    public void processPassCommand(String password) {
        this.processCommand(
            String.format("PASS %s\r\n", password),
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    public void processTypeCommand() {
        this.processCommand(
            "TYPE I\r\n",
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    public void processModeCommand() {
        this.processCommand(
            "MODE S\r\n",
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    public void processStruCommand() {
        this.processCommand(
            "STRU F\r\n",
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    public void processMkdCommand(String directoryPath) {
        this.processCommand(
            String.format("MKD %s\r\n", directoryPath),
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    public void processRmdCommand(String directoryPath) {
        this.processCommand(
            String.format("RMD %s\r\n", directoryPath),
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    // The response callback parses the IP address and port number from
    // the response, creates a Socket that connects to it, and adds it as the
    // data channel to this proxy (THROWS AN EXCEPTION IF ONE ALREADY EXISTS)
    public void processPasvCommand() {
        this.processCommand(
            "PASV\r\n",
            () -> {
                String response = this.expectSuccessResponse();
                ByteStreamConnection c = this.establishDataChannel(response);
                this.addDataChannel(c);
                return null;
            }
        );
    }

    public void processListCommand(String directoryPath) {
        this.processCommand(
            String.format("LIST %s\r\n", directoryPath),
            () -> {
                this.expectMoreActionResponse();
                return null;
            }
        );
    }

    public void processRetrCommand(String remotePath) {
        this.processCommand(
            String.format("RETR %s\r\n", remotePath),
            () -> {
                this.expectMoreActionResponse();
                return null;
            }
        );
    }

    public void processStorCommand(String remotePath) {
        this.processCommand(
            String.format("STOR %s\r\n", remotePath),
            () -> {
                this.expectMoreActionResponse();
                return null;
            }
        );
    }

    public void processDeleCommand(String remotePath) {
        this.processCommand(
            String.format("DELE %s\r\n", remotePath),
            () -> {
                this.expectSuccessResponse();
                return null;
            }
        );
    }

    // Sends the given command message to the server via the control channel and 
    // then executes the given response callback (in most cases, this callback will
    // make an assertion on the status code of the response, eg. success)
    private void processCommand(String commandMessage, Supplier<Void> responseCallback) {
        this.controlChannel().write(commandMessage);
        responseCallback.get();
    }
    
    // The following expectX methods read the next line from this proxy's control
    // channel, make an assertion about the line, and then return it

    public String expectResponse(String expectedResponse) {
        String actualResponse = this.controlChannel().readLine();

        if (!actualResponse.equals(expectedResponse)) {
            throw new RuntimeException(String.format("Did not receive expected response, instead got: %s", actualResponse));
        }

        return actualResponse;
    }
    
    private String expectMoreActionResponse() {
        return this.expectResponse(1);
    }

    public String expectSuccessResponse() {
        return this.expectResponse(2);
    }

    private String expectPrelimSuccessResponse() {
        return this.expectResponse(3);
    }

    // Reads the next line from this proxy's control channel and 
    // makes an assertion that the response starts the with the given
    // DIGIT
    private String expectResponse(int expectedDigit) {
        String message = this.controlChannel().readLine();
        int actualDigit = Integer.parseInt(message.substring(0, 1));

        if (expectedDigit != actualDigit) {
            throw new RuntimeException(String.format("Unexpected response received: %s", message));
        }

        return message;
    }

    // Convenience getters. ONLY CALL WHEN CHANNEL IS PRESENT

    protected ServerConnection controlChannel() {
        if (!this.controlChannel.isEmpty()) {
            return this.controlChannel.get();
        }

        throw new RuntimeException("No control channel added to proxy");
    }


    protected ByteStreamConnection dataChannel() {
        if (!this.dataChannel.isEmpty()) {
            return this.dataChannel.get();
        }

        throw new RuntimeException("No data channel added to proxy");
    }
    
    public int chunkSize() {
        return this.chunkSize;
    }
}
