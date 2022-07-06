import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class SocketBuffer
{
    private ReentrantLock       socket_lock;
    private ArrayList<Socket>   sockets;
    private AtomicBoolean       has_items;

    public SocketBuffer() {
        this.socket_lock    = new ReentrantLock();
        this.sockets        = new ArrayList<>();
        this.has_items      = new AtomicBoolean(false);
    }

    public final ArrayList<Socket> get_sockets() { return sockets; }

    public AtomicBoolean has_items() {
        return this.has_items;
    }

    public Socket get_socket() {
        socket_lock.lock();

        if (this.has_items.get()) {
            Socket s = this.sockets.get( this.sockets.size() - 1 );
            this.sockets.remove(this.sockets.size() - 1);

            if (this.sockets.isEmpty())
                this.has_items.set(false);

            return s;
        }

        socket_lock.unlock();

        return null;
    }

    public void add_socket(Socket socket) {
        socket_lock.lock();

        this.sockets.add(socket);

        if (!this.has_items.get()) this.has_items.set(true);

        socket_lock.unlock();
    }
}
