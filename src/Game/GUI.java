package Game;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import Game.Board.GameMode;
import Game.Board.Piece;


public class GUI extends JFrame {
	private JPanel mainPanel;
	
	private JLabel gameTitle;
	private JRadioButton simpleGame;
	private JRadioButton generalGame;
	private JLabel boardSizeLabel;
	private JSpinner boardSizeSpinner;
	
	private JLabel blueSection;
	private JRadioButton blueSChoice;
	private JRadioButton blueOChoice;
	
	private JLabel redSection;
	private JRadioButton redSChoice;
	private JRadioButton redOChoice;
	
	private JButton[][] cellButtons;
	
	private Board gameBoard;
	
	private static final int BOARD_PIXEL_SIZE = 400;

	
	public class Coordinate{
		public int X;
		public int Y;
		Coordinate(int x, int y){
			this.X = x;
			this.Y = y;
		}
	}
				
	public GUI() {
		mainPanel = new JPanel();
		//mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(null);
		
		JPanel titlePanel = createTitleRow();
		mainPanel.add(titlePanel);
		
		gameBoard = new Board(8, Board.GameMode.SIMPLE);
		
		JPanel boardPanel = boardGridSetup(gameBoard);
		mainPanel.add(boardPanel);
		
		JPanel moveType = moveTypeSetup();
		mainPanel.add(moveType);

		
		this.add(mainPanel);
		this.setSize(800,500);
		this.setVisible(true);
	
	}
	
	public JPanel createTitleRow() {
		gameTitle = new JLabel("SOS Game");
		simpleGame = new JRadioButton("Simple Game");
		generalGame = new JRadioButton("General Game");
		
		simpleGame.setSelected(true); // default simple game
		
		// creating horizontal row layout
		JPanel titlePane = new JPanel();
		
		ButtonGroup group = new ButtonGroup();
		group.add(simpleGame);
		group.add(generalGame);
		
		
		titlePane.add(gameTitle);
		titlePane.add(simpleGame);
		titlePane.add(generalGame);
		titlePane.add(sizeEntryComponentSetup());
		
		titlePane.setBounds(0, 0, 800, 50);
		
		return titlePane;
	}
	
	private JPanel sizeEntryComponentSetup() {
		JPanel pane = new JPanel();
		
		boardSizeLabel = new JLabel("Board Size");
		
		SpinnerModel values = new SpinnerNumberModel(8, // default
				Board.MINIMUM_BOARD_SIZE, // min
				Board.MAXIMUM_BOARD_SIZE, // max
				1); // step
		
		JSpinner spinner = new JSpinner(values);
		
		pane.add(boardSizeLabel);
		pane.add(spinner);
		return pane;
	}
	
	private JPanel boardGridSetup(Board gameBoard) {
		int boardSize = gameBoard.getBoardSize();
		cellButtons = new JButton[boardSize][boardSize];
		
		JPanel boardPanel = new JPanel();
		boardPanel.setLayout(new GridLayout(boardSize, boardSize));
		boardPanel.setSize(BOARD_PIXEL_SIZE,BOARD_PIXEL_SIZE);
		boardPanel.setBounds(800/2 - BOARD_PIXEL_SIZE/2, 50, BOARD_PIXEL_SIZE, BOARD_PIXEL_SIZE);
		
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				JButton cell = new JButton();
				
				cell.setFocusPainted(false);
				cell.setContentAreaFilled(false);
				
				cell.addActionListener(new CellClickAction());
				cell.setActionCommand(Integer.toString(i) + "," + Integer.toString(j));
				
				cellButtons[i][j] = cell;
				
				boardPanel.add(cell);
			}
		}
		
		return boardPanel;
	}
	
	private JPanel moveTypeSetup() {
		JPanel panel = new JPanel();
		panel.setBounds(20, 100, 100, 200);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		blueSection = new JLabel("Player");
		blueSChoice = new JRadioButton("S");
		blueOChoice = new JRadioButton("O");
		
		blueSChoice.setSelected(true);
		
		blueSChoice.setActionCommand("S");
		blueSChoice.addActionListener(new PieceChoiceAction());
		
		blueOChoice.setActionCommand("O");
		blueOChoice.addActionListener(new PieceChoiceAction());
		
		ButtonGroup group = new ButtonGroup();
		group.add(blueSChoice);
		group.add(blueOChoice);
		
		panel.add(blueSection);
		panel.add(blueSChoice);
		panel.add(blueOChoice);
		
		return panel;
	}
	
	public Coordinate cellStringToPair(String cellString){
		String[] split = cellString.split(",");
		return new Coordinate(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
	}
	
	public class CellClickAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Coordinate cellXY = cellStringToPair(e.getActionCommand());
			
			JButton clickedButton = cellButtons[cellXY.X][cellXY.Y];
			clickedButton.setText(
					gameBoard.getBlueActivePiece() == Piece.S
					? "S"
					: "O");
		}
	}
	
	public class PieceChoiceAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			gameBoard.setBlueActivePiece(
					e.getActionCommand() == "S"
					? Piece.S
					: Piece.O);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("GUI Class");
		//Board board = new Board(8, GameMode.SIMPLE);
		
		GUI gameGui = new GUI();
	}
}
