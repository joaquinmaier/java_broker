public class ServerMessage
{
    Byte            message;
    String          associated_data;
    String          sender;
    String          sender_username;

    public ServerMessage(byte msg, String sender) {
        this.message            = msg;
        this.sender             = sender;
    }

    public ServerMessage(byte msg, String sender, String sender_username, String ad) {
        this.message            = msg;
        this.sender             = sender;
        this.sender_username    = sender_username;
        this.associated_data    = ad;
    }

}
