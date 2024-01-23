package ayaviri.project_one;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

// Component that parses the given command line arguments and constructs a configuration
// of arguments to construct a client that connects to the Wordle server. Assumes
// a valid list of command line arguments has been given as per the specification
// on the assignment webpage.
// See https://3700.network/docs/projects/socketbasics/#your-client-program
public class ClientConfig {
    private List<String> args;

    private int portNumber = 27993; // The default Wordle server (unencrypted) port number
    private boolean useEncryptedSocket = false; // True if a TLS socket is to be used
    private String hostname; // The hostname of the Wordle server
    private String username; // The user's Northeastern username

    private int encryptedPortNumber = 27994; // The default Wordle server (encrypted) port number
    
    public ClientConfig(String[] args) {
        this.args = new ArrayList<String>(Arrays.asList(args));
    }

    public int portNumber() {
        return this.portNumber;
    }

    public boolean useEncryptedSocket() {
        return this.useEncryptedSocket;
    }

    public String hostname() {
        return this.hostname;
    }

    public String username() {
        return this.username;
    }

    // Populates this config's properties from the list of command line arguments
    // given during instantiation. They are in the form (AND ORDER):
    // <-p port> <-s> <hostname*> <Northeastern-username*>
    // * Represents a required parameter
    public void parseArgs() {
        boolean hasPortNumber = this.maybeSetPortNumber();
        int argsParsed = this.parseEncryptedSocketFlag(hasPortNumber);
        this.hostname = this.args.get(argsParsed);
        this.username = this.args.get(argsParsed + 1);
    }

    // 1) Checks if the port number has been specified in this configuration's arguments
    // 2) Overwrites this config's port number if so
    // 3) Returns true if the port number was set
    private boolean maybeSetPortNumber() {
        boolean hasPortNumber = this.hasPortNumber();

        if (hasPortNumber) {
            this.portNumber = this.getPortNumber();
        }

        return hasPortNumber;
    }

    // Checks whether the encryption flag is present, sets this configuration's
    // corresponding flag accordingly. Sets this config's port number to the default 
    // encrypted port number if _hasPortNumber_ is false. Returns the total number
    // of arguments parsed from the given array
    private int parseEncryptedSocketFlag(boolean hasPortNumber) {
        int argsParsed = hasPortNumber ? 2 : 0; // Port number flag + port number
        this.useEncryptedSocket = this.hasEncryptedSocketFlag(hasPortNumber);
        argsParsed = this.useEncryptedSocket ? argsParsed + 1 : argsParsed; // Encrypted socket flag

        if (useEncryptedSocket && !hasPortNumber) {
            this.portNumber = this.encryptedPortNumber;
        }

        return argsParsed;
    }

    // Returns true if the first two elements of the given argument array are the
    // port number flag and port number respectively
    private boolean hasPortNumber() {
        return (this.args.get(0).equals("-p") && this.isInteger(this.args.get(1)));
    }

    // Assumes the port number exists (as the string representation of an integer) 
    // in the given array as the 2nd element and retrieves it
    private int getPortNumber() {
        return Integer.valueOf(this.args.get(1));
    }

    // Checks if the given list of arguments has an encryption flag based on whether or not it 
    // has a port number flag. Assumes a valid list of arguments. Returns true if the
    // encryption flag is present
    private boolean hasEncryptedSocketFlag(boolean hasPortNumberFlag) {
        int encryptionFlagIndex = hasPortNumberFlag ? 2 : 0;
        String possibleEncryptionFlag = this.args.get(encryptionFlagIndex); // This can result in an exception if invalid argument lists are allowed
        
        return possibleEncryptionFlag.equals("-s");
    }

    // Returns true if the given string represents a base-10 integer. Assumes it will not be given
    // a negative integer. Does not support strings with supplementary characters.
    private boolean isInteger(String possibleInteger) {
        if (possibleInteger.isEmpty()) return false;

        for (int characterIndex = 0; characterIndex < possibleInteger.length(); characterIndex++) {
            char currentCharacter = possibleInteger.charAt(characterIndex);

            if (!Character.isDigit(currentCharacter)) {
                return false;
            }
        }

        return true;
    }

    public String toString() {
        return String.format(
            "{portNumber: %s, useEncryptedSocket: %s, hostname: %s, username: %s}", 
            this.portNumber, 
            this.useEncryptedSocket, 
            this.hostname, 
            this.username
        );
    }
}
