import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SocketBuffer implements Iterable<Socket>
{
    //private ReentrantLock       socket_lock;
    private ArrayList<Socket>   sockets;
    private AtomicBoolean       reject_items;

    public SocketBuffer() {
        this.sockets                = new ArrayList<>();
        this.reject_items           = new AtomicBoolean(false);
    }

    public boolean has_items()          { return sockets.size() > 0; }

    public Iterator<Socket> iterator()  { return new SocketBufferIterator(); }

    public int size()                   { return this.sockets.size(); }

    private Socket get(int i)           { return this.sockets.get(i); }

    public void add(Socket s) {
        while (this.reject_items.get()) {}

        this.sockets.add(s);
    }

    public void remove(Socket s) {
        this.sockets.remove(s);
    }

    public void reject_new_sockets()    { this.reject_items.set(true); }

    public void accept_new_sockets()    { this.reject_items.set(false); }

    // Iterator type
    public class SocketBufferIterator implements Iterator<Socket>
    {
        private int index = 0;

        public boolean hasNext() { return index < size(); }

        public Socket next() {
            return get(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet!");
        }
    }
}
