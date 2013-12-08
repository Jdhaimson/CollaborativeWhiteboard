package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import Command.Command;

public class ServerProtocol implements Runnable {
    
    /*
     * Receives:
     * 
     * New Board = "new boardName"
     * Switch Board = "switch username oldBoardName newBoardName"
     * Exit = "exit username"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Get Users = "users boardName"
     * Get boards = "boards"
     * Check Users = "check username"
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
        // handle the client
        try {
            handleConnection(socket);
        } catch (IOException e) {
            e.printStackTrace(); // but don't terminate
        } finally {
            try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                try {
                	System.out.println("Handle Request: " + line);
	            	String output = handleRequest(line);
	            	
	            	if(output != null) {
	            		out.println(output);
	            	}
	                
                } catch (IllegalArgumentException e) {
	                	e.printStackTrace();   
                }                
            }
        } finally {
            out.close();
            in.close();
        }
    }
    
    /**
     * Handler for client input, performing requested operations and returning an output message.
     * Receives:
     * 
     * New Board = "new boardName"
     * Switch Board = "switch username oldBoardName newBoardName"
     * Exit = "exit username"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Get Users = "users boardName"
     * Get boards = "boards"
     * Check Users = "check username"
     * 
     * @param input message from client
     * @return message to client
     * @throws IOException 
     */
    private String handleRequest(String input) throws IOException, IllegalArgumentException {
        
    	String nameReg = "[a-zA-Z0-9]+";
    	String regex = "(boards)|(new "+nameReg+")|(switch "+nameReg+" "+nameReg+")|(testHello)";
        
        if ( ! input.matches(regex)) {
            // invalid input
            return null;
        }
        
        String[] tokens = input.split(" ");
        
        if (tokens[0].equals("boards")) {
        	return "boards " + server.getBoards();
        } else if (tokens[0].equals("new")) {
        	String boardName = tokens[1];
        	return "new " + boardName + " " + String.valueOf(server.newBoard(boardName));
        } else if (tokens[0].equals("switch")) {
        	//server.switchBoard(username, oldBoardName, newBoardName);
        } else if (tokens[0].equals("testHello")) {
        	server.sendHello();
        	return "";
        }

        
        // Should never get here--make sure to return in each of the valid cases above.
        throw new UnsupportedOperationException();
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
        List<Socket> clients = server.getClients();
        for (Socket client : clients) {
            commandBoard(client, command);
        }
    }
    
    public void updateBoard(String boardName) {
        Board commands = server.getCommands(boardName);
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
