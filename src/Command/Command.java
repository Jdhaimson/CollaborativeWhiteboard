package Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import server.SimpleCanvas;
import client.CanvasGUI;

public class Command {
    private final String command;
    private final String[] arguments;
    private final String boardName;
    
    public Command(String command, String[] arguments, String boardName) {
        this.command = command;
        this.arguments = arguments;
        this.boardName = boardName;
    }
    
    /**
     * Parses a string received from the client that has already been determined to be a draw command
     * @param commandString: the string in the format "draw boardName command arg1 arg2 arg3..."
     * @return a Command object with the command and the arguments
     */
    public Command(String commandString) {
        String[] elements = commandString.split(" ");
        String[] arguments = new String[elements.length-3];
        for (int i=3; i<elements.length;i++) {
            arguments[i-1] = elements[i];
        }
        this.command = elements[2];
        this.arguments = arguments;
        this.boardName = elements[1];
    }
    
    /**
     * Finds the method with a name matching the command name,
     * then invokes the method with the command's arguments
     * @param canvas: the object that the method will be invoked on
     */
    public void invokeCommand(Canvas canvas) {
        Method[] methods = Canvas.class.getMethods();
        Method method = null;
        for (int i=0; i<methods.length;i++) {
            if (methods[i].getName().equals(command)) {
                method = methods[i];
            }
        }
        if (method == null) {
            throw new RuntimeException("Command "+command+" not found.");
        } else {
            Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != arguments.length) {
                throw new RuntimeException("Incorrect number of arguments for given method.");
            } else {
                Object[] typedArgs = new Object[arguments.length];
                for (int i=0; i<typedArgs.length;i++) {
                    //TODO: can't cast primitive types like this, use if/else
                    typedArgs[i] = parameters[i].cast(arguments[i]);
                }
                try {
                    method.invoke(canvas, typedArgs);
                } catch (IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public boolean checkBoardName(String compareBoardName) {
        return this.boardName.equals(compareBoardName);
    }
}
