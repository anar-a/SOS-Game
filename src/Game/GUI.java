package Game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;

import javax.print.attribute.IntegerSyntax;
import javax.swing.Action;
import javax.swing.BorderFactory;
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
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import Game.Board.GameMode;
import Game.Board.Piece;


public class GUI extends JFrame {
	private JPanel mainPanel;
	
	private JLabel gameTitle;
	private JRadioButton simpleGame;
	private JRadioButton generalGame;
	
	private JButton startButton;
	
	private JLabel boardSizeLabel;
	private JSpinner boardSizeSpinner;

	private JPanel boardPanel;
	
	private JPanel player0Menu;
	private JPanel player1Menu;
	
	private JLabel player0Label;
	private JLabel player1Label;
	
	private JLabel[][] cellLabels;
	
	private Board gameBoard;
	
	private static final int BOARD_PIXEL_SIZE = 400;
	private static final int GRID_GAP = 2;
				
	public GUI() {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		JPanel titlePanel = createTitleRow();
		mainPanel.add(titlePanel);
		
		startButton = createStartButton();
		this.add(startButton);
		
		this.add(mainPanel);
		this.setSize(800,500);
		this.setVisible(true);
	
	}
	
	public JButton createStartButton() {
		JButton startButton = new JButton("Start Game");
		startButton.setBounds(300, 200, 200, 50);
		
		// vanity
		startButton.setFocusPainted(false);
		startButton.setContentAreaFilled(false);
		
		startButton.addActionListener(new startButtonAction());
		
		
		return startButton;
	}
	
	private class startButtonAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				boardSizeSpinner.commitEdit();
				int boardSize = (int) boardSizeSpinner.getValue();
				
				Board.GameMode selectedMode;
				
				if (simpleGame.isSelected()) {
					selectedMode = Board.GameMode.SIMPLE;
				}
				else {
					selectedMode = Board.GameMode.GENERAL;
				}
				
				gameBoard = new Board(boardSize, selectedMode);
				
				boardPanel = boardGridSetup();
				mainPanel.add(boardPanel);
				
				player0Menu = moveTypeSetup("Blue", 0);
				player0Menu.setLocation(50,100);
				mainPanel.add(player0Menu);
				
				player1Menu = moveTypeSetup("Red", 1);
				player1Menu.setLocation(675,100);
				mainPanel.add(player1Menu);
				
				redrawTurn();
				
				startButton.setVisible(false);
				
			}
			catch (java.text.ParseException err) {
				System.out.print(err);
			}
		}
		
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
		
		SpinnerModel values = new SpinnerNumberModel(9, // default
				Board.MINIMUM_BOARD_SIZE, // min
				Board.MAXIMUM_BOARD_SIZE, // max
				1); // step
		
		boardSizeSpinner = new JSpinner(values);
		
		pane.add(boardSizeLabel);
		pane.add(boardSizeSpinner);
		return pane;
	}
	
	private JPanel boardGridSetup() {
		int boardSize = gameBoard.getBoardSize();
		cellLabels = new JLabel[boardSize][boardSize];
		
		JPanel boardPanel = new JPanel();
		GridLayout layout = new GridLayout(boardSize, boardSize);
		layout.setHgap(GRID_GAP);
		layout.setVgap(GRID_GAP);
		
		boardPanel.setLayout(layout);
		boardPanel.setSize(BOARD_PIXEL_SIZE,BOARD_PIXEL_SIZE);
		boardPanel.setBounds(800/2 - BOARD_PIXEL_SIZE/2, 50, BOARD_PIXEL_SIZE, BOARD_PIXEL_SIZE);
		
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				JLabel cell = new JLabel("", SwingConstants.CENTER);
				
				cell.setFont(new Font("Arial", Font.PLAIN, (int) (BOARD_PIXEL_SIZE / boardSize / 1.5)));
				cell.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
				
				cellLabels[i][j] = cell;
				
				boardPanel.add(cell);
			}
		}
		
		boardPanel.addMouseListener(new boardClick());
		
		return boardPanel;
	}

	public class boardClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int row = e.getY() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());
			int column = e.getX() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());
			
			gameBoard.makeMove(row, column);
			redrawBoard();
			redrawTurn();
		}
	}
	
	public void redrawBoard() {
		int boardSize = gameBoard.getBoardSize();
				
		System.out.println(gameBoard.getTurn());
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				Board.Cell currentCell = gameBoard.getCell(i, j);
				if (currentCell == Board.Cell.S) {
					cellLabels[i][j].setText("S");
				}
				else if (currentCell == Board.Cell.O) {
					cellLabels[i][j].setText("O");
				}
			}
		}
	}
	
	public void redrawTurn() {
		int turn = gameBoard.getTurn();
		
		JLabel player0Label = (JLabel) player0Menu.getComponents()[0];
		JLabel player1Label = (JLabel) player1Menu.getComponents()[0];
		
		if (turn == 0) {
			player0Label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			player1Label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
		}
		else if (turn == 1) {
			player0Label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
			player1Label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
	}
	
	private JPanel moveTypeSetup(String playerColor, int playerNum) {
		JPanel panel = new JPanel();
		panel.setSize(100,200);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		String playerNumString = Integer.toString(playerNum);
		
		JLabel section = new JLabel("Player " + playerColor);
		JRadioButton sChoice = new JRadioButton("S");
		JRadioButton oChoice = new JRadioButton("O");
		
		sChoice.setSelected(true);
		
		sChoice.setActionCommand(playerNumString + "S");
		sChoice.addActionListener(new pieceChoiceAction());
		
		oChoice.setActionCommand(playerNumString + "O");
		oChoice.addActionListener(new pieceChoiceAction());
		
		ButtonGroup group = new ButtonGroup();
		group.add(sChoice);
		group.add(oChoice);
		
		panel.add(section);
		panel.add(sChoice);
		panel.add(oChoice);
		
		return panel;
	}
	
	private class pieceChoiceAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			int playerNum = Integer.parseInt(String.valueOf(actionCommand.charAt(0)));
			char pieceChoiceChar = actionCommand.charAt(1);
			
			Player.Piece pieceChoice;
			
			if (pieceChoiceChar == 'S') {
				pieceChoice = Player.Piece.S;
			}
			else {
				pieceChoice = Player.Piece.O;
			}
			
			gameBoard.setPlayerPiece(playerNum, pieceChoice);
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("GUI Class");		
		GUI gameGui = new GUI();
	}
}
