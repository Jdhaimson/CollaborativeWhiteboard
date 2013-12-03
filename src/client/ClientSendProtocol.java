package client;

import java.net.Socket;

public class ClientSendProtocol implements Runnable {
    
    private final Socket socket;
    private final String message;
    
    public ClientSendProtocol(Socket socket, String message) {
        this.socket = socket;
        this.message = message;
    }
    
    /**
     * Sends message to server over a PrintWriter
     */
    @Override
    public void run() {
    }

}
