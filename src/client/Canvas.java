package client;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;


/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse.
 */
public class Canvas extends JFrame {

    // image where the user's drawing is stored
    private BufferedImage drawingBuffer;
    private EventListener currentListener;
    private Client client;

    //TODO:need current board name in menu bar

    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public Canvas(int width, int height, Client client) {
        this.setPreferredSize(new Dimension(width, height));
        this.client = client;
        addDrawingController(new DrawingController(false));
        
        setLayout();
        addMenuBar();
        
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
    }
    

    private void setLayout() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        
        MyCanvas myCanvas = new MyCanvas();
        this.add(myCanvas, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        
        myCanvas.add(new JLabel("Hello"));
    }
    
    public void setUsers(String[] users) {
        this.users = users;
    }
    
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getUsersMenu());
        menuBar.add(getBoardsMenu());
        menuBar.add(getModeMenu());
        menuBar.add(getColorsMenu());
        menuBar.add(getSlider());
        menuBar.add(Box.createHorizontalGlue());
        this.setJMenuBar(menuBar);
    }
    
    /**
     * Add the mode menu to the menu mar
     * @return JMenu representing the mode menu
     */
    private JMenu getModeMenu() {
        // Icon next to Mode
        final ImageIcon eraserIcon = new ImageIcon("../whiteboard/docs/icons/eraser.png");
        final ImageIcon pencilIcon = new ImageIcon("../whiteboard/docs/icons/pencil.png");
        
        final JMenu mode = new JMenu("Mode");
        mode.setIcon(pencilIcon);
        
        JMenuItem drawMenuItem = new JMenuItem("Draw", pencilIcon);
        drawMenuItem.addActionListener(new  ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addDrawingController(new DrawingController(false));
                mode.setIcon(pencilIcon);
            }});
        JMenuItem eraseMenuItem = new JMenuItem("Erase", eraserIcon);
        eraseMenuItem.addActionListener(new  ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addDrawingController(new DrawingController(true));
                mode.setIcon(eraserIcon);
            }});
        mode.add(drawMenuItem);
        mode.addSeparator();
        mode.add(eraseMenuItem);
        
        
        
        return mode;
    }
    
    
    /**
     * Add the users menu to the menu mar
     * @return JMenu representing the users menu
     */
    private JMenu getUsersMenu() {
        final JMenu usersMenu = new JMenu("Users");
        //List of Users
        for (String user: client.getUsers()) {
            JLabel label = new JLabel(user);
            label.setBorder(BorderFactory.createEmptyBorder(2, 5, 3, 5));
            usersMenu.add(label);
        }
        
        usersMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(MenuEvent arg0) {
            }

            @Override
            public void menuDeselected(MenuEvent arg0) {
            }

            @Override
            public void menuSelected(MenuEvent arg0) {
                usersMenu.removeAll();
                for (String user: client.getUsers()) {
                    JLabel label = new JLabel(user);
                    label.setBorder(BorderFactory.createEmptyBorder(2, 5, 3, 5));
                    usersMenu.add(label);
                }
            }
        });
        return usersMenu;
    }
    
    /**
     * Add the boards menu to the menu mar
     * @return JMenu representing the boards menu
     */
    private JMenu getBoardsMenu() {
      //add List of Boards
        final JMenu boards = new JMenu("Board(s)");

        //new board option
        boards.add(new JMenuItem("New Board"));
        boards.addSeparator();
        
        //List of Boards
        String[] listBoards = client.getBoards();
        for (String board: listBoards) {
            boards.add(new JMenuItem(board));
        }

        
        boards.addMenuListener(new MenuListener() {
            @Override
            public void menuCanceled(MenuEvent arg0) {
            }

            @Override
            public void menuDeselected(MenuEvent arg0) {
            }

            @Override
            public void menuSelected(MenuEvent arg0) {
                client.updateBoards();
                for (int i=boards.getItemCount()-1; i>1; i--)
                {
                    boards.remove(i);
                }
                for (String board: client.getBoards()) {
                    boards.add(new JMenuItem(board));
                }
            }
        });
        
        return boards;
    }
    
    /**
     * Add the colors menu to the menu bar
     * @return JMenu representing the colors menu
     */
    private JMenu getColorsMenu() {
        class ColorChangeListener implements ChangeListener {
            JMenu colors;
            public ColorChangeListener(JMenu colors) {
                this.colors = colors;
            }
            
            @Override
            public void stateChanged(ChangeEvent e) {
                ColorSelectionModel model = (ColorSelectionModel) e.getSource();
                Color currentColor = model.getSelectedColor();
                client.setCurrentColor(currentColor);
                colors.setBorder(BorderFactory.createLineBorder(currentColor,2));
            }
        }
        //add Colors
        
        JMenu colors = new JMenu("Paint Color");
        
        JColorChooser chooser = new JColorChooser(Color.BLACK);
        colors.add(chooser);
        chooser.getSelectionModel().addChangeListener(new ColorChangeListener(colors));
        chooser.setPreviewPanel(new JPanel());
        colors.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        
        //remove panels
        AbstractColorChooserPanel[] panels = chooser.getChooserPanels();
        for (AbstractColorChooserPanel accp : panels) {
            if (!accp.getDisplayName().equals("Swatches")) {
                chooser.removeChooserPanel(accp);
            }
        }
        return colors;
    }
    /**
     * add slider to the menu bar
     * @return JSlider representing the slider
     */
    private JSlider getSlider() {
        class SliderChangeListener implements ChangeListener {

            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                if (!source.getValueIsAdjusting()) {
                    float weight = (float)source.getValue();
                    client.setCurrentWidth(weight);
                }
                
            }
        }
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 50, (int)Math.round(client.getCurrentWidth()));

        slider.addChangeListener(new SliderChangeListener());
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(2);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        //slider.setSize(50, 1000);
        slider.setVisible(true);
        
        return slider;
    }
    class MyCanvas extends JPanel {

        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        public void paintComponent(Graphics g) {
            // If this is the first time paintComponent() is being called,
            // make our drawing buffer.
            if (drawingBuffer == null) {
                makeDrawingBuffer();
            }
            
            // Copy the drawing buffer to the screen.
            g.drawImage(drawingBuffer, 0, 0, null);
        }
    
    
        /*
         * Make the drawing buffer and draw some starting content for it.
         */
        private void makeDrawingBuffer() {
            drawingBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            fillWithWhite();
        }

        public void setDrawingBuffer(BufferedImage newImage) {
            drawingBuffer = newImage;
            repaint();
        }
    
        /*
         * Make the drawing buffer entirely white.
         */
        private void fillWithWhite() {
            final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
    
            g.setColor(Color.WHITE);
            g.fillRect(0,  0,  getWidth(), getHeight());
            
            // IMPORTANT!  every time we draw on the internal drawing buffer, we
            // have to notify Swing to repaint this component on the screen.
            this.repaint();
        }
    }
    
    /*
     * Draw a line between two points (x1, y1) and (x2, y2), specified in
     * pixels relative to the upper-left corner of the drawing buffer.
     */
    public void drawLineSegment(int x1, int y1, int x2, int y2, Color color, float width) {
        Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();
        
        g.setColor(color);
        g.setStroke(new BasicStroke(width));
        g.drawLine(x1, y1, x2, y2);
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
    }
    
    /*
     * Add the mouse listener that supports the user's freehand drawing.
     */
    private void addDrawingController(EventListener listener) {
        if (currentListener != null) {
            removeMouseListener((MouseListener) currentListener);
            removeMouseMotionListener((MouseMotionListener) currentListener);
        }
        currentListener = listener;
        addMouseListener((MouseListener) currentListener);
        addMouseMotionListener((MouseMotionListener) currentListener);
    }
    
    /*
     * DrawingController handles the user's freehand drawing.
     */
    private class DrawingController implements MouseListener, MouseMotionListener {
        // store the coordinates of the last mouse event, so we can
        // draw a line segment from that last point to the point of the next mouse event.
        private int lastX, lastY;
        private final boolean isErasing;

        public DrawingController(boolean erasing) {
            this.isErasing = erasing;
        }
        /*
         * When mouse button is pressed down, start drawing.
         */
        public void mousePressed(MouseEvent e) {
            lastX = e.getX();
            lastY = e.getY();
        }

        /*
         * When mouse moves while a button is pressed down,
         * draw a line segment.
         */
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            
            Color color = client.getCurrentColor();
            if (isErasing) {    color = Color.white; }
            
            // to make up for the height of the menu
            int menuHeight = 73;
            drawLineSegment(lastX, lastY - menuHeight, x, y - menuHeight, color, client.getCurrentWidth());
            lastX = x;
            lastY = y;
        }

        // Ignore all these other mouse events.
        public void mouseMoved(MouseEvent e) { }
        public void mouseClicked(MouseEvent e) { }
        public void mouseReleased(MouseEvent e) { }
        public void mouseEntered(MouseEvent e) { }
        public void mouseExited(MouseEvent e) { }
    }    
}
