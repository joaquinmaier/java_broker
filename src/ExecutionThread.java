import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

public class ExecutionThread extends Thread
{
    private volatile boolean running;
    private AtomicReference<SocketBuffer> socket_buffer;

    public ExecutionThread(AtomicReference<SocketBuffer> s) {
        this.socket_buffer = s;
        this.running = false;
    }

    public void quit() {
        this.running = false;
    }

    public void run() {
        this.running = true;

        while (running) {
            if (this.socket_buffer.get().has_items().get()) {
                Socket socket = this.socket_buffer.get().get_socket();

                System.out.printf("Got socket -- %d\n", Thread.currentThread().getId());

                try {
                    BufferedReader br = new BufferedReader( new InputStreamReader( socket.getInputStream() ));

                    String read = br.readLine();

                    System.out.println(read);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
