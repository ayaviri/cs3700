package ayaviri.project_two;

import java.net.URL;
import java.util.Optional;
import java.io.IOException;

// Sends the USER and PASS commands to the server
// through the given proxy using the given remote path
public class LoginStep implements Step {
    private final ServerProxy serverProxy;
    private String username = "anonymous";
    private Optional<String> password;
    private final String userInfoDelimiter = ":";

    public LoginStep(ServerProxy serverProxy, Optional<String> userInfo) {
        this.serverProxy = serverProxy;
        
        if (!userInfo.isEmpty()) {
            this.retrieveUserInfo(userInfo.get());
        }
    }

    public void execute() throws IOException {
        this.serverProxy.processUserCommand(this.username);

        if (!this.password.isEmpty()) {
            this.serverProxy.processPassCommand(this.password.get());
        }
    }

    // Splits the given string according to the _userInfoDelimiter_ and
    // sets the first element it finds as the username, the second as the 
    // password, disregarding the rest
    private void retrieveUserInfo(String userInfo) {
        String[] credentials = userInfo.split(this.userInfoDelimiter);
    
        if (credentials.length >= 1) {
            this.username = credentials[0];
        }

        if (credentials.length >= 2) {
            this.password = Optional.of(credentials[1]);
        }

        // Disregard the rest
    }
}
