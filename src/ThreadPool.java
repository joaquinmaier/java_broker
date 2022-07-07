import java.net.Socket;
import java.nio.ByteBuffer;
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
        this.sockets.waitForLock();
        this.sockets.add(socket);
        this.sockets.freeLock();
    }

    public void handle_sockets() {
        for (Socket s : sockets) {
            this.threads.add( new ExecutionThread(s, new AtomicReference<>(this)) );
            this.threads.get( this.threads.size() - 1 ).start();
            sockets.remove(s);

        }
    }

    public void handle_messages() {
        for (ServerMessage msg : messages) {
            switch (msg.message) 
            {
                case 0x00:
                    // ! 0x00 => The socket is closed, destroy the thread.
                    // Convert the associated data (the thread_id) to a long
                    Long thread_id = ByteBuffer.allocate(Long.BYTES)
                                        .put( ServerMessage.get_byte_array(msg.associated_data) )
                                        .flip()
                                        .getLong();

                    // Kill the thread
                    for (ExecutionThread thread : threads) {
                        if (thread.thread_id == thread_id) {
                            thread.quit();
                            thread.interrupt();
                            threads.remove(thread);
                        }
                    }
                    break;
                
                case 0x01:
                    // ! 0x01 => New message received, send it to other threads
                    // Simply re-send the message to the other ExecutionThreads, they will send the
                    // message to their respective sockets
                    

                    Long tid = ByteBuffer.allocate(Long.BYTES)
                                        .put( ServerMessage.get_byte_array(msg.associated_data) )
                                        .flip()
                                        .getLong();

                    // ? 0x02 => Send this message to the client
                    ServerMessage modified_msg = new ServerMessage((byte) 0x02, msg.sender, msg.associated_data);

                    for (ExecutionThread thread : threads) {
                        if (thread.thread_id != tid) thread.send_message(modified_msg);

                    }

                    break;

                default: break;
                                        
            }
        }
    }

    public void send_message(ServerMessage msg)
    {
        messages.waitForLock();
        messages.add(msg);
        messages.freeLock();
    }

    // Logic of the ThreadPool
    public void run() {
        this.running = true;

        while (running) {
            if (this.sockets.has_items()) {
                this.sockets.waitForLock();     // Get the lock

                this.handle_sockets();          // Handle the sockets in the buffer

                this.sockets.freeLock();        // Free the lock

            }

            if (this.messages.has_items()) {
                this.messages.waitForLock();

                this.handle_messages();

                this.messages.freeLock();

            }
        }
    }

    public void close() {
        this.running = false;

        for (ExecutionThread t : threads) {
            t.quit();
            try { t.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
