import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadPool
{
    private ExecutionThread[] threads;
    private SocketBuffer socket_buffer;

    public ThreadPool(int max_threads) {
        this.socket_buffer = new SocketBuffer();
        this.threads = new ExecutionThread[max_threads];

        for (int i = 0; i < max_threads; i++) {
            this.threads[i] = new ExecutionThread( new AtomicReference<>(socket_buffer) );
            this.threads[i].start();
        }
    }

    public void handle_connection(Socket socket) {
        this.socket_buffer.add_socket(socket);
    }
}
