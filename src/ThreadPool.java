import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadPool extends Thread
{
    private volatile boolean                    running;
    private ArrayList<ExecutionThread>          threads;
    private SocketBuffer                        sockets;
    private MessageBuffer                       messages;

    public ThreadPool() {
        this.threads = new ArrayList<>();
        this.sockets = new SocketBuffer();
        this.running = false;
        this.messages = new MessageBuffer();
    }

    public void add_socket(Socket socket) {
        this.sockets.add(socket);
    }

    //public void send_confirmation() { this.messages.add(new ServerMessage((byte)0x01, "main", "Hello from the server!")); }

    public void handle_sockets() {
        this.sockets.reject_new_sockets();

        for (Socket s : sockets) {
            try {
                ExecutionThread new_thread = new ExecutionThread(s, new AtomicReference<>(this));
                this.threads.add(new_thread);
                this.threads.get( this.threads.indexOf(new_thread) ).start();

                String connected_message = "Client " + this.threads.get( this.threads.indexOf(new_thread) ).client_id + " connected!";
                this.messages.add(new ServerMessage((byte)0x01, ":", connected_message));

                sockets.remove(s);

            } catch (IOException e) { e.printStackTrace(); }

        }

        this.sockets.accept_new_sockets();
    }

    public void handle_messages() {
        if (sockets.size() > 0) return;     // Sockets must be handled before messages

        messages.reject_new_messages();
        for (ServerMessage msg : messages) {
            switch (msg.message)
            {
                case 0x00:
                    // ? 0x00 => The socket is closed, destroy the thread.
                    String c_id = msg.sender;

                    // Kill the thread
                    for (ExecutionThread thread : threads) {
                        if (thread.client_id == c_id) {
                            System.out.println("\033[0;33mDESTROYING CHILD\033[0m");
                            thread.quit();
                            try { thread.join(); } catch (InterruptedException e) {}

                            threads.remove(thread);
                            break;
                        }
                    }

                    this.messages.remove(msg);
                    break;
                
                case 0x01:
                    // ? 0x01 => New message received, send it to other threads
                    // Simply re-send the message to the other ExecutionThreads, they will send the
                    // message to their respective sockets

                    System.out.println("\033[0;33mGOT MESSAGE, RE-SENDING\033[0m");

                    String c_id2 = msg.sender;

                    // ? 0x02 => Send this message to the client
                    ServerMessage modified_msg = new ServerMessage((byte) 0x02, msg.sender, msg.associated_data);

                    for (ExecutionThread thread : threads) {
                        if (thread.client_id != c_id2) thread.send_message(modified_msg);

                    }

                    this.messages.remove(msg);

                    System.out.println("\033[0;33mDONE\033[0m");

                    break;

                default: break;

            }
        }
        messages.accept_new_messages();
    }

    public void send_message(ServerMessage msg)
    {
        messages.add(msg);
    }

    // Logic of the ThreadPool
    public void run() {
        this.running = true;

        while (running) {
            if (this.sockets.has_items()) {
                System.out.println("\033[0;33mHANDLING SOCKETS\033[0m");
                this.handle_sockets();

            }
            //* .
            if (this.messages.has_items()) {
                this.handle_messages();

            }
        }
    }

    public void close() {
        this.running = false;

        for (ExecutionThread t : threads) {
            t.quit();
            try { t.join(); } catch (InterruptedException e) {}
        }
    }
}
