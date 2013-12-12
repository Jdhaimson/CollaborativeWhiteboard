package client;

import static org.junit.Assert.*;

import org.junit.Test;

public class TrackerTest {

	/*
	 * Testing Strategy:
	 * 
	 * - Test constructor
	 * - Test setter
	 * - Test thread safety
	 */
	
	
	@Test
	public void testBasic() {
		Tracker t = new Tracker(false);
		assertTrue(!t.getValue());
	}
	
	@Test
	public void testSetter() {
		Tracker t = new Tracker(false);
		t.setValue(true);
		assertTrue(t.getValue());
	}
	
	/**
	 * Test that we can modify a Tracker in a separate thread and see the changes
	 * in the main thread without getting any concurrency bugs
	 */
	@Test
	public void testMultiThreading() {
		Tracker t = new Tracker(false);
		
		/**
		 * Runnable class to test thread safety of Tracker object
		 */
		class TrackerMod implements Runnable {
			private Tracker tr;
			public TrackerMod(Tracker tr) {
				this.tr = tr;
			}
			
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tr.setValue(true);
			}
		}
		
		new Thread(new TrackerMod(t)).start();
		long startTime = System.currentTimeMillis();
		boolean timeout = false;
		long timeoutLength = 2000;

		while(!timeout && !t.getValue()) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - startTime >= timeoutLength) {
				timeout = true;
			}
		}
		
		// Check that we saw the changes and didn't time out
		assertTrue(!timeout);
	}

}
