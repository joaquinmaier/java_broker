import java.util.ArrayList;

public class ServerMessage 
{
    Byte            message;
    String          associated_data;
    String          sender;

    public ServerMessage(byte msg, String sender) {
        this.message = msg;
        this.sender = sender;
    }

    public ServerMessage(byte msg, String sender, String ad) {
        this.message = msg;
        this.sender = sender;
        this.associated_data = ad;
    }

    public static byte[] get_byte_array(ArrayList<Byte> ba) {
        Byte[] Byte_data = new Byte[ba.size()];
        byte[] byte_data = new byte[ba.size()];

        int i = 0;
        for (Byte b : Byte_data) {
            byte_data[i++] = b.byteValue();
        }

        return byte_data;
    }
}
