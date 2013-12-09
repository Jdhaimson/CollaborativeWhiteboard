package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Callable;

public class ClientSendProtocol implements Runnable {
    
    private final PrintWriter out;
    private final String message;
    
    public ClientSendProtocol(PrintWriter out, String message) {
        this.out = out;
        this.message = message;
    }
    
    /**
     * Sends message to server over a PrintWriter
     * @throws IOException 
     */
    @Override
    public void run() {
        System.out.println("Make Request: "+message);
		out.println(message);
		
    }

}
