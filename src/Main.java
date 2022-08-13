import stind.STIND;

import java.io.IOException;


// ! TO-DO: The client WAY TOO OFTEN stops receiving messages out of nowhere. Might be a concurrency problem?
// ! TO-DO: Clients do not close down when the program crashes, etc. Allow ExecutionThreads to be automatically deleted after some time.
// ! TO-DO: Wayy too much consumption by the server, probably caused by having a ton of threads executing at the same time. Look into optimization
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
            System.out.println("\033[0;35mCOMMANDS:\n    .exit -- Close the client\n    .user=[username] -- Change your username (no spaces!)\033[0m");
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
