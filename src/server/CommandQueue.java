package server;

import java.util.LinkedList;

import Command.Command;

public class CommandQueue {
    // image where the user's drawing is stored
    private LinkedList<Command> commands = new LinkedList<Command>();
    private String[] users;
    
    public CommandQueue() {
        users = new String[0];
    }
    
    public LinkedList<Command> getCommands() {
        return commands;
    }
    
    public void addCommand(Command command) {
        this.commands.add(command);
    }
    
    public void deleteUser(String username) {
        int newUserNumber = users.length;
        for (String user : users) {
            if (user.equals(username)) {
                newUserNumber--;
            }
        }
        String[] newUsers = new String[newUserNumber];
        int count = 0;
        for (String user : users) {
            if (!user.equals(username)) {
                newUsers[count] = user;
                count++;
            }
        }
        users = newUsers;
    }
    
    public void addUser(String username) {
        String[] newUsers = new String[users.length+1];
        for (int i=0; i<users.length; i++) {
            newUsers[i]=users[i];
        }
        newUsers[users.length] = username;
        users = newUsers;
    }
    
    public boolean checkUsername(String username) {
        boolean unique = true;
        for (String user : users) {
            if (user.equals(username)) {
                unique = false;
            }
        }
        return unique;
    }

}
