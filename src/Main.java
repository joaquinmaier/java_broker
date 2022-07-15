import stind.STIND;

import java.io.IOException;


// ! TO-DO: Si el ClientListenerThread recibe o no el socket de nuevo es random, probablemente por un problema de concurrencia
public class Main
{
    public static void main(String[] args) throws IOException {
        STIND stind = new STIND(System.in);

        if (args.length == 0) throw new RuntimeException("0 arguments were provided, at least 1 is required.");

        if (args[0].equals("--server")) {
            Servidor server = new Servidor();
            while (true)
            {
                server.listen();
            }
        }
        else if (args[0].equals("--client")) {
            System.out.println("\033[0;35mCOMMANDS:\n    .exit -- Close the client\033[0m");
            Cliente client = new Cliente(stind);

            String message = "";
            boolean running = true;
            while (running) {
                message = stind.read_line().unwrap_or( Throwable::printStackTrace );

                if (message.equals(".exit")) { running = false;}

                client.send_message(message);
            }

            client.close();
        }
    }
}
