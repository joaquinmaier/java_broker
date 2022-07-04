import java.net.Socket;

public class ThreadPool
{
    private Thread[] threads;
    private int last_thread = 0;

    public ThreadPool(int max_threads) {
        this.threads = new Thread[max_threads];
    }

    public void handle_connection(Socket socket) {

    }
}
