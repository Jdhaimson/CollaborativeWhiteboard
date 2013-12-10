package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import command.Command;


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
     * Check Users = "check username boardName"
     * 
     * 
     * Sends: 
     * 
     * Update Users = "users boardName user1 user2 user3..."
     * Update Available Boards = "boards board1 board2 board3"
     * Draw = "draw boardName command param1 param2 param3"
     *      Example: "draw boardName drawLineSegment x1 y1 x2 y2 color width"
     * Check Users = "check username boardName boolean"
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
     * Check Users = "check username boardName"
     * 
     * @param input message from client
     * @return message to client
     * @throws IOException 
     */
    private String handleRequest(String input) throws IOException, IllegalArgumentException {
        
    	String nameReg = "[a-zA-Z0-9\\.]+";
    	String regex = "(boards)|(new "+nameReg+")|(switch "+nameReg+" "+nameReg+" "+nameReg+")|"
    			+ "(exit "+nameReg+")|(users "+nameReg+")|"
    			+ "(check "+nameReg+" "+nameReg+")|"
    			+ "(draw "+nameReg+"( "+nameReg+")+)|"
    			+ "(users "+nameReg+")";
        
        if ( ! input.matches(regex)) {
            // invalid input
        	System.out.println("Invalid input");
            return null;
        }

        String[] tokens = input.split(" ");
        
        // Get Boards
        if (tokens[0].equals("boards")) {
        	return "boards " + server.getBoards();
        } //New Board
        else if (tokens[0].equals("new")) {
        	String boardName = tokens[1];
        	return "new " + boardName + " " + String.valueOf(server.newBoard(boardName));
        } // Switch Board
        else if (tokens[0].equals("switch")) {
            String userName = tokens[1];
            String oldBoardName = tokens[2];
            String newBoardName = tokens[3];
            String newLine = System.getProperty("line.separator");
            List<Command> commands = server.switchBoard(userName, oldBoardName, newBoardName);
        	String str =  "switch " + userName + " " + oldBoardName + " " + newBoardName + newLine;
        	for (Command command: commands) {
        	    str += command.toString() + newLine;
        	}
        	return str;
        }
        // Exit 
        else if (tokens[0].equals("exit")) {
            
            String username = tokens[1];
            server.exit(username);
            return "exit " + username;
        } // Draw Command 
        else if (tokens[0].equals("draw")) {
            String boardName = tokens[1];
            Command command = new Command(input);
            server.updateBoard(boardName, command);
            server.sendDrawCommand(command);
            return "draw";
        } // Check User
        else if (tokens[0].equals("check")) {
            
            String boardName = tokens[2];
            String username = tokens[1];
            return "check " + username + " " + boardName + " " + String.valueOf(server.checkUser(username, boardName));
        } // Get Users
        else if (tokens[0].equals("users")) {
            String boardName = tokens[1];
            return "users "+boardName+" "+server.getUsers(boardName);
        }
        


        // Should never get here--make sure to return in each of the valid cases above.
        throw new UnsupportedOperationException();
    }
    
    
    

}
