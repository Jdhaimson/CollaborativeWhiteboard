package client;

import java.io.BufferedReader;
import java.io.IOException;

import Command.Command;

public class ClientReceiveProtocol implements Runnable {
    
    private final BufferedReader in;
    private final Client client;
    private boolean isRunning = true;
    
    public ClientReceiveProtocol(BufferedReader in, Client client) {
        this.in= in;
        this.client = client;
    }
    
    /**
     * Waits for message from server and calls appropriate request handler
     */
    @Override
    public void run() {
    	// provide a way to kill thread
    	while(isRunning) {
	    	//handle the client
		    try {
		        handleConnection(in);
		    } catch (IOException e) {
		    	// Means connection has closed
		    }
    	}
    }
	    
    
    /**
     * Handle connection to server. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if connection has an error or terminates unexpectedly
     */
    private void handleConnection(BufferedReader in) throws IOException {        

        for (String line = in.readLine(); line != null; line = in.readLine()) {
        	System.out.println("Handle Request: " + line);
        	handleRequest(line);                
        }
    }
    
    /**
     * Handler for server input, performing requested operations and returning an output message.
     * Receives:
     * 
     * Update Users = "users boardName user1 user2 user3..."
     * Update Available Boards = "boards board1 board2 board3"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Check and add User = "checkAndAddUser username boardName boolean"
     * New Board = "newBoard boardName boolean"
     * 
     * @param input message from server
     * @return message to client
     * @throws IOException 
     */
    private void handleRequest(String input) throws IOException, IllegalArgumentException {
        
    	String nameReg = "[a-zA-Z0-9\\.]+";
    	String regex = "(draw "+nameReg+"( "+nameReg+")+)|(users( "+nameReg+")+)|(exit "+nameReg+")|"
    	        +"(boards( "+nameReg+")*)|(checkAndAddUser ("+nameReg+" "+nameReg+" (true|false)))|"
    	        +"(newBoard "+nameReg+" (true|false))|(switch "+nameReg+" "+nameReg+")|(testHello)";
    	System.out.println("input: "+input);
    	// make sure it's a valid input
        if (input.matches(regex)) {
            String[] tokens = input.split(" ");
            System.out.println("token 0: "+tokens[0]);
            if (tokens[0].equals("boards")) {
            	try {
					client.setBoards(client.parseBoardsFromServerResponse(input));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }  else if (tokens[0].equals("newBoard")) {
                try {
                    client.parseNewBoardFromServerResponse(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tokens[0].equals("checkAndAddUser")) {
                try {
                    client.parseNewUserFromServerResponse(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tokens[0].equals("users")) {
                try {
                    if (client.checkForCorrectBoard(input.split(" ")[1])) {
                        
                        client.setUsers(client.parseUsersFromServerResponse(input));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tokens[0].equals("exit")) {
                try {
                    client.completeExit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tokens[0].equals("draw")) {
                try {
                    Command command = new Command(input);
                    if (command.checkBoardName(client.getCurrentBoardName())) {
                        client.applyCommand(command);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
            }    
        }
   
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

    /**
     * Used to kill thread from outside
     */
    public void kill() {
    	isRunning = false;
    	// TODO: Fix exception thrown on window close
    }
    
}
