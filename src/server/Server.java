package server;

import java.net.Socket;
import java.util.Hashtable;

import Command.Command;

public class Server {
    
    //stores all the boards created as canvases associated with names
    private Hashtable<String, SimpleCanvas> boards = new Hashtable<String, SimpleCanvas>();
    Socket[] clients;
    
    /**
     * Iterates through all the sockets except for the socket given as an argument and sends the command to each
     * @param command: the string to send to each socket
     * @param socket: the one socket that sent the command to the server in the first place and thus does not need to be updated
     */
    public synchronized void updateClients(String command, Socket socket) {
    }
    
    /**
     * Performs the command on the server's version of the canvas
     * @param boardName: the board to draw on
     * @param command: the command to perform on the board
     */
    public void updateBoard(String boardName, Command command) {
    }
    
    /**
     * Creates a new board with the specified boardName
     * Switches the user from their previous board to this one
     * Requires a unique board name
     * @param boardName: the name of the new board
     * @param username: the name of the user who created the board
     */
    public void newBoard(String boardName, String username) {
    }
    
    /**
     * Removes the user from the old board and adds the user to the new board
     * Updates the user's canvas to the BufferedImage of the new board
     * @param username: the username of the user making the switch
     * @param socket: the socket for the user, to send messages to
     * @param oldBoardName: the name of the board the user is switching from
     * @param newBoardName: the name of the board the user is switching to
     */
    public void switchBoard(String username, Socket socket, String oldBoardName, String newBoardName) {
    }
    
    /**
     * Removes the user from all boards
     * @param username: the username of the user exiting
     */
    public void exit(String username) {
    }
    
    /**
     * Adds the user to a board for the first time
     * @param username: the entering user
     * @param boardName: the board they have chosen to enter
     */
    public void enter(String username, String boardName) {
    }
    
}
