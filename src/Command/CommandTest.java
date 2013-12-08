package Command;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.Test;

import client.Client;

public class CommandTest {
    
    /*
     * Testing strategy:
     * 
     * -Constructor
     * -toString
     * -checkBoardName
     * -compare a canvas with a command invoked on it and a canvas just drawn on
     */
    
    String noArguments = "draw board1 drawNothing";
    Command noArgumentsObject = new Command(noArguments);
    String lineSegment = "draw board2 drawLineSegment 50 50 60 60 0 10";
    Command lineSegmentObject = new Command(lineSegment);
    
    @Test
    public void testConstructor() {
        Command noArgumentsCorrect = new Command("drawNothing", new String[0], "board1");
        assertTrue(noArgumentsObject.equals(noArgumentsCorrect));
        Command lineSegmentCorrect = new Command("drawLineSegment", new String[]{"50", "50", "60", "60", "0", "10"}, "board2");
        assertTrue(lineSegmentObject.equals(lineSegmentCorrect));
    }
    
    @Test
    public void testToString() {
        assertTrue(noArgumentsObject.toString().equals(noArguments));
        assertTrue(lineSegmentObject.toString().equals(lineSegment));
    }
    
    @Test
    public void checkBoardNameTest() {
        assertTrue(!lineSegmentObject.checkBoardName("board1"));
        assertTrue(lineSegmentObject.checkBoardName("board2"));
    }
    
    @Test
    public void invokeCommandTest() {
        Client clientInvoked = new Client();
        clientInvoked.setupCanvas("username", "board2");
        lineSegmentObject.invokeCommand(clientInvoked.getCanvas());
        Client clientDrawn = new Client();
        clientDrawn.setupCanvas("username", "board2");
        clientDrawn.getCanvas().drawLineSegment(50, 50, 60, 60, 0, 10);
        BufferedImage imageInvoked = clientInvoked.getCanvas().getDrawingBuffer();
        BufferedImage imageDrawn = clientDrawn.getCanvas().getDrawingBuffer();
        boolean same = true;
        for (int x = 0; x < imageInvoked.getWidth(); x++) {
            for (int y = 0; y < imageInvoked.getHeight(); y++) {
                if (imageInvoked.getRGB(x, y) != imageDrawn.getRGB(x, y) ) same = false;
             }
        }
        assertTrue(same);
    }
    
}
