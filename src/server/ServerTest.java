package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import command.Command;

import client.Client;

public class ServerTest {
    
    /*
     * Testing Strategy: 
     * 
     * -When a client connects, their socket is added to a list of sockets
     * -New board for a board whose name is unique adds the board and returns true
     * -New board for a board whose name is already taken returns false
     * -Update board adds a command to a board
     * -Get users for a board with no users returns an empty string
     * -Get users for a board with users returns the users separated with spaces
     * -Switch boards successfully deletes a user from a board and adds it to the correct one and returns the correct list of commands
     * -Exit successfully deletes the user from the correct board
     * -Enter adds the user to the correct board
     * -Get boards when there are no boards returns an empty string
     * -Get boards when there are boards returns the list of boards separated by spaces
     * -Check username returns false if the username is not unique (for all boards), and does not enter the user
     * -Check username returns true if the username is unique and enters the user
     */
    public Server makeServer() {
        Server server = null;
        try {
            server = new Server(4444);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return server;
    }
    

    
    //@Test
    public void testServe() {
        try {
            Server server = makeServer();
            Client client = new Client();
            List<Socket> clients = server.getClients();
            Socket clientSocket = client.getSocket();
            List<Socket> clientSocketAsList = new LinkedList<Socket>();
            clientSocketAsList.add(clientSocket);
            assertTrue(clients.equals(clientSocketAsList));
            server.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void newBoardTest() throws IOException {
        Server server = makeServer();
        //board1 adds correctly
        assertTrue(server.newBoard("board1"));
        assertTrue(server.getBoardsHashtable().containsKey("board1")
                && server.getBoardsHashtable().size() == 1);
        //board2 adds correctly
        assertTrue(server.newBoard("board2"));
        assertTrue(server.getBoardsHashtable().containsKey("board1") 
                && server.getBoardsHashtable().containsKey("board2")
                && server.getBoardsHashtable().size() == 2);
        //board2 will not add again
        assertTrue(!server.newBoard("board2"));
        assertTrue(server.getBoardsHashtable().containsKey("board1") 
                && server.getBoardsHashtable().containsKey("board2")
                && server.getBoardsHashtable().size() == 2);
        server.close();
    }
    
    @Test
    public void updateBoardTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        Command command = new Command("draw board1 drawLineSegment 50 50 60 60 0 10.0");
        server.updateBoard("board1", command);
        LinkedList<Command> commandList = new LinkedList<Command>();
        commandList.add(command);
        assertTrue(server.getBoard("board1").getCommands().equals(commandList));
        server.newBoard("board2");
        assertTrue(server.getBoard("board2").getCommands().isEmpty());
        server.close();
    }
    
    @Test
    public void getUsersTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        assertTrue(server.getUsers("board1").equals(""));
        server.getBoard("board1").setUsers(new String[] {"jessica"});
        assertTrue(server.getUsers("board1").equals("jessica"));
        server.getBoard("board1").setUsers(new String[] {"jessica", "juan", "josh"});
        assertTrue(server.getUsers("board1").equals("jessica juan josh"));
        server.close();
    }
    
    @Test
    public void switchBoardTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        Command command = new Command("draw board1 drawLineSegment 50 50 60 60 0 10.0");
        server.updateBoard("board1", command);
        server.newBoard("board2");
        server.getBoard("board2").setUsers(new String[] {"jessica"});
        List<Command> commandList = new LinkedList<Command>();
        commandList.add(command);
        assertTrue(server.switchBoard("jessica", "board2", "board1").equals(commandList));
        assertTrue(Arrays.equals(server.getBoard("board1").getUsers(), new String[] {"jessica"}));
        assertTrue(Arrays.equals(server.getBoard("board2").getUsers(), new String[0]));
        server.close();
    }
    
    @Test
    public void exitTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        server.newBoard("board2");
        server.getBoard("board1").setUsers(new String[] {"juan"});
        server.getBoard("board2").setUsers(new String[] {"josh"});
        server.exit("josh");
        assertTrue(Arrays.equals(server.getBoard("board1").getUsers(), new String[] {"juan"}));
        assertTrue(Arrays.equals(server.getBoard("board2").getUsers(), new String[0]));
        server.close();
    }
    
    @Test
    public void enterTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        server.newBoard("board2");
        server.enter("jessica", "board1");
        assertTrue(Arrays.equals(server.getBoard("board1").getUsers(), new String[] {"jessica"}));
        assertTrue(Arrays.equals(server.getBoard("board2").getUsers(), new String[0]));
        server.enter("juan", "board1");
        assertTrue(Arrays.equals(server.getBoard("board1").getUsers(), new String[] {"jessica", "juan"}));
        assertTrue(Arrays.equals(server.getBoard("board2").getUsers(), new String[0]));
        server.close();
    }
    
    @Test
    public void getBoardsTest() throws IOException {
        Server server = makeServer();
        assertTrue(server.getBoards().equals(""));
        server.newBoard("board1");
        assertTrue(server.getBoards().equals("board1"));
        server.newBoard("board2");
        server.newBoard("board3");
        ArrayList<String> boardList = new ArrayList<String>();
        boardList.add("board1");
        boardList.add("board2");
        boardList.add("board3");
        assertTrue(Arrays.asList(server.getBoards().split(" ")).containsAll(boardList));
        server.close();
    }
    
    @Test
    public void checkUsernameTest() throws IOException {
        Server server = makeServer();
        server.newBoard("board1");
        //try checking a user on a board
        assertTrue(server.checkUser("josh", "board1"));
        assertTrue(Arrays.equals(server.getBoard("board1").getUsers(), new String[] {"josh"}));
        //try checking another user for the same board
        assertTrue(server.checkUser("juan", "board1"));
        assertTrue(Arrays.toString(server.getBoard("board1").getUsers()).equals(Arrays.toString(new String[] {"josh, juan"})));
        //try entering the same user on the same board
        assertTrue(!server.checkUser("josh", "board1"));
        assertTrue(Arrays.toString(server.getBoard("board1").getUsers()).equals(Arrays.toString(new String[] {"josh, juan"})));
        //try entering the same user on a different board
        server.newBoard("board2");
        assertTrue(!server.checkUser("josh", "board2"));
        assertTrue(Arrays.toString(server.getBoard("board2").getUsers()).equals(Arrays.toString(new String[0])));
        server.close();
    }
}
