// ? Class created for clarity, adds nothing of value to ServerMessage
public class ClientMessage extends ServerMessage {
    public ClientMessage(byte msg, String sender) {
        super(msg, sender);
    }

    public ClientMessage(byte msg, String sender, String ad) {
        super(msg, sender, ad);
    }
}
