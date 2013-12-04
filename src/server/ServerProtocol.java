package server;

import java.net.Socket;

public class ServerProtocol implements Runnable {
    
    private final Socket socket;
    private final Server server;
    
    public ServerProtocol(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
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
     * Calls server.newBoard and returns the result to the client
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
     * Calls server.getBoards and returns the result to the client
     */
    public void getBoards() {
        
    }
    
    /**
     * Calls server.checkUsers and returns the result to the client
     */
    public void checkUsers() {
        
    }

}
