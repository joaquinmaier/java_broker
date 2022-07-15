import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

public class ClientListenerThread extends Thread
{
    private volatile boolean                running;
    private AtomicReference<ClientSocket>   socket_reference;
    private BufferedReader                  reader;
    private MessageBuffer                   messages;
    private Long                            thread_id;

    public ClientListenerThread(AtomicReference<ClientSocket> socket) throws IOException {
        this.socket_reference               = socket;
        this.reader                         = new BufferedReader( new InputStreamReader( socket.get().get().getInputStream() ) );
        this.messages                       = new MessageBuffer();    // Reuso de codigo nefasto
        this.thread_id                      = Thread.currentThread().getId();
    }

    //! TO-DO: refactor this
    public void request_socket() {
        messages.add(new ClientMessage((byte) 0x01, ""));
    }

    public void quit() {
        if (socket_reference.get().owner() == thread_id) socket_reference.get().free(thread_id);
        this.running                        = false;
    }

    public void run() {
        this.running                        = true;
        this.socket_reference.get().take(thread_id);

        while (running) {
            try {
                if (socket_reference.get().get().getInputStream().available() > 0) {
                    BufferedReader reader   = new BufferedReader( new InputStreamReader(socket_reference.get().get().getInputStream()) );

                    String message          = reader.readLine();
                    System.out.println(message);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (messages.has_items()) {
                messages.reject_new_messages();

                for (ServerMessage sm : messages) {
                    if (sm.message == 0x01) {
                        //System.out.println("\033[0;34mREQUEST TO LEAVE SOCKET\033[0m");
                        if (socket_reference.get().owner() == thread_id) socket_reference.get().free(thread_id);

                        // Allow the client to get the socket, then wait to get it again.
                        socket_reference.get().wait_for_occupation();
                        socket_reference.get().take(thread_id);
                        //System.out.println("\033[0;34mGOT THE SOCKET BACK\033[0m");
                    }

                    messages.remove(sm);
                }

                messages.accept_new_messages();
            }

            try {
                if (socket_reference.get().get().getInputStream().available() > 0) {
                    String message          = reader.readLine();
                    System.out.println(message);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
    }
}
