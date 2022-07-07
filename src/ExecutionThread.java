import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutionThread extends Thread
{
    private volatile boolean            running;
    private Socket                      socket;
    private AtomicReference<ThreadPool> parent;
    private MessageBuffer               messages;
    public Long                         thread_id;

    public ExecutionThread(Socket s, AtomicReference<ThreadPool> p) {
        this.socket     = s;
        this.parent     = p;
        this.thread_id  = Thread.currentThread().getId();
        this.messages   = new MessageBuffer();

        System.out.printf("Thread %d in the game now baby\n", Thread.currentThread().getId());
    }

    public void send_message(ServerMessage msg) {
        messages.waitForLock();
        messages.add(msg);
        messages.freeLock();
    }

    public void quit() {
        this.running    = false;
    }

    public void run() {
        this.running    = true;
        
        while (running && !socket.isClosed()) {
            // socket handling
            try {
                
                if (this.socket.getInputStream().available() > 0) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

                    String new_msg = reader.readLine();
                    System.out.printf("Read: %s\n", new_msg);

                    ArrayList<Byte> associated_data = new ArrayList<>();
                    for (char c : new_msg.toCharArray()) {
                        associated_data.add((byte) c);
                    }

                    parent.get().send_message( new ServerMessage( (byte) 0x01, Thread.currentThread().getId(), associated_data ) );
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (messages.has_items()) {
                messages.waitForLock();

                for (ServerMessage sm : messages) {
                    if (sm.message == (byte) 0x02) {
                        try {
                            PrintWriter writer = new PrintWriter(this.socket.getOutputStream(), true);

                            writer.println(sm.associated_data.toString());

                        } catch (IOException e) { e.printStackTrace(); }

                    }

                }

                messages.freeLock();

            }
            
        }

        if (!running) try { socket.close(); } catch (IOException e) { e.printStackTrace(); };
    }
}
