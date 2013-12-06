package server;

import java.net.Socket;
import java.util.Hashtable;

import Command.Command;

public class Server {
    
    //stores all the boards created as canvases associated with names
    private Hashtable<String, Board> boards = new Hashtable<String, Board>();
    private Socket[] clients;
    
    
    /**
     * Iterates through all the sockets and sends the command to each
     * @param socket: the one socket that sent the command to the server in the first place and thus does not need to be updated
     */
    public synchronized void updateClients(String command) {
        //TODO
    }
    
    /**
     * Add the command on the server's queue of commands
     * Requires valid board name
     * @param boardName: the board to draw on
     * @param command: the command to perform on the board
     */
    public void updateBoard(String boardName, Command command) {
        boards.get(boardName).addCommand(command);
    }
    
    /**
     * Checks if the board name is unique
     * Creates a new board with the specified board name
     * @param boardName: the name of the new board
     * @return: whether or not the new board was successfully made
     */
    public synchronized boolean newBoard(String boardName) {
        if(boards.contains(boardName)) {
            return false;
        } else {
            boards.put(boardName, new Board());
            return true;
        }
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
        //TODO
    }
    
    /**
     * Removes the user from all boards
     * @param username: the username of the user exiting
     */
    public synchronized void exit(String username) {
        for(String boardName : boards.keySet()) {
            Board board = boards.get(boardName);
            board.deleteUser(username);
        }
    }
    
    /**
     * Adds the user to a board for the first time
     * @param username: the entering user
     * @param boardName: the board they have chosen to enter
     */
    public synchronized void enter(String username, String boardName) {
        boards.get(boardName).addUser(username);
    }
    
    /**
     * Gets a list of all the board names
     * @return: a list of a all the board names
     */
    public synchronized String[] getBoards() {
        return boards.keySet().toArray(new String[0]);
    }
    
    /**
     * Checks if the username is unique and if it is, return true and enter the user
     * @param username: the username to check
     * @param boardName: the board the user wants to enter
     * @return: whether or not the user entered successfully
     */
    public boolean checkUsers(String username, String boardName) {
        boolean unique = true;
        for (String board : boards.keySet()) {
            if (!boards.get(board).checkUsername(username)) {
                unique = false;
            }
        }
        if (unique == true) {
            enter(username, boardName);
            return true;
        } else {
            return false;
        }
    }
    
    public Socket[] getClients() {
        return clients;
    }
    
    public Board getCommands(String boardName) {
        return boards.get(boardName);
    }
    
}
