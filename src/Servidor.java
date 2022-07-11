import java.io.*;
import java.net.*;

public class Servidor
{
    private ServerSocket server_socket;
    private ThreadPool thread_pool;

    public Servidor(int threads) throws IOException {
        this.server_socket = new ServerSocket(8081);
        this.thread_pool = new ThreadPool();
        thread_pool.start();
        System.out.println("Server ready!");

    }

    public void listen() {
        try {
            Socket socket = server_socket.accept();

            System.out.println("SERVER GOT A NEW SOCKET!");

            thread_pool.add_socket(socket);

            System.out.println("SOCKET DISPATCHED");

        } catch (IOException e) {
            System.err.printf("IOException occurred creating socket: %s", e.getMessage());
            return;
        }

    }
}