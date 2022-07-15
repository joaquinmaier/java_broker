import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageBuffer implements Iterable<ServerMessage>
{
    //private ReentrantLock               lock;
    private ArrayList<ServerMessage>    messages;
    private AtomicBoolean               reject_items;

    public MessageBuffer() {
        this.messages               = new ArrayList<>();
        this.reject_items           = new AtomicBoolean(false);
    }

    public boolean has_items()                  { return messages.size() > 0; }

    public Iterator<ServerMessage> iterator()   { return new MessageBufferIterator(); }

    public int size()                           { return this.messages.size(); }

    private ServerMessage get(int i)            { return this.messages.get(i); }

    public void reject_new_messages()           { this.reject_items.set(true); }

    public void accept_new_messages()           { this.reject_items.set(false); }
    

    public void add(ServerMessage s) {
        // Puts the thread in a loop until it starts accepting items again.
        // I hate locks
        while (reject_items.get()) {}

        this.messages.add(s);
    }

    public void remove(ServerMessage s) {
        this.messages.remove(s);
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
