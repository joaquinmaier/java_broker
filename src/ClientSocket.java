import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSocket 
{
    private Socket          socket;
    private AtomicBoolean   available;
    private Long            owner_thread;

    public ClientSocket(String input_ip, int port) throws IOException {
        this.socket         = new Socket(input_ip, port);
        this.available      = new AtomicBoolean(true);
        this.owner_thread   = (long) 0;
    }

    public final Long owner() { return owner_thread; }

    public void take(Long thread_id) {
        while (!available.get()) {}

        this.available.set(false);
        this.owner_thread = thread_id;

    }

    public boolean try_take(Long thread_id) {
        if (!available.get()) return false;

        this.available.set(false);
        this.owner_thread = thread_id;

        return true;
    }

    public void free(Long thread_id) {
        if (this.owner_thread == thread_id) {
            this.owner_thread = (long) 0;
            this.available.set(true);

        }
    }

    public void wait_for_occupation() {
        while (available.get()) {}
    }

    public Socket get() {
        return socket;
    }

    public void close() throws IOException {
        socket.close();
    }

    public final boolean isAvailable() {
        return available.get();
    }

}
