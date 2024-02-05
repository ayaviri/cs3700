package ayaviri.project_two;

import java.util.Optional;
import java.util.function.Supplier;

public class ServerProxy {
    private Optional<ServerConnection> controlChannel;
    // TODO: The current ServerConnection implementation can't send binary data directly
    private Optional<ServerConnection> dataChannel; 

    // Creates a proxy with no control or data channel
    public ServerProxy() {
        this.controlChannel = Optional.empty();
        this.dataChannel = Optional.empty();
    }

    public void addControlChannel(ServerConnection c) {
        if (!this.controlChannel.isEmpty()) {
            throw new RuntimeException("Only one control channel can be managed at a time");
        }

        this.controlChannel = Optional.of(c);
    }

    // In this context, to "process" a command will refer to sending a request to perform
    // a given command AND expect the appropriate response. The following methods are the 
    // command this proxy supports processing
    
    public void processUserCommand(String username) {
        this.processCommand(
            String.format("USER %s\r\n", username),
            () -> {
                this.expectSuccessResponse();
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

    // Sends the given command message to the server via the control channel and 
    // then executes the given response callback (in most cases, this callback will
    // make an assertion on the status code of the response, eg. success)
    private void processCommand(String commandMessage, Supplier<Void> responseCallback) {
        // TODO: What is the conventional type for a callback like this ?
        this.controlChannel().write(commandMessage);
        responseCallback.get();
    }

    // Reads the next line from this proxy's control channel and
    // makes an assertion that the response matches the given string
    // exactly
    public void expectResponse(String expectedResponse) {
        String actualResponse = this.controlChannel().readLine();
        assert actualResponse.equals(expectedResponse) : String.format("Did not receive expected response, instead got: %s", actualResponse);
    }
    
    private void expectMoreActionResponse() {
        this.expectResponse(1);
    }

    private void expectSuccessResponse() {
        this.expectResponse(2);
    }

    private void expectPrelimSuccessResponse() {
        this.expectResponse(3);
    }

    // Reads the next line from this proxy's control channel and 
    // makes an assertion that the response starts the with the given
    // DIGIT
    private void expectResponse(int expectedDigit) {
        String message = this.controlChannel().readLine();
        int actualDigit = Integer.parseInt(message.substring(0, 1));

        assert expectedDigit == actualDigit : String.format("Unexpected response received: %s", message);
    }

    // Convenience getters. ONLY CALL WHEN CHANNEL IS PRESENT

    protected ServerConnection controlChannel() {
        return this.controlChannel.get();
    }


    protected ServerConnection dataChannel() {
        return this.dataChannel.get();
    }
}
