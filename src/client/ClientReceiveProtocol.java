package client;

import java.net.Socket;

public class ClientReceiveProtocol implements Runnable {
    
    private final Socket socket;
    private final Client client;
    
    public ClientReceiveProtocol(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
    }
    
    /**
     * Waits for message from server and calls appropriate request handler
     */
    @Override
    public void run() {
    }
    
    /**
     * Checks that the board is the correct one, parses the message into an array of users and calls client.setCanvasUsers
     * @param usersMessage: the message in the format "users boardName user1 user2 user3..."
     */
    public void updateUsers(String usersMessage) {
    }
    
    /**
     * Checks that the board is the correct one, parses the message into an array of boards and calls client.setBoards
     * @param boardsMessage: the message in the format "boards boardName board1 board2 board3..."
     */
    public void updateBoards(String boardsMessage) {
    }
    
    /**
     * Uses the Command class to create a command object and calls client.updateCanvasCommand
     * @param commandMessage: the message in the format "draw boardName command param1 param2 param3..."
     */
    public void commandCanvas(String commandMessage) {
    }

}
