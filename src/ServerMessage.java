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
        System.out.printf("Converting: %s\n", ba.toString());

        byte[] byte_data = new byte[ba.size()];

        for (int i = 0; i < ba.size(); i++) {
            System.out.printf("i = %d, value = %c\n", i, (char)ba.get(i).byteValue());
            byte_data[i] = ba.get(i).byteValue();
        }

        return byte_data;
    }
}
