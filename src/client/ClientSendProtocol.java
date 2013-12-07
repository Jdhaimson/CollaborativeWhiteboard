package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ClientSendProtocol implements Callable {
    
    private final Socket socket;
    private final String message;
    
    public ClientSendProtocol(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }
    
    /**
     * Sends message to server over a PrintWriter
     * @throws IOException 
     */
    @Override
    public String call() throws IOException {
    	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.write(message);
        // All responses are one line
        return in.readLine();
    }

}
