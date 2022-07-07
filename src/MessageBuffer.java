import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MessageBuffer implements Iterable<ServerMessage>
{
    private ReentrantLock               lock;
    private ArrayList<ServerMessage>    messages;
    private Condition                   condition_available;
    private Condition                   condition_hasitems;
    private AtomicBoolean               available;

    public MessageBuffer() {
        this.lock                   = new ReentrantLock();
        this.messages               = new ArrayList<>();
        this.condition_available    = lock.newCondition();
        this.condition_hasitems     = lock.newCondition();
        this.available              = new AtomicBoolean(true);
    }

    public boolean has_items()                  { return messages.size() > 0; }

    public Iterator<ServerMessage> iterator()   { return new MessageBufferIterator(); }

    public int size()                           { return this.messages.size(); }

    private ServerMessage get(int i)            { return this.messages.get(i); }

    private void acquireLock() 
    {
        this.lock.lock();
        this.available.set(false);
    }

    public void freeLock() 
    {
        this.lock.unlock();
        this.available.set(true);
    }

    public final ReentrantLock getLock()        { return this.lock; }

    // Waits for the lock to be available, and gets it.
    public void waitForLock() 
    {
        while (!available.get()) {
            try {
                condition_available.await();

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        lock.lock();
        acquireLock();
        condition_available.signal();    
    }

    // Waits for there to be items in the buffer
    public void waitForItems()
    {
        while (messages.size() == 0) {
            try {
                condition_hasitems.await();

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        condition_hasitems.signal();
    }

    public void add(ServerMessage s) {
        if (lock.isHeldByCurrentThread()) {
            this.messages.add(s);

        }
    }

    // Iterator type
    public class MessageBufferIterator implements Iterator<ServerMessage>
    {
        private int index = 0;

        public boolean hasNext() { return index < size(); }

        public ServerMessage next() {
            return get(index++);
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet!");
        }
    }
}
