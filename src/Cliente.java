import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

import stind.*;

public class Cliente
{
    private ClientSocket            client_socket;
    private ClientListenerThread    listener;
    private Long                    thread_id;

    public Cliente(STIND stind) throws IOException {
        this.client_socket = new ClientSocket("127.0.0.1", 8080);
        this.thread_id = Thread.currentThread().getId();
        try { this.listener = new ClientListenerThread(new AtomicReference<>(client_socket)); } catch (IOException e) { e.printStackTrace(); }
        this.listener.start();
    }

    public void send_message(String message) throws IOException {
        if (!client_socket.isAvailable()) {
            listener.request_socket();
        }
        System.out.println("\033[0;35mWaiting for socket\033[0m");
        client_socket.take(thread_id);
        System.out.println("\033[0;35mGot Socket\033[0m");

        OutputStream output = client_socket.get().getOutputStream();
        PrintWriter writer = new PrintWriter(output, false);

        writer.println(message);
        writer.flush();

        client_socket.free(thread_id);
    }

    public void close() throws IOException { 
        listener.quit();
        try { listener.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        client_socket.take(thread_id);
        client_socket.close();
        client_socket.free(thread_id);
    }
}
