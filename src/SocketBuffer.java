import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SocketBuffer implements Iterable<Socket>
{
    private ReentrantLock       socket_lock;
    private ArrayList<Socket>   sockets;
    private Condition           condition_available;
    private Condition           condition_hasitems;
    private AtomicBoolean       available;

    public SocketBuffer() {
        this.socket_lock            = new ReentrantLock();
        this.sockets                = new ArrayList<>();
        this.available              = new AtomicBoolean(true);
        this.condition_available    = socket_lock.newCondition();
        this.condition_hasitems     = socket_lock.newCondition();
    }

    public boolean has_items()          { return sockets.size() > 0; }

    public Iterator<Socket> iterator()  { return new SocketBufferIterator(); }

    public int size()                   { return this.sockets.size(); }

    private Socket get(int i)           { return this.sockets.get(i); }

    private void acquireLock() 
    {
        this.socket_lock.lock();
        this.available.set(false);
    }

    public void freeLock() 
    {
        this.socket_lock.unlock();
        this.available.set(true);
    }

    public final ReentrantLock getLock() { return this.socket_lock; }

    // Waits for the lock to be available, and gets it.
    public void waitForLock() 
    {
        acquireLock(); 
    }

    // Waits for there to be items in the buffer
    public void waitForItems()
    {
        while (sockets.size() == 0) {
            try {
                condition_hasitems.await();

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        condition_hasitems.signal();
    }

    public void add(Socket s) {
        if (socket_lock.isHeldByCurrentThread()) {
            this.sockets.add(s);
            System.out.printf("Has Items: %b\n", this.has_items());

        }
    }

    public void remove(Socket s) {
        if (socket_lock.isHeldByCurrentThread()) {
            this.sockets.remove(s);

        }
    }

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
