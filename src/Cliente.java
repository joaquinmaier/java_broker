import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import stind.*;

public class Cliente
{
    Socket client_socket;

    public Cliente(STIND stind) throws IOException {
        System.out.print("IP: ");
        String input_ip = stind.read_line().unwrap_or( Throwable::printStackTrace );

        this.client_socket = new Socket(input_ip, 8081);
    }

    public void send_message(String message) throws IOException {
        OutputStream output = client_socket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);

        writer.println(message);

        InputStream input = client_socket.getInputStream();
        BufferedReader reader = new BufferedReader( new InputStreamReader(input) );

        System.out.println(reader.readLine());
    }

    public void close() throws IOException { this.client_socket.close(); }
}
