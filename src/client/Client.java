package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.DefaultListModel;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import Command.Command;

public class Client {
    
    //the username the client will go by in this session
    //must be unique; no other clients can have this user name
    private String username;
    //the name of the board currently being drawn upon
    private String currentBoardName;
    //the board the client has selected and is drawing on
    private Canvas canvas;
    //the GUI for this client
    private JFrame frame;
    //the color the user is currently drawing in
    private Color currentColor = Color.BLACK;
    //the width of the brush the user is currently drawing with
    private float currentWidth = 10;
    //the socket with which the user connects to the client
    private Socket socket;
    // Thread pool
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private JDialog dialog;
    private DefaultListModel<String> boardListModel;
    private JTextField newBoard;
    
    public Client() throws UnknownHostException, IOException {
        socket = new Socket("localhost", 4444);
        
        dialog = new JDialog();
        dialog.setTitle("Welcome to Whiteboard");
        final Container dialogContainer = new Container();
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
        
        boardListModel = new DefaultListModel<String>();

        // Get boards from server and add to data model
        // TODO: Handle case where there are no boards on server
        String[] tempBoards;
		try {
			tempBoards = this.getBoards();
			
			for (int i=0; i<tempBoards.length;i++) {
	            boardListModel.addElement(tempBoards[i]);
	        }
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
        final JList<String> boardList = new JList<String>(boardListModel); //data has type Object[]
        boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boardList.setLayoutOrientation(JList.VERTICAL);
        boardList.setVisibleRowCount(-1);
        JScrollPane boardListScroller = new JScrollPane(boardList);
        boardListScroller.setPreferredSize(new Dimension(100, 150));
        
        SequentialGroup hNewBoard = layout.createSequentialGroup();
        JLabel newBoardLabel = new JLabel("New Board:");
        newBoard = new JTextField(10);
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
                if (username.getText().equals("")) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a username.", "Try again", JOptionPane.ERROR_MESSAGE);
                } else if (boardList.isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please select a board.", "Try again", JOptionPane.ERROR_MESSAGE);
                } else if (createUser(username.getText(), boardList.getSelectedValue())) {
                    dialog.setVisible(false);
                    setupCanvas(username.getText(), boardList.getSelectedValue());
                } else {
                    JOptionPane.showMessageDialog(dialog, "Sorry, this username is already taken currently.", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        newBoardButton.addActionListener(new ActionListener() {
            public synchronized void actionPerformed(ActionEvent e) {
                NewBoardWorker worker = new NewBoardWorker(newBoard.getText());
                worker.execute();
                
            }
        });
        
        // close socket on exit
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        });
    }
    
    class NewBoardWorker extends SwingWorker<Boolean, Object> {
        
        private String newBoardName;
        
        public NewBoardWorker(String newBoardName) {
            this.newBoardName = newBoardName;
        }
        
        /**
         * Called when execute is called on the worker
         */
        @Override
        protected Boolean doInBackground() throws Exception {
            return newBoard(newBoardName);
        }   
        
        /**
         * After doInBackground has gotten its result, display the result in the list (or not)
         */
        @Override
        protected void done() {
            try {
                if (get()) {
                    boardListModel.addElement(newBoardName);
                    newBoard.setText("");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Sorry, this board name is already taken.", "Try again", JOptionPane.ERROR_MESSAGE);
                }
            } catch (HeadlessException | InterruptedException
                    | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks with the server to make sure the username hasn't already been taken and if it hasn't, create the user
     * @param username: the user's choice of username
     * @return: true if username creation is successful, false if not
     */
    public boolean createUser(String username, String boardName) {
        //TODO
        return true;
    }
    
    public void setupCanvas(String username, String boardName) {
        this.username = username;
        this.currentBoardName = boardName;
        makeNewCanvas();
    }
    
    /**
     * Gives this client a blank, new Canvas
     */
    public void makeNewCanvas() {
        canvas = new Canvas(800, 600, this);
    }
    
    /**
     * Switches the current board to the board with the given name
     * server switch command
     * Updates the current users of the canvas
     * @param newBoardName: the name of the new board
     */
    public void switchBoard(String newBoardName) {
        //TODO
    }

    /**
     * Checks that the board name hasn't already been taken and if it hasn't,
     * creates a new board on the server and names it with the given name
     * 
     * @param newBoardName
     *            the name to name the new board with
     * @return true if the board creation is successful, false if not
     */
    public boolean newBoard(String newBoardName) { 
        //TODO
        return true;
    }
    
    /**
     * Check that the boardName and currentBoardName are the same and then perform the command on the canvas
     * @param boardName: the board this command is for
     * @param command: the command to perform on the canvas
     */
    public void commandCanvas(String boardName, Command command) {
        if (command.checkBoardName(boardName)) {
            command.invokeCommand(canvas);
        }
    }
    
    /**
     * Gets the users for the current board from the server and sets them
     */
    public String[] getUsers() {
        //TODO
        return new String[] {"Jessica", "Juan", "Josh"};
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
    

    public String[] getBoards() throws Exception {
        String boards = makeRequest("boards");
        if(!boards.contains("boards")) {
        	throw new Exception("Server returned unexpected result: " + boards);
        }
        
        String[] boardsListStrings = boards.split(" ");
        return Arrays.copyOfRange(boardsListStrings, 1, boardsListStrings.length);
    }
    
    public String getCurrentBoardName() {
        return currentBoardName;
    }
    

    public String makeRequest(String request) throws IOException {
    	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println(request);        
        // All responses are only one line
        String response = in.readLine();
        
        return response;
    }
    
    public String getUsername() {
        return username;
    }
    
    /*
     * Main program. Make a window containing a Canvas.
     */
    public static void main(String[] args) {
        // set up the UI (on the event-handling thread)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
					Client client = new Client();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });
    }

}
