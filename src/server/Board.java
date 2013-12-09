package server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Command.Command;

/**
 * Object which represents a whiteboard stored on the server.  Stores a list
 * of all commands ever sent to whiteboard so that it can be recreated on all
 * clients.  Also stores all current users connected to this whiteboard.
 * 
 * Concurrency Argument:
 *   - This class is made concurrent by the monitor pattern
 * 
 * @author Josh
 *
 */
public class Board {

    private LinkedList<Command> commands = new LinkedList<Command>();
    private List<String> users = new LinkedList<String>();
    
    /**
     * Returns all commands ever sent to this board
     * @return
     */
    public synchronized LinkedList<Command> getCommands() {
        return commands;
    }
    
    /**
     * Adds a command to the board
     * @param command
     */
    public synchronized void addCommand(Command command) {
        this.commands.add(command);
    }
    
    /**
     * Deletes user from board if user is in board
     * @param username
     */
    public synchronized void deleteUser(String username) {
    	Iterator<String> it = users.iterator();
    	while(it.hasNext()) {
    		String user = it.next();
    		if (user.equals(username)) {
    			it.remove();
    		}
    	}
    }
    
    /**
     * Adds user to board
     * @param username
     */
    public synchronized void addUser(String username) {
        users.add(username);
    }
    
    /**
     * Checks if username is available on this board
     * @param username
     * @return: true if username is not contained in board's users, false otherwise
     */
    public synchronized boolean checkUsernameAvailable(String username) {
        return !users.contains(username);
    }
    
    /**
     * Returns list of all users in board
     * @return
     */
    public synchronized String[] getUsers() {
        String[] usersArray = new String[users.size()];
    	return users.toArray(usersArray);
    }

}
