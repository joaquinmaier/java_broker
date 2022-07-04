import java.io.*;
import java.net.*;
import java.util.stream.Stream;

/*
public class Servidor
{
    private ServerSocket server_socket;
    private boolean closing = false;

    public Servidor() throws IOException {
        System.out.println("your father and your father");
        this.server_socket = new ServerSocket(8081);
        System.out.println("in the night");
    }

    public void listen() throws IOException {
        Socket          socket      = server_socket.accept();

        InputStream     input       = socket.getInputStream();
        BufferedReader  reader      = new BufferedReader( new InputStreamReader(input) );

        String          raw_data    = reader.readLine();
        System.out.printf("{ %s }", raw_data);

        OutputStream    output      = socket.getOutputStream();
        PrintWriter     writer      = new PrintWriter(output, true);
        writer.println("Ok!");

        if (raw_data.equals(".exit")) {
            this.closing = true;
        }

        socket.close();
    }

    public void close() throws IOException { this.server_socket.close(); }

    public boolean is_closing() { return this.closing; }
}
*/

public class Servidor
{
    private ServerSocket server_socket;
    private ThreadPool thread_pool;

    public Servidor() throws IOException {
        this.server_socket = new ServerSocket(8081);
        this.thread_pool = new ThreadPool(32);
        System.out.println("Server ready!");

    }

    public void listen() {
        Socket socket;

        try {
            socket = server_socket.accept();

        } catch (IOException e) {
            System.err.printf("IOException occurred creating socket: %s", e.getMessage());
            return;
        }

        thread_pool.handle_connection(socket);

    }
}