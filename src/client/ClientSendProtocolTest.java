package client;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

public class ClientSendProtocolTest {
	/*
	 * @category no_didit
	 * Testing strategy:
	 * - Test that we can receive a message sent out over the the send protocol
	 *   without interruption
	 */

	@Test
	public void test() throws IOException {
		String expectedMessage = "Hello World!";
		
		// Connect to server and send message in separate thread
		new Thread(new Runnable() {
			public void run() {
				String sendMessage = "Hello World!";
				
				// wait for server thread to start accepting connections
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// connect to server send hellow world message
				try {
					Socket sendSocket = new Socket("localhost", 4444);
					PrintWriter out = new PrintWriter(sendSocket.getOutputStream(), true);
	
					Thread t = new Thread(new ClientSendProtocol(out, sendMessage));
					t.start();
					
					// Wait for thread to finish and close socket
					t.join();
					sendSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

		
			
		ServerSocket serverSocket = new ServerSocket(4444);
		Socket receiveSocket = serverSocket.accept();
		BufferedReader in = new BufferedReader(new InputStreamReader(receiveSocket.getInputStream()));
		String message = in.readLine();
		
		assertEquals(expectedMessage, message);
		
		serverSocket.close();
	}

}
