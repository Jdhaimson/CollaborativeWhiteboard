package server;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import client.Client;

public class ServerProtocolTest {
    /**************** New Board Protocol *******************/
    @Test
    public void makeNewBoardBasicTest() throws IllegalArgumentException, IOException{
        ServerProtocol protocol = new ServerProtocol(null, new Server(4444));
        String input = "new board";
        String output = protocol.testHandleRequest(input);
        
        assertEquals("new board true",output);
        System.exit(0);
        
    }
    

}
