package testResources;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import server.Server;

public class Helper {
	
	/**
	 * Sets up a server to test on
	 * @param port
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static Server serverSetup(int port) {
		class RunnableServer implements Runnable{
			private Server server;
			public RunnableServer(Server server) {
				this.server = server;
			}
			
			public void run() {
				server.serve();
			}
		}
		
		try {
			Server server = new Server(port);
			new Thread(new RunnableServer(server)).start();
			return server;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	} 
}
