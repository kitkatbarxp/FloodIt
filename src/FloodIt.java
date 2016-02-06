// CS 201 - Data Structures
// Final Project: Flood It
// Names: Kit Tse, Bekah Moon
// Comment: The dialog boxes have been giving us trouble on certain browsers/
//		operating systems. But ideally they should work!

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;

public class FloodIt extends JFrame implements ActionListener, ItemListener {
	

	private static final long serialVersionUID = 1L;

	// instance variables
	BoardCanvas b; 			   // canvas showing the actual board
	Button howToPlay, newGame; // buttons for instructions and new game
	Button red, yellow, green, // buttons for choosing the color
		  blue, pink, magenta;
	Label step; 			   // label to show the current number of steps
	Choice board;			   // choice panel for board size
	int stepCounter = 0;		   // keeps track of the number of steps taken
	int boardSize = 0;		   // board size set at small 
	
	
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(new Runnable(){
	        @Override
	        public void run() {
	            new FloodIt();
	        }
	    });
	}
	
	// constructor
	public FloodIt() {
		init();
	}

	// initializes the board
	public void init () {
		this.setSize(685,550);
		setLayout(new BorderLayout());
		add("East", sideBar());
		b = new BoardCanvas(this);
		add("Center", b);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	}

	// creates empty labels for layout design
	protected Label emptyLabel() {
		Label emptyLabel = new Label();
		return emptyLabel;
	}

	// creates the side bar panel
	protected Panel sideBar() {
		Panel sideBar = new Panel();
		sideBar.setLayout(new GridLayout(2,1));	
		sideBar.setBackground(Color.LIGHT_GRAY);
		sideBar.add(top());
		sideBar.add(bottom());

		return sideBar;
	}

	// creates the top part of the side bar
	protected Panel top() {
		Panel top = new Panel();
		top.setLayout(new BorderLayout(20,0));
		step = new Label("Steps: "+ stepCounter + "/22");
		step.setFont(new Font("Arial", Font.PLAIN, 30));
		step.setAlignment(Label.CENTER);
		top.add("North",step);
		top.add("East", emptyLabel());
		top.add("South", emptyLabel());
		top.add("West", emptyLabel());
		top.add("Center", colorButtons());
		return top;
	}

	// creates the color button panel for users to press
	protected Panel colorButtons() {
		Panel colorButtons = new Panel();
		colorButtons.setLayout(new GridLayout(3,3,15,10));
		red = CButton(Color.red, "u");
		yellow = CButton(Color.yellow, "i");
		green = CButton(Color.green, "o");
		blue = CButton(Color.blue, "j");
		pink = CButton(Color.pink, "k");
		magenta = CButton(Color.magenta, "l");
		colorButtons.add(emptyLabel());
		colorButtons.add(emptyLabel());
		colorButtons.add(emptyLabel());
		colorButtons.add(red);
		colorButtons.add(yellow);
		colorButtons.add(green);
		colorButtons.add(blue);
		colorButtons.add(pink);
		colorButtons.add(magenta);
		return colorButtons;
	}

	// helper method for creating a color button
	protected Button CButton(Color bg, String text) {
		Button b = new Button(text);
		b.setFont(new Font("Arial", Font.BOLD, 20));
		b.setBackground(bg);
		b.setPreferredSize(new Dimension(25,25));
		b.addActionListener(this);
		return b;
	}

	// creates the bottom panel
	protected Panel bottom() {
		Panel bottom = new Panel();
		bottom.setLayout(new BorderLayout());
		bottom.add("North",emptyLabel());
		bottom.add("East",emptyLabel());
		bottom.add("South",emptyLabel());
		bottom.add("West",emptyLabel());
		bottom.add("Center",bottomButtons());
		return bottom;
	}

	// creates the buttons in the bottom panel
	protected Panel bottomButtons() {
		Panel bottomButtons = new Panel();
		bottomButtons.setLayout(new GridLayout(7,1));
		howToPlay = new Button("How to play");
		howToPlay.addActionListener(this);
		newGame = new Button("New game");
		newGame.addActionListener(this);
		bottomButtons.add(emptyLabel());
		bottomButtons.add(board());
		bottomButtons.add(emptyLabel());
		bottomButtons.add(howToPlay);
		bottomButtons.add(emptyLabel());
		bottomButtons.add(newGame);
		bottomButtons.add(emptyLabel());
		return bottomButtons;
	}

	// creates a panel for board size options
	protected Panel board() {
		Panel menu = new Panel();
		menu.setLayout(new GridLayout(2,1));
		board = new Choice();
		board.addItem("Small");
		board.addItem("Medium");
		board.addItem("Large");
		menu.add(new Label("Board size:"));
		menu.add(board);
		board.addItemListener(this);
		return menu;
	}

	// handles events when buttons are pressed
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() instanceof Button) {
			String label = ((Button)event.getSource()).getLabel();
			if (label.equals("How to play"))
				b.howToPlay();
			else if (label.equals("New game")) {
				b.newGame();
				b.refreshStepCounter();
			} else { // if color buttons are pressed, match adjacent squares
					 // to current group and updates the step counter
				int pressedColor;
				if (event.getSource() == red)
					pressedColor = 0;
				else if (event.getSource() == yellow)
					pressedColor = 1;
				else if (event.getSource() == green)
					pressedColor = 2;
				else if (event.getSource() == blue)
					pressedColor = 3;
				else if (event.getSource() == pink)
					pressedColor = 4;
				else
					pressedColor = 5;
				b.findAdjacentSquares(pressedColor);
				b.refreshStepCounter();
			}
		}

	}

	// if an option is selected in the drop down menu, change
	// 	the board size
	public void itemStateChanged(ItemEvent event)  {
		if (event.getSource() == board) {
			boardSize = board.getSelectedIndex();
			b.newGame();
		}
	}
}

// the Canvas class where the game refreshes/redraws
class BoardCanvas extends Canvas implements KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	boolean won;    // determines whether the user has won or not according
					// to the stepCounter variable in FloodIt class
	int[][] color;  // used to initialize the 2D array to 
					// store the color of the squares
	int x; 			// width of each square
	int y; 			// height of each square
	FloodIt parent; // calls the applet class
	Stack<int[]> adjacent = new Stack<int[]>(); // for determining which squares
												// to change color

	// local color constants
	static final Color black = Color.black;
	static final Color white = Color.white;
	static final Color red = Color.red;
	static final Color green = Color.green;
	static final Color blue = Color.blue;
	static final Color yellow = Color.yellow;
	static final Color pink = Color.pink;
	static final Color magenta = Color.magenta;

	// constructor
	public BoardCanvas (FloodIt f) {
		parent = f;
		addKeyListener(this); // for KeyListener
		setFocusable(true);   // for KeyListener
		newGame();
	}

	// draws the board
	public void paint (Graphics g) {
		for (int row = 0; row < boardSize(); row++) {
			for (int col = 0; col < boardSize(); col++) {
				if (color[row][col] == 0) 
					g.setColor(red);
				else if (color[row][col] == 1)
					g.setColor(yellow);
				else if (color[row][col] == 2)
					g.setColor(green);
				else if (color[row][col] == 3)
					g.setColor(blue);
				else if (color[row][col] == 4)
					g.setColor(pink);
				else
					g.setColor(magenta);
				Dimension d = getSize(); // get the size of the board
				x = col*(d.height/boardSize());
				y = row*(d.height/boardSize());
				g.fillRect(x,y,d.height/boardSize(),d.height/boardSize());
			}
		}
		if (won) {
			// shows a pop-up winning window
			JOptionPane.showMessageDialog(null, "You did it! Click \"New game\"" +
												" to play again.");
		} else {
			// shows a pop-up losing window
			if (parent.stepCounter == maxSteps()) {
				JOptionPane.showMessageDialog(null, "Game over! Click \"New game\"" +
													" to try again.", 
						"You lost!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// dialog window for instructions
	public void howToPlay() {
		JOptionPane.showMessageDialog(null, "You start in the top-left corner and " +
				"progress through the rest of the board by selecting different\n" +
				"colors. When you change your current color, every adjacent square " +
				"with the same color also\n" + "changes. To win, flood the entire board " +
				"with the same color in the indicated number of steps.\n\n" + "You can " +
				"change your current color by clicking on the buttons in the color panel " +
				"in the sidebar, or\n" + "by pressing the keys assigned to each color " +
				"on your keyboard. In order to activate the keyboard, \n" + "press on the " +
				"canvas.\n\n" + "You can also change the size of the board by using the " +
				"drop-down menu in the sidebar.\n\n" + "Click on \"New game\" to begin!",
				"How to play Flood-It:", JOptionPane.WARNING_MESSAGE);
	}

	// creates a new game according to different level difficulty
	public void newGame() {

		color = new int[boardSize()][boardSize()];
		// randomly generates the color of each square in the board
		Random randomColor = new Random();
		for (int row = 0; row < boardSize(); row++) {
			for (int col = 0; col < boardSize(); col++) {
				color[row][col] = randomColor.nextInt(6);
			}
		}
		parent.stepCounter = 0;
		refreshStepCounter();
		won = false;
		repaint();
	}

	// determines the maximum steps for each board size
	public int maxSteps () {
		if (parent.boardSize == 0)
			return 22;
		else if (parent.boardSize == 1)
			return 30;
		else 
			return 36;
	}

	// returns the board size according to the option chosen
	public int boardSize () {
		if (parent.boardSize == 0)
			return 12;
		else if (parent.boardSize == 1)
			return 17;
		else 
			return 22;
	}

	// determines which squares to be connected and change color
	public void findAdjacentSquares(int pressedColor) {
		int[] original = {0,0}; // starts with the left corner
		adjacent.push(original);
		int currentColor = color[0][0];

		if (parent.stepCounter < maxSteps() && !won) {
			if (currentColor != pressedColor) {
				while (!adjacent.isEmpty()) {
					// pops the last location in stack to check its 
					// surroundings
					int[] currentLocation = adjacent.pop();
					int row = currentLocation[0];
					int col = currentLocation[1];

					// checks the squares in all four directions of
					// the current location. If the square is the 
					// same color as the current square, push it to
					// the adjacent stack
					try {
						if (color[row][col-1] == currentColor) {
							int[] location = {row,col-1};
							adjacent.push(location);
						} 
					} catch (ArrayIndexOutOfBoundsException e) {}
					try {
						if (color[row][col+1] == currentColor) {
							int[] location = {row,col+1};
							adjacent.push(location);
						} 
					} catch (ArrayIndexOutOfBoundsException e) {}
					try {
						if (color[row+1][col] == currentColor) {
							int[] location = {row+1,col};
							adjacent.push(location);
						}
					} catch (ArrayIndexOutOfBoundsException e) {}
					try {
						if (color[row-1][col] == currentColor) {
							int[] location = {row-1,col};
							adjacent.push(location);
						}
					} catch (ArrayIndexOutOfBoundsException e) {}
					if (color[row][col] != pressedColor) {
						color[row][col] = pressedColor;
					}
				}
				parent.stepCounter += 1;
				won = oneColor(); // checks to see if the board
								  // is flooded with one color
			}
		}
		repaint();
	}

	// updates the step counter in the applet
	public void refreshStepCounter () {
		parent.step.setText("Steps: " + parent.stepCounter
				+ "/" + maxSteps());
	}

	// determines if the board is flooded with one single color using
	// the top left square as reference
	public boolean oneColor () {
		int temp = color[0][0];
		for (int row = 0; row < boardSize(); row++) {
			for (int col = 0; col < boardSize(); col++) {
				if (temp != color[row][col])
					return false;
			}
		}
		return true;
	}

	// handles key events when keys are pressed to change color
	public void keyPressed(KeyEvent event) {
		char c = event.getKeyChar();
		int pressedColor = 10; // arbitrary number
		if (c == 'j') // blue
			pressedColor = 3; 
		else if (c == 'k') // pink
			pressedColor = 4;
		else if (c == 'l') // magenta
			pressedColor = 5;
		else if (c == 'u') // red
			pressedColor = 0;
		else if (c == 'i') // yellow
			pressedColor = 1;
		else {
			if (c == 'o') // green
				pressedColor = 2;
		}
		if (pressedColor >= 0 && pressedColor <= 5) {
			findAdjacentSquares(pressedColor);
			refreshStepCounter();
		}
	}

	public void keyReleased(KeyEvent event) {}

	public void keyTyped(KeyEvent event) {}
}
