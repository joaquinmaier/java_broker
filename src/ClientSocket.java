import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientSocket 
{
    private ReentrantLock           lock;
    private Socket                  socket;
    private Condition               condition_available;
    private Condition               condition_occupied;
    private AtomicBoolean           available;

    public ClientSocket(String input_ip, int port) throws IOException {
        this.socket                 = new Socket(input_ip, port);
        this.lock                   = new ReentrantLock();
        this.available              = new AtomicBoolean(true);

        this.condition_available    = lock.newCondition();
        this.condition_occupied     = lock.newCondition();
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
        if (lock.isHeldByCurrentThread()) return;
        this.lock.lock();
        this.available.set(false);
    }

    public void freeLock() {
        if (lock.isHeldByCurrentThread()) {
            this.lock.unlock();
            this.available.set(true);
        }
    }

    public void waitForLock() {
        System.out.printf("Getting the lock -- %d\n", Thread.currentThread().getId());

        acquireLock();
        
        System.out.printf("Got the lock -- %d\n", Thread.currentThread().getId());
    }

    public void waitForOccupiedLock() {
        System.out.println("Waiting for the lock to be occupied");

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
