package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.print.attribute.IntegerSyntax;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

import Game.Board.Cell;
import Game.Board.CellPoint;
import Game.Board.GameMode;
import Game.Board.SPair;
import Game.Player.Piece;


public class GUI extends JFrame {
	private static final int BOARD_PIXEL_SIZE = 400;
	private static final Dimension MAIN_PANEL_SIZE = new Dimension(800, 500);
	private static final int GRID_GAP = 2;
	private static final Point[] PLAYER_MENU_LOCATIONS = new Point[] {
			new Point(50, 75),
			new Point(650, 75),
			new Point(50, 250),
			new Point(650, 250)};
	
	private JPanel mainPanel;
	
	private JLabel gameTitle;
	private JRadioButton simpleGame;
	private JRadioButton generalGame;
	
	private JButton startButton;
	
	private JLabel boardSizeLabel;
	private JSpinner boardSizeSpinner;
	private JSpinner playerCountSpinner;

	private JPanel boardPanel;
	
	private JPanel[] playerMenus;
	
	private JLabel[][] cellLabels;
	
	private Board gameBoard;
	
	private JButton newGameB;
	
	private JPanel gameContainer;
	private JPanel winnerPanel;
	
	private boolean isRecording;
	private boolean replaying = false;
	
				
	public GUI() {
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		JPanel titlePanel = createTitleRow();
		startButton = createStartButton();
		
		mainPanel.add(titlePanel);
		mainPanel.add(startButton);
		
		this.add(mainPanel);
		this.setSize(MAIN_PANEL_SIZE);
		this.setVisible(true);
	
	}
	
	private JPanel preGameSetup() {
		JPanel preGameContainer = new JPanel();
		preGameContainer.setLayout(null);
		
		JPanel titlePanel = createTitleRow();
		startButton = createStartButton();

		preGameContainer.add(titlePanel);
		preGameContainer.add(startButton);
		
		preGameContainer.setSize(MAIN_PANEL_SIZE);
		
		return preGameContainer;
	}
	
	private JButton createStartButton() {
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
		int playerCount = (int) playerCountSpinner.getValue();
		
		GameMode selectedMode;
		
		if (simpleGame.isSelected()) {
			selectedMode = GameMode.SIMPLE;
		}
		else {
			selectedMode = GameMode.GENERAL;
		}
		
		
		gameBoard = new Board(boardSize, selectedMode, playerCount) { // values validated in board
			@Override
			public void onMoveEvent() {
				afterMoveEvent();
			}
		};
		
		gameContainer = new JPanel();
		
		boardPanel = boardGridSetup();
		gameContainer.add(boardPanel);
		playerMenus = new JPanel[gameBoard.getNumPlayers()];
		
		for (int i = 0; i < gameBoard.getNumPlayers(); i++) {
			playerMenus[i] = moveTypeSetup(i);
			gameContainer.add(playerMenus[i]);
			playerMenus[i].setLocation(PLAYER_MENU_LOCATIONS[i]);
		}
		
		
		newGameB = setupNewGameButton();
		newGameB.setBounds(620, 400, 150, 25);
		gameContainer.add(newGameB);
		
		gameContainer.setLayout(null);
		gameContainer.setBounds(mainPanel.getBounds());
		
		JCheckBox recordB = recordButtonSetup();
		recordB.setBounds(30, 430, 150, 25);
		gameContainer.add(recordB);
		
		JButton replayB = replayButtonSetup();
		replayB.setBounds(620, 430, 150, 25);
		gameContainer.add(replayB);
		
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
	
	private JPanel createTitleRow() {
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
		titlePane.add(playerCountSpinnerSetup());
		
		titlePane.setBounds(0, 0, 800, 50);
		
		titlePane.repaint();
		
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
	
	private JPanel playerCountSpinnerSetup() {
		JPanel pane = new JPanel();
		
		JLabel spinnerLabel = new JLabel("Player Count");
		
		SpinnerModel values = new SpinnerNumberModel(2,
				Board.MINIMUM_NUM_PLAYERS,
				Board.MAXIMUM_NUM_PLAYERS,
				1);
		
		playerCountSpinner = new JSpinner(values);
		
		pane.add(spinnerLabel);
		pane.add(playerCountSpinner);
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
		winnerPanel = winnerPanelSetup();
		gameContainer.add(winnerPanel);
		winnerPanel.setBounds(20, 400, 130, 25);
	}
	
	private void checkWinCondition() {
		if (gameBoard.isGameOver() && winnerPanel == null) {
			displayWinnerPanel();
			if (isRecording) {
				gameBoard.makeHistoryFile();				
			}
		}
	}
	
	private void afterMoveEvent() {
		redrawTurn();
		checkWinCondition();
		redrawBoard();
	}
	
	private class boardClick extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (!gameBoard.getPlayer(gameBoard.getTurn()).isComputer()) {
				int row = e.getY() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());
				int column = e.getX() / (BOARD_PIXEL_SIZE / gameBoard.getBoardSize());

				gameBoard.makeMove(row, column);
			}
		}
	}
	
	private void redrawBoard() {
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
				else {
					cellLabels[i][j].setText("");
				}
			}
		}
		
		boardPanel.repaint();
		boardPanel.revalidate();
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
		panel.setSize(100,125);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
		JLabel section = new JLabel("Player " + gameBoard.getPlayer(playerNum).getName());
		panel.add(section);		
		
		JRadioButton sChoice = new JRadioButton("S");
		JRadioButton oChoice = new JRadioButton("O");
		JRadioButton humanChoice = new JRadioButton("Human");
		JRadioButton computerChoice = new JRadioButton("Computer");
		humanChoice.setSelected(true);

		sChoice.setSelected(true);
		
		sChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameBoard.setPlayerPiece(playerNum, Player.Piece.S);				
			}
		});
		
		oChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameBoard.setPlayerPiece(playerNum, Player.Piece.O);				
				
			}
		});
		
		humanChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Player currentPlayer = gameBoard.getPlayer(playerNum);
				gameBoard.setPlayerComputerStatus(currentPlayer, false);
				redrawBoard();
				redrawTurn();
				
				Piece p = sChoice.isSelected() ? Piece.S : Piece.O; 
				currentPlayer.setActivePiece(p);
			}
		});
		
		computerChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gameBoard.setPlayerComputerStatus(gameBoard.getPlayer(playerNum), true);
				redrawBoard();
				redrawTurn();
			}
		});
		
		ButtonGroup humanComputerGroup = new ButtonGroup();
		humanComputerGroup.add(humanChoice);
		humanComputerGroup.add(computerChoice);
		
		ButtonGroup group = new ButtonGroup();
		group.add(sChoice);
		group.add(oChoice);
		
		panel.add(humanChoice);
		panel.add(sChoice);
		panel.add(oChoice);	
		panel.add(Box.createVerticalStrut(10));
		panel.add(computerChoice);
		
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
			if (!replaying) {
				mainPanel.remove(gameContainer);
				winnerPanel = null;

				gameBoard.terminateGame();

				initiateGame();

				mainPanel.revalidate();
				mainPanel.repaint();
			}
		}
		
	}
	
	private JCheckBox recordButtonSetup() {
		isRecording = false;
		JCheckBox recordB = new JCheckBox("Record Game");
		
		recordB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isRecording = recordB.isSelected();
			}
		});
		
		return recordB;
	}
	
	private void replayGame() {
		if (gameBoard.isGameOver() && !replaying) {
			replaying = true;
			LinkedList<CellPoint> moveHistory = gameBoard.getMoveHistory();
			
			Board newBoard = new Board(gameBoard.getBoardSize(), gameBoard.getGameMode(), gameBoard.getNumPlayers()) {
				@Override
				public void onMoveEvent() {
					afterMoveEvent();
				}
			};
			Board oldBoard = gameBoard;
			gameBoard = newBoard;
			
			gameContainer.remove(winnerPanel);
			winnerPanel = null;
			
			redrawTurn();
			redrawBoard();
			
			mainPanel.revalidate();
			mainPanel.repaint();
			
			ListIterator<CellPoint> it = moveHistory.listIterator(moveHistory.size());
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						while (it.hasPrevious()) {
							Thread.sleep(500);
							CellPoint p = it.previous();
							
							Piece moveMade = oldBoard.getCell(p.row, p.column) == Cell.S? Piece.S : Piece.O;
							gameBoard.setPlayerPiece(gameBoard.getTurn(), moveMade);
							gameBoard.makeMove(p.row, p.column);
						}
					} 
					catch (Exception ex) {
						ex.printStackTrace();
					}
					finally {
						replaying = false;
					}
				}
			
			});
			t.start();
		}
	}
	
	private JButton replayButtonSetup() {
		JButton replayB = new JButton("Replay");
		replayB.setFocusPainted(false);
		replayB.setContentAreaFilled(false);
		
		replayB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replayGame();
			}
		});
		
		return replayB;
	}
	public static void main(String[] args) {
		new GUI();
		
	}
}
