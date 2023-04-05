package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;

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
import Game.Board.SPair;


public class GUI extends JFrame {
	private JPanel mainPanel;
	
	private JLabel gameTitle;
	private JRadioButton simpleGame;
	private JRadioButton generalGame;
	
	private JButton startButton;
	
	private JLabel boardSizeLabel;
	private JSpinner boardSizeSpinner;

	private JPanel boardPanel;
	
	private JPanel[] playerMenus;
	
	private JLabel[][] cellLabels;
	
	private Board gameBoard;
	
	private JButton newGameB;
	
	private JPanel gameContainer;
	
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
	
	private void initiateGame() {
		int boardSize = (int) boardSizeSpinner.getValue();
		
		GameMode selectedMode;
		
		if (simpleGame.isSelected()) {
			selectedMode = GameMode.SIMPLE;
		}
		else {
			selectedMode = GameMode.GENERAL;
		}
		
		gameBoard = new Board(boardSize, selectedMode);
		
		gameContainer = new JPanel();
		
		boardPanel = boardGridSetup();
		gameContainer.add(boardPanel);
		playerMenus = new JPanel[gameBoard.getNumPlayers()];
		
		for (int i = 0; i < gameBoard.getNumPlayers(); i++) {
			playerMenus[i] = moveTypeSetup(i);
			gameContainer.add(playerMenus[i]);
		}
		
		playerMenus[0].setLocation(50,100);
		playerMenus[1].setLocation(675,100);
		
		newGameB = setupNewGameButton();
		newGameB.setBounds(620, 400, 150, 25);
		gameContainer.add(newGameB);
		
		gameContainer.setLayout(null);
		gameContainer.setBounds(mainPanel.getBounds());
		mainPanel.add(gameContainer);
		
		redrawTurn();
		mainPanel.repaint();
	}
	
	private class startButtonAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {			
			try {
				boardSizeSpinner.commitEdit();
				initiateGame();
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
	
	private class BoardPanel extends JPanel{
		@Override
		public void paint(Graphics g) {
			super.paint(g);
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setStroke(new BasicStroke(10));

			float boardSize = gameBoard.getBoardSize();
			LinkedList<SPair> scoredSPairs = gameBoard.getScoredSPairs();
			
			float increment = BOARD_PIXEL_SIZE / boardSize;
			
			g2.setStroke(new BasicStroke(3));
			
			for (SPair pair : scoredSPairs) {
				Line2D line = new Line2D.Float(pair.pointA.column * increment + increment/2, 
						pair.pointA.row * increment + increment/2,
						pair.pointB.column * increment + increment/2, 
						pair.pointB.row * increment + increment/2);
				
				g2.setColor(pair.owner.getColor());
				g2.draw(line);
			}

		}
	
	}
	
	private JPanel boardGridSetup() {
		int boardSize = gameBoard.getBoardSize();
		cellLabels = new JLabel[boardSize][boardSize];
		
		BoardPanel boardPanel = new BoardPanel();
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

	private void displayWinnerPanel() {
		JPanel winnerPanel = winnerPanelSetup();
		gameContainer.add(winnerPanel);
		winnerPanel.setBounds(600, 350, 200, 50);
	}
	
	public class boardClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			int row = e.getY() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());
			int column = e.getX() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());
			
			gameBoard.makeMove(row, column);
			redrawBoard();
			redrawTurn();
			
			if (gameBoard.isGameOver()) {
				displayWinnerPanel();
			}
		}
	}
	
	public void redrawBoard() {
		int boardSize = gameBoard.getBoardSize();
				
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
		
		boardPanel.repaint();
		
	}
	
	private void redrawTurn() {
		int turn = gameBoard.getTurn();

		for (int i = 0; i < playerMenus.length; i++) {
			JLabel playerLabel = (JLabel) playerMenus[i].getComponents()[0];
			
			if (i == turn) {
				playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
			}
			else {
				playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
			}
		}
	}
	
	private JPanel moveTypeSetup(int playerNum) {
		JPanel panel = new JPanel();
		panel.setSize(100,200);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		String playerNumString = Integer.toString(playerNum);
		
		JLabel section = new JLabel("Player " + gameBoard.getPlayer(playerNum).getName());
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
	
	private JPanel winnerPanelSetup() {
		JPanel panel = new JPanel();
		JLabel winnerLabel = new JLabel("");
		Player winner = gameBoard.getWinner();
		
		if (winner == null) {
			winnerLabel.setText("Game Draw");
		}
		else {
			winnerLabel.setText("Winner: " + winner.getName());
		}
		
		winnerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
		
		panel.add(winnerLabel);
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
	
	private JButton setupNewGameButton() {
		JButton button = new JButton("New Game");
		
		// vanity
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		
		button.addActionListener(new onNewGameAction());
		
		return button;
	}
	
	private class onNewGameAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			mainPanel.remove(gameContainer);
			initiateGame();
			mainPanel.repaint();
		}
		
	}
	
	public static void main(String[] args) {
		new GUI();
	}
}
