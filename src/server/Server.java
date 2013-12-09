package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import Command.Command;

public class Server {
    
    //stores all the boards created as canvases associated with names
    private Hashtable<String, Board> boards = new Hashtable<String, Board>();
    private List<Socket> clients = new LinkedList<Socket>();
    private final ServerSocket serverSocket;
    
    /**
     * Create our server on port port
     * @param port: port for server to listen on
     * @throws IOException 
     */
    public Server(int port) throws IOException {
    	serverSocket = new ServerSocket(port);
    	this.newBoard("test");
    }
    
    
 
    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
    	System.out.println("Server serving");
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            clients.add(socket);

            // create new thread for each connection
            new Thread(new ServerProtocol(socket, this)).start();
        }
    }
    
    /**
     * Iterates through all the sockets and sends the command to each
     * 
     * @param socket
     *            the one socket that sent the command to the server in the
     *            first place and thus does not need to be updated
     */
    public synchronized void updateClients(String command) {
        //TODO
    }
    
    /**
     * Add the command on the server's queue of commands Requires valid board
     * name
     * 
     * @param boardName
     *            the board to draw on
     * @param command
     *            the command to perform on the board
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
        if(boards.containsKey(boardName)) {
        	return false;
        } else {
            boards.put(boardName, new Board());
            return true;
        }
    }
    
    /**
     * Test method
     */
    public void sendDrawCommand(Command command) {
    	for (Socket client: clients) {
    		try {
    			if (!client.isClosed()) {
    				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
					out.println(command.toString());
    			}
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    /**
     * Gets the users from a board
     * 
     * @param boardName
     * @return String[] of users from the board
     */
    public String getUsers(String boardName) {
        Board board = boards.get(boardName);
        String[] users = board.getUsers();
        StringBuilder usersString = new StringBuilder("");
        for (String user: users) {
            usersString.append(user + " ");
        }
        
        if(usersString.length() > 0) {
            usersString.deleteCharAt(usersString.length() - 1);
        }
        
        return usersString.toString();
    }
    
    /**
     * Removes the user from the old board and adds the user to the new board.
     * 
     * @param username
     *            the username of the user making the switch
     * @param oldBoardName
     *            the name of the board the user is switching from
     * @param newBoardName
     *            the name of the board the user is switching to
     * @return
     *          List of Commands of the new Board the user is switching to           
     */
    public List<Command> switchBoard(String username, String oldBoardName, String newBoardName) {
        boards.get(oldBoardName).deleteUser(username);
        boards.get(newBoardName).addUser(username);
        return boards.get(newBoardName).getCommands();
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
        System.out.println(boardName);
        Board board = boards.get(boardName);
        board.addUser(username);
    }
    
    /**
     * Gets a list of all the board names
     * @return: a list of a all the board names
     */
    public synchronized String getBoards() {
        String[] boardsArray = boards.keySet().toArray(new String[0]);
       
        StringBuilder boardsString = new StringBuilder("");
        for (String board: boardsArray) {
        	boardsString.append(board + " ");
        }
        
        if(boardsString.length() > 0) {
        	boardsString.deleteCharAt(boardsString.length() - 1);
        }
        
        return boardsString.toString();
    }
    
    /**
     * Checks if the username is unique and if it is, return true and enter the user
     * @param username: the username to check
     * @param boardName: the board the user wants to enter
     * @return: whether or not the user entered successfully
     */
    public boolean checkUser(String username, String boardName) {
        boolean unique = true;
        //System.out.println("enter: "+username);
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
    
    public List<Socket> getClients() {
        return clients;
    }
    
    public Board getCommands(String boardName) {
        return boards.get(boardName);
    }
    
    public static void main(String[] args) {
    	Server server;
		try {
			server = new Server(4444);
			server.serve();
		} catch (IOException e) {
			System.out.println("You pooped up");
			e.printStackTrace();
		}
    	
    }
    
}
