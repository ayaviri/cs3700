# Project 1: Wordle TCP Client

Note to self: I never found the secret flag with the unencrypted connection. An implementation of a smarter guessing strategy will fix that.

## Architectural Overview (Static Interactions Between Components)

```
                                                                                          Has-a
                                                                                 ┌───────────────────────┐
                                                                                 │                       │
                                                                                 │                       │
                                                                                 │                       ▼
                                                                    ┌────────────┴──────┐        ┌──────────────────────┐
                                                       Has-a        │                   │        │                      │
                                                  ┌────────────────►│                   │        │                      │
   !! START HERE !!                               │                 │    ServerProxy    │        │   ServerConnection   │
                                 ┌────────────────┴──┐              │                   │        │                      │
   ┌────────────┐                │                   │              │                   │        │                      │
   │            │    Creates     │                   │              └───────────────────┘        └──────────────────────┘
   │ EntryPoint ├───────────────►│      Client       │
   │            │                │                   │
   └─────┬──────┘                │                   │
         │                       └─────────────────┬─┘
         │                                ▲        │                     ┌──────────┐
 Creates │                  ▼             │        │        Has-a        │          │
         │                                │        └────────────────────►│  Player  │
         ▼                                │                              │          │
┌──────────────────┐                      │                              └──────────┘
│                  │                      │
│                  │        Configures    │
│   ClientConfig   ├──────────────────────┘
│                  │
│                  │
└──────────────────┘
```

Not my greatest diagram, but I'm working on it. Descriptions of each and their purpose:

- **EntryPoint**: The component with the main function. Creates ClientConfig and Client, plays game, prints result
- **ClientConfig**: Responsible for PARSING COMMAND LINE ARGUMENTS and configuring Client accordingly
- **Client**: The highest-level interface between server and client. Has one public facing `playGame` method
- **ServerProxy**: An implementation of the remote proxy pattern. A local object representing the server that the Client speaks to
- **ServerConnection**: A representation of the TCP connection held with server that communicates using JSON. Handles reading from, writing to, and closing the connection
- **Player**: The component that encapsulates the Wordle game playing strategy. Currently hardcoded to a guessing strategy that chooses randomly for the wordlist

## Guessing Strategy Implemented

As of right now, the player just picks a random word from the list and submits it as a guess. Removes it to avoid choosing it again in the future.

## Testing

There are no unit tests, the bash script was just tested manually and logs were thrown about all over the place
