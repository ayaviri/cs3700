# TODO

## Project 2:
- Implement the MVOperation component
- Create README with appropriate class diagrams
- Abstract the RMDIROperation, MKDIROperation, LSOperation components. They are identical

### Questions
- Would I ever want the output from a step and operate on it elsewhere ? The LsCommandStep right now logs the data received from the server to console, but should it store it to be operated on ? Is that desirable ?
- Could the ByteStreamConnection class extend both InputStream and OutputStream ? It is a thin wrapper around an encapsulation of both, and could allow the reuse of the readAll... function in the ServerProxy
