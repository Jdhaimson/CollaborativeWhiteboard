package server;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Command.Command;

public class Board {
    // image where the user's drawing is stored
    private List<Command> commands = new LinkedList<Command>();
    private List<String> users = new LinkedList<String>();
    
    public Board() {
    	
    }
    
    public List<Command> getCommands() {
        return commands;
    }
    
    public void addCommand(Command command) {
        this.commands.add(command);
    }
    
    public void deleteUser(String username) {
        Iterator<String> it = users.iterator();
        String user;
        while(it.hasNext()) {
        	user = (String) it.next();
            if (user.equals(username)) {
                it.remove();
            }
        }
    }
    
    public void addUser(String username) {
        users.add(username);
    }
    
    public boolean checkUsername(String username) {
        return users.contains(username);
    }
    
    public String[] getUsers() {
    	String[] userArray = new String[users.size()];
        return users.toArray(userArray);
    }

}
