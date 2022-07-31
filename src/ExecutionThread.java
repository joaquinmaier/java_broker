import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutionThread extends Thread
{
    private volatile boolean            running;
    private Socket                      socket;
    private BufferedReader              input;
    private PrintWriter                 output;
    private AtomicReference<ThreadPool> parent;
    private MessageBuffer               messages;
    private User                        user;
    public final String                 client_id;

    public ExecutionThread(Socket s, AtomicReference<ThreadPool> p) throws IOException {
        this.socket     = s;
        this.input      = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
        this.output     = new PrintWriter( s.getOutputStream(), true );
        this.parent     = p;
        this.client_id  = UUID.randomUUID().toString();
        this.user       = new User( client_id );
        this.messages   = new MessageBuffer();

    }

    public void send_message(ServerMessage msg) {
        messages.add(msg);
    }

    public void quit() {
        this.running    = false;
        try { this.socket.close(); } catch (IOException e) { e.printStackTrace(); }
    }

    public void run() {
        System.out.println("\033[0;31mRUNNING\033[0m");
        this.running    = true;

        while (running && !socket.isClosed()) {
            // socket handling
            try {
                if (this.input.ready()) {
                    String received_message = this.input.readLine();

                    if (received_message == null)               { this.quit(); continue; }
                    if (received_message.equals(".exit"))       { this.quit(); continue; }
                    if (received_message.startsWith(".user="))  { this.user.set_user_name( received_message.substring( received_message.indexOf('=') + 1 ) ); continue; }

                    System.out.printf("Read: %s\n", received_message);

                    parent.get().send_message( new ServerMessage( (byte)0x01, this.client_id, this.user.get_formatted_username(), received_message ) );
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (messages.has_items()) {
                messages.reject_new_messages();

                for (ServerMessage sm : messages) {
                    if (sm.message == (byte) 0x02) {
                        System.out.println("\033[0;31mGOT A MESSAGE TO SEND\033[0m");

                        output.printf("%s: %s\n", sm.sender_username, sm.associated_data);

                        System.out.println("\033[0;31mDONE\033[0m");

                    }

                    this.messages.remove(sm);

                }

                messages.accept_new_messages();

            }

        }

        if (!running) try { socket.close(); } catch (IOException e) { e.printStackTrace(); };

        System.out.println("\033[0;31mSENDING DESTROY MESSAGE\033[0m");
        this.parent.get().send_message( new ServerMessage( (byte)0x00, this.client_id ) );
    }
}
