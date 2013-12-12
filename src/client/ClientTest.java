package client;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import server.Server;
import testResources.Helper;
public class ClientTest {

	/*
	 * @category no_didit
	 */
	
	@Test
	public void testBoardCreation() throws Exception {
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
		Thread.sleep(100);
		server.shutDown();
	}

}
 