package client;

public class ClientGUITest {
    
    /*
     * @category no_didit
     * 
     * Testing strategy:
     * 
     *  Start dialog (window to choose username and initial board)
     *  -Pressing "Start" 
     *      -with a blank username triggers an error popup
     *      -without selecting a board (with a username) triggers an error popup
     *      -if your chosen username already exists triggers an error popup
     *      -with all fields correctly filled in opens the canvas
     *  -Pressing "Create Board" 
     *      -with a blank text field triggers an error popup
     *      -when the board name already is taken triggers an error popup
     *      -when the board name is unique appends the new board name to the list of available boards
     *  -Xing out of the dialog
     *      -kills the thread
     *  
     *  Canvas
     *  -Username and initial board chosen are displayed in the top right
     *  -Pressing "Boards" on the menu
     *      -shows the most updated list of boards, including ones created by other users
     *      -gives a "New Board" option
     *  -Pressing "New Board" under "Boards"
     *      -pulls up a dialog to create a new board
     *  -Pressing the new board dialog 
     *      -with a blank text field triggers an error popup
     *      -when the board name is unique creates the board, so clicking "Boards" will display the new board
     *  -Pressing a board under "Boards"
     *      -changes the current board name in the top right corner
     *      -redraws a white canvas then draws the board's image on the canvas
     *      -updates the list of users on the board when "Users" is pressed
     *  -Pressing "Users" on the menu
     *      -shows the most updated list of users
     *      -shows only the users on the current board
     *      -includes the user on the list
     *      -users are not selectable
     *  -Pressing "Mode" on the menu
     *      -displays two options: draw and erase, each with an icon next to them
     *      -clicking one of the options changes the icon on the "mode" menu button to the icon for the option
     *      -clicking erase makes the color automatically white, not affected by the current color
     *  -Pressing "Color" on the menu
     *      -opens a color picker
     *      -clicking a color adds it to the recent menu
     *      -clicking a color changes the border of the menu button
     *      -pressing anywhere makes the color picker go away
     *      -drawing color is color of border
     *  -Dragging the slider
     *      -changes the size of the brush stroke
     */
    
    
}
