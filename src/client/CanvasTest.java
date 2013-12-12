package client;

import static org.junit.Assert.assertTrue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.JLabel;

import org.junit.Test;

public class CanvasTest {
    
    /*
     * @category no_didit
     * Testing Strategy:
     * 
     * -paintComponent (called on repaint)
     *      -if drawing buffer is null, create an all-white drawing buffer of size 800x600
     *      -new image is painted
     * -makeDrawingBuffer
     *      -sets the client's drawing buffer to a new all-white rectangle
     * -fillWithWhite
     *      -draws an all-white rectangle over the drawing buffer
     * -drawLineSegment
     *      -draws the correct line segment
     * -updateCurrentUserboard
     *      -sets the JLabel in the upper-right corner to the correct values
     * -addDrawingController
     *      -sets the current listener to the new listener
     * -switchBoard
     *      -draws an all-white rectangle over the drawing buffer
     *      -sets the client's current board name to the correct value     
     */
    
    @Test
    public void paintComponentTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.getCanvas().repaint();
        
        BufferedImage madeImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = (Graphics2D) madeImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  800, 600);
        
        boolean same = true;
        for (int x = 0; x < madeImage.getWidth(); x++) {
            for (int y = 0; y < madeImage.getHeight(); y++) {
                if (madeImage.getRGB(x, y) != client.getDrawingBuffer().getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
    @Test
    public void makeDrawingBufferTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.getCanvas().makeDrawingBuffer();
        
        BufferedImage madeImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = (Graphics2D) madeImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  800, 600);
        
        boolean same = true;
        for (int x = 0; x < madeImage.getWidth(); x++) {
            for (int y = 0; y < madeImage.getHeight(); y++) {
                if (madeImage.getRGB(x, y) != client.getDrawingBuffer().getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
    @Test
    public void fillWithWhiteTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.getCanvas().fillWithWhite();
        
        BufferedImage madeImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = (Graphics2D) madeImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  800, 600);
        
        boolean same = true;
        for (int x = 0; x < madeImage.getWidth(); x++) {
            for (int y = 0; y < madeImage.getHeight(); y++) {
                if (madeImage.getRGB(x, y) != client.getDrawingBuffer().getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
    @Test
    public void drawLineSegmentTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.getCanvas().drawLineSegment(50, 50, 60, 60, 0, 10);
        
        Client clientTest = new Client("localhost", 4444);
        clientTest.getClientGUI().setupCanvas();
        Graphics2D g = (Graphics2D) clientTest.getDrawingBuffer().getGraphics();
        Color colorObject = new Color(0);
        g.setColor(colorObject);
        g.setStroke(new BasicStroke(10));
        g.drawLine(50, 50, 60, 60);
        
        boolean same = true;
        for (int x = 0; x < clientTest.getDrawingBuffer().getWidth(); x++) {
            for (int y = 0; y < clientTest.getDrawingBuffer().getHeight(); y++) {
                if (clientTest.getDrawingBuffer().getRGB(x, y) != client.getDrawingBuffer().getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
    @Test
    public void updateCurrentUserBoardTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.setUsername("Jessica");
        client.setCurrentBoardName("board1");
        client.getCanvas().updateCurrentUserBoard();
        JLabel userBoardTest = new JLabel("Hi, Jessica. This board is: board1");
        assertTrue(userBoardTest.equals(client.getClientGUI().getCurrentUserBoard()));
    }
    
    @Test
    public void addDrawingControllerTest() throws UnknownHostException, IOException {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        DrawingController controller = new DrawingController(client);
        client.getCanvas().addDrawingController(controller);
        assertTrue(client.getCanvas().getCurrentListener().equals(controller));
    }
    
    @Test
    public void switchBoardTest() throws Exception {
        Client client = new Client("localhost", 4444);
        client.getClientGUI().setupCanvas();
        client.newBoard("board1");
        client.getCanvas().switchBoard("board1");
        assertTrue(client.getCurrentBoardName().equals("board1"));
        BufferedImage madeImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = (Graphics2D) madeImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,  0,  800, 600);
        
        boolean same = true;
        for (int x = 0; x < madeImage.getWidth(); x++) {
            for (int y = 0; y < madeImage.getHeight(); y++) {
                if (madeImage.getRGB(x, y) != client.getDrawingBuffer().getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
}
