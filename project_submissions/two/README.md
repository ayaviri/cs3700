# Project 2: FTP Client

I spent too much time on this project, and I think it's slightly overengineered, but oh well. Who's reading all this XD

## A Quick Glossary of Terms

- **(FTP)Operation**: As of writing this (02/07/24), one of mv, cp, rm, rmdir, mkdir, ls. These operations compose the interface provided to a user of this client
- **Step**: A smaller unit of work (relative to an Operation) that, when composed together, perform an Operation. An example of this is the step that connects the client to server, the ServerConnectionStep. 

The following are not specific to this client implementation

- **FTP Command**: A lower level interface that the *client* uses to communicate with the FTP server. This term is not specific to this project. See [here](https://en.wikipedia.org/wiki/List_of_FTP_commands).
- **Control channel**: FTP uses two socket connections, one for sending/receiving FTP requests/responses, and
- **Data channel**: another for downloading/uploading data. **NO DATA IS SENT THROUGH THE CONTROL CHANNEL**.

## A Worked Example

Let's go through the what the system does to execute the following invocation in order to make it easier to digest the diagram below:

```
./3700ftp ls ftp://username:password@host:port/directory/path
```

1) Parser takes first command line argument (`ls`) and sends it to an Operation factory to create the appropriate Operation builder, an LSOperation.Builder.
2) Builder takes remaining arguments, validates them (with the exception of directory existence), and creates an LSOperation.
3) This LSOperation is given to the Client, and the Client's `performOperation` method is called. This calls the underlying LSOperation's `produceStepSequence` method, and each step returned is then `execute`'d one after the other.
4) The LSOperation in particular is composed of the following Steps:
    1) **ServerConnectionStep**: Creates the control channel and adds it to the proxy
    2) **LoginStep**: Passes the user information of the remote URL through USER and PASS FTP commands
    3) **ConnectionModeConfigurationStep**: Calls a few FTP commands that will configure the data channel to be opened
    4) **DataChannelCreationStep**: Creates the data channel through which the FTP server will send the directory listings
    5) **ListCommandStep**: Calls the LIST FTP command, reads the directory contents from the data channel, and logs them to console
    6) **DataChannelClosureStep**: Closes the data channel
    7) **ServerDisconnectionStep**: Closes the control channel

## Architectural Overview (Static Interaction Between Logical Components)

```
                            ┌─────────────────┐
                            │                 │               Client-side
                            │                 │
            ┌───────────────┤    EntryPoint   │
            │               │                 │
            │   Creates-a   │                 │
            │               └─────────────────┘
            │              Creates-a                  Is-given-to
            │          ┌─────────────────┐      ┌─────────────────────┐
            ▼          │                 │      │                     │
     ┌─────────────────┴──────┐          ▼      │                     ▼
     │                        │     ┌───────────┴────────┐     ┌──────────────┐
     │                        │     │                    │     │              │
     │   CL Argument Parser   │     │                    │     │    Client    │
     │                        │     │   FTP Operation    │     │              │
     │                        │     │                    │     └─┬────────────┘
     └────────────────────────┘     │                    │       │
                                    └─┬──────────────────┘       │
                                      │                          │ Uses
              Produces-a-sequence-of  │                          │
                               ┌──────┘                          │
                               ▼                                 ▼
                         ┌──────────┐            ┌──────────────────┐
                         │          │            │                  │
                         │   Step   │            │   Server Proxy   │
                         │          │            │                  │
                         └──────────┘            └─────┬────────────┘
                                                       │
                                                       │
                                                       │
───────────────────────────────────────────────────────┼───────────────────────────
                                                       │
                                                       │
                              ┌───────────────┐        │         Server-side
                              │               │        │
                              │               │        │
                              │  FTP Server   │        │
                              │               │◄───────┘
                              │               │  Sends-commands-to
                              └───────────────┘
```

### The Diagram Above, In Natural Language

Again, I'm working on my diagrams. Spelled out, the above communicates the following.

1) The EntryPoint component creates a Parser for the command line arguments. This Parser makes use of an OperationFactory to parse the first argument and then delegates to the appropriate factory produced OperationBuilder to produce the supported FTPOperation.
2) Once the appropriate FTPOperation has been constructed from the command line arguments, the Operation is given to the Client.
3) The client calls upon the Operation's ability to produce a list of Steps that, when done in sequence, perform the Operation.
4) Communication with the client is done through server proxy, which each Step uses to send FTP commands to the FTP server using abstractions over the Java standard library sockets.
