import stind.STIND;

import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException {
        STIND stind = new STIND(System.in);

        if (args[0].equals("--server")) {
            Servidor server = new Servidor();

            server.listen();
        }
        else if (args[0].equals("--client")) {
            Cliente client = new Cliente(stind);

            String message = "";
            while (!message.equals(".exit")) {
                System.out.print("Message: ");
                message = stind.read_line().unwrap_or(Throwable::printStackTrace);
                client.send_message(message);
            }

            client.close();
        }
    }
}
