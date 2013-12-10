package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.SwingWorker;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

public class ClientGUI {

	private final Client client;
	
    //the board the client has selected and is drawing on
    private Canvas canvas;
    //the GUI for this client
    private JFrame frame;

	// Start Dialog GUI objects
	private JDialog dialog;
	private DefaultListModel<String> boardListModel;
	private JLabel newBoardLabel;
	private JTextField newBoard;
	private JList<String> boardList;
	private Container dialogContainer;
	private GroupLayout layout;
	private JTextField usernameTextField;
	private JLabel usernameLabel;
	private JScrollPane boardListScroller;
	private JButton newBoardButton;
	private JButton startButton;

	public ClientGUI(Client client) {
		this.client = client;
		startDialog();
	}


	/**
	 * Creates start dialog which handles username and initial board
	 */
	private void startDialog() {
		dialog = new JDialog();
		dialog.setTitle("Welcome to Whiteboard");
		dialog.setResizable(false);

		setDialogLayout();
		setDialogActionListeners(); 
	}

	/**
	 * Sets layout for start dialog
	 */
	public void setDialogLayout() {
		dialogContainer = new Container();
		layout = new GroupLayout(dialogContainer);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		dialogContainer.setLayout(layout);

		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);

		SequentialGroup hUsername = layout.createSequentialGroup();
		usernameTextField = new JTextField(10);
		usernameTextField.setName("username");
		usernameLabel = new JLabel("Username:");
		hUsername.addComponent(usernameLabel).addComponent(usernameTextField);

		boardListModel = new DefaultListModel<String>();

		// Get boards from server and add to data model
		try {
			String[] boards = client.getBoards();
			for (int i=0; i<boards.length;i++) {
				boardListModel.addElement(boards[i]);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		boardList = new JList<String>(boardListModel); //data has type Object[]
		boardList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		boardList.setLayoutOrientation(JList.VERTICAL);
		boardList.setVisibleRowCount(-1);
		boardListScroller = new JScrollPane(boardList);
		boardListScroller.setPreferredSize(new Dimension(100, 150));

		SequentialGroup hNewBoard = layout.createSequentialGroup();
		newBoardLabel = new JLabel("New Board:");
		newBoard = new JTextField(10);
		newBoard.setName("newBoard");
		newBoardButton = new JButton("Add Board");
		hNewBoard.addComponent(newBoardLabel).addComponent(newBoard).addComponent(newBoardButton);

		startButton = new JButton("Start");

		hGroup.addGroup(hUsername).addComponent(boardListScroller).addGroup(hNewBoard).addComponent(startButton);

		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vAll = layout.createSequentialGroup();

		ParallelGroup v1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v1.addComponent(usernameLabel).addComponent(usernameTextField);

		ParallelGroup v2 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v2.addComponent(newBoardLabel).addComponent(newBoard).addComponent(newBoardButton);

		vAll.addGroup(v1).addComponent(boardListScroller).addGroup(v2).addComponent(startButton);

		vGroup.addGroup(vAll);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		dialog.setContentPane(dialogContainer);
		dialog.pack();
		dialog.setVisible(true);
	}

	/**
	 * Adds action listeners to start dialog
	 */
	private void setDialogActionListeners() {

		startButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				if (usernameTextField.getText().equals("")) {
					JOptionPane.showMessageDialog(dialog, "Please enter a username.", "Try again", JOptionPane.ERROR_MESSAGE);
				} else if (boardList.isSelectionEmpty()) {
					JOptionPane.showMessageDialog(dialog, "Please select a board.", "Try again", JOptionPane.ERROR_MESSAGE);
				} else
					try {
						if (client.createUser(usernameTextField.getText(), boardList.getSelectedValue())) {
							dialog.dispose();
							setupCanvas();
							client.makeRequest("switch "+client.getUsername()+" "+client.getCurrentBoardName()+" "+client.getCurrentBoardName());
						} else {
							JOptionPane.showMessageDialog(dialog, "Sorry, this username is already taken currently.", "Try again", JOptionPane.ERROR_MESSAGE);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
			}
		});

		newBoardButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				NewBoardWorker worker = new NewBoardWorker(newBoard.getText());
				worker.execute();

			}
		});

	}	
	
	public Canvas getCanvas() {
		return canvas;
	}

    public void setupCanvas() {
        frame = new JFrame("Freehand Canvas");
        frame.setTitle("Whiteboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);
        makeNewCanvas();
    }
    
    /**
     * Gives this client a blank, new Canvas
     */
    public void makeNewCanvas() {
        canvas = new Canvas(800, 600, client);
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
			return client.newBoard(newBoardName);
		}   

		/**
		 * After doInBackground has gotten its result, display the result in the list (or not)
		 */
		@Override
		protected void done() {
			try {
				if (get()) {
					String[] boards = client.getBoards();
					boardListModel.removeAllElements();
					for (int i=0; i<boards.length;i++) {
						boardListModel.addElement(boards[i]);
					}
					newBoard.setText("");
				} else {
					JOptionPane.showMessageDialog(dialog, "Sorry, this board name is already taken.", "Try again", JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void newBoardDialog() {
		final JDialog newBoardDialog = new JDialog();
		newBoardDialog.setTitle("Create New Board");
		newBoardDialog.setResizable(false);
		final Container newBoardDialogContainer = new Container();
		GroupLayout layout = new GroupLayout(newBoardDialogContainer);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		newBoardDialogContainer.setLayout(layout);

		JLabel newBoardNameLabel = new JLabel("New Board Name:");
		final JTextField newBoardName = new JTextField(10);
		JButton newBoardButton = new JButton("Create");

		ParallelGroup hGroup = layout.createParallelGroup(GroupLayout.Alignment.CENTER);

		SequentialGroup hEnter = layout.createSequentialGroup();
		hEnter.addComponent(newBoardNameLabel).addComponent(newBoardName);

		hGroup.addGroup(hEnter).addComponent(newBoardButton);

		ParallelGroup vGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		SequentialGroup vAll = layout.createSequentialGroup();

		ParallelGroup v1 = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
		v1.addComponent(newBoardNameLabel).addComponent(newBoardName);

		vAll.addGroup(v1).addComponent(newBoardButton);

		vGroup.addGroup(vAll);

		layout.setHorizontalGroup(hGroup);
		layout.setVerticalGroup(vGroup);

		newBoardDialog.setContentPane(newBoardDialogContainer);
		newBoardDialog.pack();
		newBoardDialog.setVisible(true);

		newBoardButton.addActionListener(new ActionListener() {
			public synchronized void actionPerformed(ActionEvent e) {
				String newBoardNameString = newBoardName.getText();
				if (newBoardNameString.equals("")) {
					JOptionPane.showMessageDialog(newBoardDialog, "Please enter a board name.", "Try again", JOptionPane.ERROR_MESSAGE);
				} else {
					try {
						boolean successful = client.newBoard(newBoardNameString);
						if (!successful) {
							JOptionPane.showMessageDialog(newBoardDialog, "Sorry, this board name is already taken.", "Try again", JOptionPane.ERROR_MESSAGE);
						} else {
							client.getBoards();
							newBoardDialog.dispose();
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
}
