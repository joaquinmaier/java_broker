import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientSocket 
{
    private ReentrantLock lock;
    private Socket socket;
    private Condition condition_available;
    private Condition condition_occupied;
    private AtomicBoolean available;

    public ClientSocket(String input_ip, int port) throws IOException {
        this.socket = new Socket(input_ip, port);
        this.lock = new ReentrantLock();
        this.condition_available = lock.newCondition();
        this.condition_occupied = lock.newCondition();
        this.available = new AtomicBoolean(true);
    }

    public Socket get() {
        if (lock.isHeldByCurrentThread()) {
            return socket;

        } else {
            throw new RuntimeException("Thread does not own the lock");
        }
    }

    public void close() throws IOException {
        if (lock.isHeldByCurrentThread()) {
            socket.close();
            this.available.set(false);

        }
    }

    private void acquireLock() {
        this.lock.lock();
        this.available.set(false);
    }

    public void freeLock() {
        this.lock.unlock();
        this.available.set(true);
    }

    public void waitForLock() {
        while (!available.get()) {
            try {
                condition_available.await();

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        acquireLock();
        condition_available.signal();
    }

    public void waitForOccupiedLock() {
        while (available.get()) {
            try {
                condition_occupied.await();

            } catch (InterruptedException e) { e.printStackTrace(); }
        }

        condition_occupied.signalAll();
    }

    public final boolean isAvailable() {
        return available.get();
    }

    public final boolean isHeldByCurrentThread() {
        return lock.isHeldByCurrentThread();
    }
}
