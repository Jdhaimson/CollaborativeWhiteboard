package Command;

public class Command {
    private final String command;
    private final String[] arguments;
    
    public Command(String command, String[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }
    
    /**
     * Parses a string received from the client that has already been determined to be a draw command
     * @param commandString: the string in the format "draw boardName command arg1 arg2 arg3..."
     * @return a Command object with the command and the arguments
     */
    public static Command parseCommand(String commandString) {
        String[] elements = commandString.split(" ");
        String[] arguments = new String[elements.length-3];
        for (int i=3; i<elements.length;i++) {
            arguments[i-1] = elements[i];
        }
        Command command = new Command(elements[2], arguments);
        return command;
    }
    
    /**
     * Determines whether the object is a Canvas or SimpleCanvas, then finds the method with a name matching the command name,
     * then invokes the method with the command's arguments
     * @param canvas: the object that the method will be invoked on
     */
    public void invokeCommand(Object canvas) {
    }
}
