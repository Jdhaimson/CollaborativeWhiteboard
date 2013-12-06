package server;

import java.net.Socket;

import Command.Command;

public class ServerProtocol implements Runnable {
    
    /*
     * Receives:
     * 
     * New Board = "new boardName"
     * Switch Board = "switch username newBoardName"
     * Exit = "exit username"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Get Users = "users boardName"
     * Get boards = "boards"
     * Check Users = "check username boardName"
     * 
     * 
     * Sends: 
     * 
     * Update Users = "users boardName user1 user2 user3..."
     * Update Available Boards = "boards board1 board2 board3"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Check Users = "check username boolean"
     * New Board = "new boardName boolean"
     */
    
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
        //TODO
    }
    
    /**
     * Sends a command in string format to one board
     * @param client: the socket to send the command to
     * @param command: the command to send
     */
    public void commandBoard(Socket client, Command command) {
        String commandString = command.toString();
        //TODO
    }
    
    /**
     * Sends the same command to all of the clients
     * @param command: the command to send
     */
    public void commandAllBoards(Command command) {
        Socket[] clients = server.getClients();
        for (Socket client : clients) {
            commandBoard(client, command);
        }
    }
    
    public void updateBoard(String boardName) {
        CommandQueue commands = server.getCommands(boardName);
        for (Command command : commands.getCommands()) {
            commandBoard(this.socket, command);
        }
    }
    
    /**
     * Calls server.newBoard and returns the result to the client
     * format of return: "new boardName boolean"
     */
    public void newBoard() {
        //TODO
    }
    
    /**
     * Calls server.switchBoard
     * Calls updateBoard (if you're switching boards you obviously want to update too)
     */
    public void switchBoard() {
        //TODO
    }
    
    /**
     * Closes connections and calls server.exit
     */
    public void exit() {
        //TODO
    }
    
    /**
     * Calls server.getBoards and returns the result to the client
     * Format of response: "boards board1 board2 board3"
     */
    public void getBoards() {
        //TODO
    }
    
    /**
     * Calls server.checkUsers and returns the result to the client
     * format of return: "check username boolean"
     */
    public void checkUsers() {
        //TODO
    }
    
    /**
     * Returns the new list of users for a board to the client
     * format of response: "users boardName user1 user2 user3"
     */
    public void updateUsers() {
        //TODO
    }

}
