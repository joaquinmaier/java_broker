# Java Broker

Simple broker proof-of-concept using sockets and Java for my Networking class.

## How to use

First, to compile the program, run `make` or `make compile`.

Then to run the **server**:

```make run-server```

Or, to run a **client**:

```make run-client```

### Final note

Because of the way the input is handled, your message might seem to be cut off if you receive one. It doesn't, it's just visual, the message is not modified.
