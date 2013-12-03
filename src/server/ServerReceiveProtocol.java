package server;

import java.net.Socket;

public class ServerReceiveProtocol implements Runnable {
    
    private final Socket socket;
    private final Server server;
    private final String username;
    
    public ServerReceiveProtocol(Socket socket, Server server, String username) {
        this.socket = socket;
        this.server = server;
        this.username = username;
    }
    
    /**
     * Waits on the client to send data then calls the appropriate request handler
     */
    @Override
    public void run() {
    }
    
    /**
     * Sends a command in string format to all clients with server.updateClients
     * Parses the command into a Command and calls server.updateBoard
     */
    public void updateBoard() {
    }
    
    /**
     * Calls server.newBoard
     */
    public void newBoard() {
    }
    
    /**
     * Calls server.switchBoard
     */
    public void switchBoard() {
    }
    
    /**
     * Closes connections and calls server.exit
     */
    public void exit() {
    }
    
    /**
     * Calls server.enter
     */
    public void enter() {
    }

}
