import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class ClientListenerThread extends Thread
{
    private volatile boolean                running;
    private AtomicReference<ClientSocket>   socket_reference;
    private MessageBuffer                   messages;

    public ClientListenerThread(AtomicReference<ClientSocket> socket) {
        this.socket_reference = socket;
        this.messages = new MessageBuffer();    // Reuso de codigo nefasto
    }

    public void request_socket() {
        messages.add(new ServerMessage((byte) 0x01, 0x00));
    }

    public void quit() {
        this.running = false;

        if (socket_reference.get().isHeldByCurrentThread()) socket_reference.get().freeLock();
    }

    public void run() {
        this.running = true;
        this.socket_reference.get().waitForLock();

        while (running) {
            if (messages.has_items()) {
                messages.waitForLock();

                for (ServerMessage sm : messages) {
                    if (sm.message == 0x01 && socket_reference.get().isHeldByCurrentThread()) {
                        socket_reference.get().freeLock();

                        // Allow the client to get the socket, then wait to get it again.
                        socket_reference.get().waitForOccupiedLock();
                        socket_reference.get().waitForLock();
                    }
                }

                messages.freeLock();
            }

            try {
                if (socket_reference.get().get().getInputStream().available() > 0) {
                    BufferedReader reader = new BufferedReader( new InputStreamReader(socket_reference.get().get().getInputStream()) );

                    String message = reader.readLine();
                    System.out.println(message);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }
}
