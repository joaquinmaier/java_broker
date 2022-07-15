public class ServerMessage 
{
    Byte            message;
    String          associated_data;
    String          sender;

    public ServerMessage(byte msg, String sender) {
        this.message            = msg;
        this.sender             = sender;
    }

    public ServerMessage(byte msg, String sender, String ad) {
        this.message            = msg;
        this.sender             = sender;
        this.associated_data    = ad;
    }

}
