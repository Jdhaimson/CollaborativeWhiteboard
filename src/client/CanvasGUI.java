package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import Command.Canvas;


/**
 * Canvas represents a drawing surface that allows the user to draw
 * on it freehand, with the mouse.
 */
public class CanvasGUI extends JPanel implements Canvas {
    // image where the user's drawing is stored
    private BufferedImage drawingBuffer;
    private EventListener currentListener;
    private String name;
    private String[] users;
    private Client client;
    
    /**
     * Make a canvas.
     * @param width width in pixels
     * @param height height in pixels
     */
    public CanvasGUI(int width, int height, Client client) {
        this.setPreferredSize(new Dimension(width, height));
        this.users = new String[0];
        this.client = client;
        addMenuBar();
        addDrawingController(new DrawingController());
        
        // note: we can't call makeDrawingBuffer here, because it only
        // works *after* this canvas has been added to a window.  Have to
        // wait until paintComponent() is first called.
    }
    
    public void setUsers(String[] users) {
        this.users = users;
    }
    
    private void addMenuBar() {
    	JMenuBar menuBar = new JMenuBar();
    	
    	//add First Menu = Mode
        JMenu mode = new JMenu("Mode");
        JMenuItem drawMenuItem = new JMenuItem("Draw");
        drawMenuItem.addActionListener(new  ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addDrawingController(new DrawingController());
            }});
        JMenuItem eraseMenuItem = new JMenuItem("Erase");
        eraseMenuItem.addActionListener(new  ActionListener() {
            public void actionPerformed(ActionEvent event) {
                addDrawingController(new EraserController());
            }});
        
        menuBar.add(mode);
        mode.add(drawMenuItem);
        mode.addSeparator();
        mode.add(eraseMenuItem);
        
        //add Second Menu = Users
        final JMenu usersMenu = new JMenu("Users");
        menuBar.add(usersMenu);
        //List of Users
        for (String user: users) {
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
                client.updateUsers();
                usersMenu.removeAll();
                for (String user: users) {
                    JLabel label = new JLabel(user);
                    label.setBorder(BorderFactory.createEmptyBorder(2, 5, 3, 5));
                    usersMenu.add(label);
                }
                repaint();
            }
        });
        
        
        //add List of Boards
        final JMenu boards = new JMenu("Board(s)");
        menuBar.add(boards);
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
                boards.removeAll();
                for (String board: client.getBoards()) {
                    boards.add(new JMenuItem(board));
                }
                repaint();
            }
        });
        
        class ColorActionListener implements ActionListener {
            Color newColor;
            public ColorActionListener(Color singleColor) {
                newColor = singleColor;
            }

            public void actionPerformed(ActionEvent e) {
                client.setCurrentColor(newColor);
            }
        }
        
        //add Colors
        JMenu colors = new JMenu("Paint Color");
        menuBar.add(colors);
        Object[][] listColors = {{"Black", Color.BLACK},
                                {"Blue", Color.BLUE},
                                {"Cyan", Color.CYAN},
                                {"Green", Color.GREEN},
                                {"Orange", Color.ORANGE},
                                {"Magenta", Color.MAGENTA},
                                {"Yellow", Color.YELLOW}};
        for (int i = 0; i<listColors.length; i++) {
            String name = (String)listColors[i][0];
            Color singleColor = (Color)listColors[i][1];
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(new ColorActionListener(singleColor));
            colors.add(item);
        }
        
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
        slider.setSize(200, 200);
        slider.setVisible(true);
        
        this.add("Menu", menuBar);
        this.add(slider);
        
    }
    
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
        drawSmile();
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
    
    /*
     * Draw a happy smile on the drawing buffer.
     */
    private void drawSmile() {
        final Graphics2D g = (Graphics2D) drawingBuffer.getGraphics();

        // all positions and sizes below are in pixels
        final Rectangle smileBox = new Rectangle(20, 20, 100, 100); // x, y, width, height
        final Point smileCenter = new Point(smileBox.x + smileBox.width/2, smileBox.y + smileBox.height/2);
        final int smileStrokeWidth = 3;
        final Dimension eyeSize = new Dimension(9, 9);
        final Dimension eyeOffset = new Dimension(smileBox.width/6, smileBox.height/6);
        
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(smileStrokeWidth));
        
        // draw the smile -- an arc inscribed in smileBox, starting at -30 degrees (southeast)
        // and covering 120 degrees
        g.drawArc(smileBox.x, smileBox.y, smileBox.width, smileBox.height, -30, -120);
        
        // draw some eyes to make it look like a smile rather than an arc
        for (int side: new int[] { -1, 1 }) {
            g.fillOval(smileCenter.x + side * eyeOffset.width - eyeSize.width/2,
                       smileCenter.y - eyeOffset.height - eyeSize.width/2,
                       eyeSize.width,
                       eyeSize.height);
        }
        
        // IMPORTANT!  every time we draw on the internal drawing buffer, we
        // have to notify Swing to repaint this component on the screen.
        this.repaint();
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
            drawLineSegment(lastX, lastY, x, y, client.getCurrentColor(), client.getCurrentWidth());
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
    
    /*
     * EraserController handles the user's freehand drawing.
     */
    private class EraserController implements MouseListener, MouseMotionListener {
        // store the coordinates of the last mouse event, so we can
        // draw a line segment from that last point to the point of the next mouse event.
        private int lastX, lastY; 

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
            drawLineSegment(lastX, lastY, x, y, Color.WHITE, client.getCurrentWidth());
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
