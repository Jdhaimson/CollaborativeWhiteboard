package client;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import org.junit.Test;

import command.Command;
import server.Server;
import testResources.Helper;



/**
 * Test some user interface stuff.
 * @category no_didit
 */
public class ClientTest {
	/*
	 * Testing Strategy:
	 * - Test that all of the board methods work
	 * - Test basic interactions between boards and servers
	 */
	
	@Test
	public void testBoards() throws Exception {
		Server server = Helper.serverSetup(4444);
		
		Client client = new Client("localhost", 4444);
		assertTrue(client.newBoard("board1"));
		assertFalse(client.newBoard("board1"));
		
		String[] boards = client.getBoards();
		boolean contains = false;
		for(String board: boards) {
			if(board.equals("board1")) {
				contains = true;
			}
		}
		assertTrue(contains);
		
		client.kill();
		server.shutDown();
		Thread.sleep(100);
	}
	
	@Test
	public void testAllTheThings() throws Exception {
		Server server = Helper.serverSetup(4444);
		Client client1 = new Client("localhost", 4444);
		client1.startGUI();
		ClientGUI client1GUI = client1.getClientGUI();
		
		Client client2 = new Client("localhost", 4444);
		client2.startGUI();
		ClientGUI client2GUI = client2.getClientGUI();
		
		client1.newBoard("board1");
		client1.newBoard("board2");
		assertTrue(client1.createUser("josh", "board1"));
		assertFalse(client2.createUser("josh", "board2"));
		assertTrue(client2.createUser("juan", "board1"));
		
		client1GUI.setupCanvas();
		client2GUI.setupCanvas();
		
		String[] users = client1.getUsers();
		boolean containsJosh = false;
		boolean containsJuan = false;
		for (String user:users) {
			if (user.equals("josh")) containsJosh = true;
			else if (user.equals("juan")) containsJuan = true;
		}
		assertTrue(containsJosh && containsJuan);
		
		
		
		new Command("draw board1 drawLineSegment 50 50 60 60 0 10.0".split(" ")).invokeCommand(client1.getCanvas());
		client1.makeDrawRequest("drawLineSegment 50 50 60 60 0 10.0");
		
		BufferedImage imageDrawn = client1.getDrawingBuffer();
		BufferedImage imageInvoked = client2.getDrawingBuffer();
		
        boolean same = true;
        for (int x = 0; x < imageInvoked.getWidth(); x++) {
            for (int y = 0; y < imageInvoked.getHeight(); y++) {
                if (imageInvoked.getRGB(x, y) != imageDrawn.getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
		
		client1.switchBoard("board2");

		String[] board2users = client1.getUsers();
		String[] expected1 = {"josh"};
		assertArrayEquals(board2users, expected1);
		String[] board1users = client2.getUsers();
		String[] expected2 = {"juan"};
		assertArrayEquals(board1users, expected2);


		
		client1.kill();
		client2.kill();
		server.shutDown();
		Thread.sleep(100);
	}
	

}
 