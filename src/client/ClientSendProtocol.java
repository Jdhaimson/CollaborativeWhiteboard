package client;

import java.io.IOException;
import java.io.PrintWriter;

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
