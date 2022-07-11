import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

import stind.*;

public class Cliente
{
    private ClientSocket            client_socket;
    private ClientListenerThread    listener;

    public Cliente(STIND stind) throws IOException {
        System.out.print("IP: ");
        String input_ip = stind.read_line().unwrap_or( Throwable::printStackTrace );

        this.client_socket = new ClientSocket(input_ip, 8081);
        this.listener = new ClientListenerThread(new AtomicReference<>(client_socket));
        this.listener.start();
    }

    public void send_message(String message) throws IOException {
        if (!client_socket.isAvailable()) {
            listener.request_socket();

            client_socket.waitForLock();
            System.out.println("Got socket lock");
        }

        OutputStream output = client_socket.get().getOutputStream();
        PrintWriter writer = new PrintWriter(output, false);

        writer.println(message);
        writer.flush();

        client_socket.freeLock();
    }

    public void close() throws IOException { 
        listener.quit();
        listener.interrupt();

        client_socket.waitForLock();
        client_socket.close();
        client_socket.freeLock();
    }
}
