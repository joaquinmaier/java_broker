import java.io.*;
import java.util.concurrent.atomic.AtomicReference;

import stind.*;

public class Cliente
{
    private ClientSocket            client_socket;
    private ClientListenerThread    listener;
    private PrintWriter             writer;
    private Long                    thread_id;

    public Cliente(STIND stind) throws IOException {
        System.out.print("\033[0;32mIP: \033[0m");
        String dest_ip = stind.read_line().unwrap_or( Throwable::printStackTrace );

        this.client_socket          = new ClientSocket(dest_ip, 8081);
        this.writer                 = new PrintWriter( client_socket.get().getOutputStream(), false );
        this.thread_id              = Thread.currentThread().getId();
        try { this.listener         = new ClientListenerThread(new AtomicReference<>(client_socket)); } catch (IOException e) { e.printStackTrace(); }
        this.listener.start();
    }

    public void send_message(String message) throws IOException {
        if (!client_socket.isAvailable()) {
            listener.request_socket();
        }
        //System.out.println("\033[0;35mWaiting for socket\033[0m");
        client_socket.take(thread_id);
        //System.out.println("\033[0;35mGot Socket\033[0m");

        writer.println( cleanup_message( message ) );
        writer.flush();

        client_socket.free(thread_id);
    }

    public void close() throws IOException {
        listener.quit();
        try { listener.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        client_socket.take(thread_id);
        client_socket.close();
    }

    // A function that tries to clean up and fix commands if it can recognize them, usually very common mistakes/typos
    private static String cleanup_message( String original_message ) {
        if ( original_message.toLowerCase().startsWith( ".username=" ) ) {
            return cleanup_message( ".user=" + original_message.substring( original_message.indexOf('=') + 1 ).trim() );
        }

        if ( original_message.toLowerCase().startsWith( ".user=" ) ) {
            if ( original_message.trim().indexOf(' ') != -1 )   return original_message.substring( 0, original_message.indexOf('=') + 1 ).toLowerCase() + original_message.substring( original_message.indexOf('=') + 1 ).trim().replace( " ", "" );
            else                                                return original_message.substring( 0, original_message.indexOf('=') + 1 ).toLowerCase() + original_message.substring( original_message.indexOf('=') + 1 ).trim();
        }

        if ( original_message.toLowerCase().startsWith( ".exit" ) ) return ".exit";

        return original_message;
    }
}
