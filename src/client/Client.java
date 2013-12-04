package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.Socket;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import Command.Command;

public class Client {
    
    //the username the client will go by in this session
    //must be unique; no other clients can have this username
    private String username;
    //the same of the board currently being drawn upon
    private String currentBoardName;
    //the board the client has selected and is drawing on
    private CanvasGUI canvas;
    //the GUI for this client
    private JFrame frame;
    //the color the user is currently drawing in
    private Color currentColor = Color.BLACK;
    //the width of the brush the user is currently drawing with
    private float currentWidth = 10;
    //the socket with which the user connects to the client
    private Socket socket;
    //the list of boards the user has to choose from
    private String[] boards;
    
    public Client() {
        
        final JDialog dialog = new JDialog();
        dialog.setTitle("Welcome to Whiteboard");
        Container dialogContainer = new Container();
        GroupLayout layout = new GroupLayout(dialogContainer);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        dialogContainer.setLayout(layout);
        
        ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        
        SequentialGroup hUsername = layout.createSequentialGroup();
        final JTextField username = new JTextField(10);
        username.setName("username");
        JLabel usernameLabel = new JLabel("Username:");
        hUsername.addComponent(usernameLabel).addComponent(username);
        
        //TODO:get boards
        JList boardList = new JList<String>(new String[]{"Board 1", "Board 2", "Board 3"}); //data has type Object[]
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setLayoutOrientation(JList.VERTICAL);
        boardList.setVisibleRowCount(-1);
        JScrollPane boardListScroller = new JScrollPane(boardList);
        boardListScroller.setPreferredSize(new Dimension(100, 150));
        
        SequentialGroup hNewBoard = layout.createSequentialGroup();
        JLabel newBoardLabel = new JLabel("New Board:");
        JTextField newBoard = new JTextField(10);
        newBoard.setName("newBoard");
        JButton newBoardButton = new JButton("Add Board");
        hNewBoard.addComponent(newBoardLabel).addComponent(newBoard).addComponent(newBoardButton);
        
        JButton startButton = new JButton("Start");
        
        hGroup.addGroup(hUsername).addComponent(boardListScroller).addGroup(hNewBoard).addComponent(startButton);
        
        ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup vAll = layout.createSequentialGroup();
        
        ParallelGroup v1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v1.addComponent(usernameLabel).addComponent(username);
        
        ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
        v2.addComponent(newBoardLabel).addComponent(newBoard).addComponent(newBoardButton);
        
        vAll.addGroup(v1).addComponent(boardListScroller).addGroup(v2).addComponent(startButton);
        
        vGroup.addGroup(vAll);
        
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
        
        dialog.setContentPane(dialogContainer);
        dialog.pack();
        dialog.setVisible(true);
        
        startButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                if (checkForUniqueUser(username.getText())) {
                    dialog.setVisible(false);
                    setupCanvas();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Sorry, this username is already taken currently.", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            }
        });    
    }
    
    /**
     * Checks with the server to make sure the username hasn't already been taken
     * @param username: the user's choice of username
     * @return: true if the username is unique, false if it is not
     */
    public boolean checkForUniqueUser(String username) {
        return true;
    }
    
    public void setupCanvas() {
        updateBoards();
        frame = new JFrame("Freehand Canvas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        canvas = new CanvasGUI(800, 600, this);
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Switches the current board to the board with the given name
     * calls updateCanvasImage to set the image of the canvas to the one obtained by the server
     * Updates the current users of the canvas
     * @param newBoardName: the name of the new board
     */
    public void switchBoard(String newBoardName) {    
    }
    
    /**
     * Creates a new board on the server and names it with the given name
     * Sets current board name to the new name
     * Wipes the canvas clean
     * Sets users on the canvas to only this user
     * @param newBoardName: the name to name the new board with
     */
    public void newBoard(String newBoardName) {  
    }
    
    /**
     * Sets the canvas's bufferedImage to newImage
     * @param newImage: the BufferedImage to set the canvas's image to
     */
    public void updateCanvasImage(BufferedImage newImage) {
    }
    
    /**
     * Check that the boardName and currentBoardName are the same and then perform the command on the canvas
     * @param boardName: the board this command is for
     * @param command: the command to perform on the canvas
     */
    public void updateCanvasCommand(String boardName, Command command) {
    }
    
    /**
     * Gets the users for the current board from the server and sets them
     */
    public void updateUsers() {
        //TODO
        canvas.setUsers(new String[] {"Jessica", "Juan", "Josh"});
    }
    
    /**
     * Sets who the users are for the current canvas
     * @param users: the list of users to set for the canvas
     */
    public void setCanvasUsers(String[] users) {
    }
    
    /**
     * Gets all of the current boards from the server
     */
    public void updateBoards() {
        //TODO
        this.boards = new String[] {"Board 1", "Board 2", "Board 3"};
    }
    
    /**
     * Changes the color the user is using to paint on the canvas
     * @param color: the current color of choice to switch to
     */
    public void changeColor(Color newColor) {
    }
    
    /**
     * Changes the width of the stroke the user is painting with on the canvas
     * @param newWidth: the number representing the new widht of the stroke to use
     */
    public void changeWidth(float newWidth) {
    }
    
    /**
     * Gets the current color to use for drawing a line segment on the canvas
     * @return the currentColor being used to draw
     */
    public Color getCurrentColor() {
        return currentColor;
    }
    
    /**
     * Gets the current width to use for drawing a line segment on the canvas
     * @return the currentWidth being used to draw
     */
    public float getCurrentWidth() {
        return currentWidth;
    }
    
    /**
     * Sets the newWidth, probably based off of a slider movement on the canvas
     * @param newWidth: the new width of the stroke
     */
    public void setCurrentWidth(float newWidth) {
        currentWidth = newWidth;
    }
    
    /**
     * Sets the newColor, probably based off of a color picker selection on the canvas
     * @param newWidth: the new color of the stroke
     */
    public void setCurrentColor(Color newColor) {
        currentColor = newColor;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setCurrentBoardName(String currentBoardName) {
        this.currentBoardName = currentBoardName;
    }
    
    public void setBoards(String[] boards) {
        this.boards = boards;
    }
    
    public String[] getBoards() {
        return boards;
    }
    
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Client client = new Client();
            }
        });
    }

}
