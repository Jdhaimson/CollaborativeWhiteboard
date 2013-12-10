package server;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class BoardTest {
    
    /*
     * Testing strategy:
     * 
     * -Add users to a board
     * -Delete users from a board
     * -Try to delete a user from a board that user is not on
     * -Check to see if a user exists on a board when it does
     * -Check to see if a user exists on a board when it doesn't
     */
    
    @Test
    public void addUserTest() {
        Board board = new Board();
        board.addUser("jessica");
        assertTrue(Arrays.equals(board.getUsers(),new String[] {"jessica"}));
        board.addUser("josh");
        assertTrue(Arrays.equals(board.getUsers(),new String[] {"jessica", "josh"}));
    }
    
    @Test
    public void deleteUserTest() {
        Board board = new Board();
        board.setUsers(new String[] {"jessica", "josh"});
        board.deleteUser("jessica");
        assertTrue(Arrays.equals(board.getUsers(),new String[] {"josh"}));
        board.deleteUser("juan");
        assertTrue(Arrays.equals(board.getUsers(),new String[] {"josh"}));
    }
    
    @Test
    public void checkUsernameTest() {
        Board board = new Board();
        board.setUsers(new String[] {"josh"});
        assertTrue(!board.checkUsernameAvailable("josh"));
        assertTrue(board.checkUsernameAvailable("juan"));
    }

}
