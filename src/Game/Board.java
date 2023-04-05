package Game;

import java.awt.Color;
import java.util.LinkedList;

public class Board {
	public static final int MINIMUM_BOARD_SIZE = 3;
	public static final int MAXIMUM_BOARD_SIZE = 16;
	
	public static final int NUM_PLAYERS = 2;
	
	private int boardSize;
	private Cell[][] boardCells;
		
	private GameMode currentGameMode;	
	
	private Player[] players = new Player[NUM_PLAYERS];
	
	private int turn = 0;
	private boolean gameOver = false;
	private Player winner;
	
	private LinkedList<SPair> scoredSPairs = new LinkedList<SPair>();
	
	private String[] playerNames = {"Blue", "Red"};
	private Color[] playerColors = {new Color(50, 100, 255), new Color(255, 100, 50)};
	
	public enum GameMode {
		SIMPLE,
		GENERAL
	}
	
	public enum Cell{
		EMPTY,
		S,
		O
	}
	
	public class SPair {
		public CellPoint pointA;
		public CellPoint pointB;
		
		public Player owner;
		
		SPair(CellPoint a, CellPoint b, Player owner){
			pointA = a;
			pointB = b;
			this.owner = owner;
		}
		
		public boolean isPointInPair(CellPoint point) {
			if (point.isEqual(pointA) || point.isEqual(pointB)) {
				return true;
			}
			else {
				return false;
			}
		}
		
	}
	
	public class CellPoint {
		public int row;
		public int column;
		
		CellPoint(int row, int column){
			this.row = row;
			this.column = column;
		}
		
		public boolean isEqual(CellPoint B) {
			if (this.row == B.row && this.column == B.column) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	
	public Board(int boardSizeGiven, GameMode mode) {
		setBoardSize(boardSizeGiven);
		setGameMode(mode);
				
		boardCells = new Cell[boardSize][boardSize]; // limit checked in setBoardSize()
		setAllCellsEmpty();
		populatePlayerArray();
		
	}
	
	private void setGameMode(GameMode mode) {
		currentGameMode = mode;
	}
	
	public int getBoardSize() {
		return boardSize;
	}
	
	public GameMode getGameMode() {
		return currentGameMode;
	}
	
	public Cell getCell(int row, int column) {
		if (isInBounds(row, column)) {
			return boardCells[row][column];
		}
		else {
			return null;
		}
	}
	
	public boolean isInBounds(int row, int column) {
		if (row < 0 || row >= boardSize || column < 0 || column >= boardSize) {
			return false;
		}
		else {
			return true;
		}
	}
	
	private void setBoardSize(int size) {
		if (size < MINIMUM_BOARD_SIZE) {
			boardSize = MINIMUM_BOARD_SIZE;
		}
		else if (size > MAXIMUM_BOARD_SIZE) {
			boardSize = MAXIMUM_BOARD_SIZE;
		}
		else {
			boardSize = size;
		}
	}
	
	private void setAllCellsEmpty() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				boardCells[i][j] = Cell.EMPTY;
			}
		}
	}
	
	private void populatePlayerArray() {
		for (int i = 0; i < NUM_PLAYERS; i++) {
			Player p = new Player();
			players[i] = p;
			p.setName(playerNames[i]);
			p.setColor(playerColors[i]);
		}
	}
	
	private boolean areAdjacentCellsScored(CellPoint cellA, CellPoint cellB) {
		for (SPair pair : scoredSPairs) {
			if (pair.isPointInPair(cellA) && pair.isPointInPair(cellB)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	// need to store matching cells to draw lines
	private boolean scoreMatchingCells(int cellARow, int cellAColumn, int cellBRow, int cellBColumn) {
		CellPoint cellA = new CellPoint(cellARow, cellAColumn);
		CellPoint cellB = new CellPoint(cellBRow, cellBColumn);
		
		if (!areAdjacentCellsScored(cellA, cellB)) {
			Player scoringPlayer = players[turn];
			SPair newPair = new SPair(cellA, cellB, scoringPlayer);
			scoredSPairs.addLast(newPair);
			scoringPlayer.incrementScore();
			return true;
		}
		else {
			return false;
		}
	}
	
	private void checkScoreCondition(int row, int column) {
		// given an S, check for adjacent O, then recursively check the O
		if (boardCells[row][column] == Cell.S) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (isInBounds(row + i, column + j) && getCell(row + i, column + j) == Cell.O 
							&& (i != 0 || j != 0)) {
						checkScoreCondition(row + i, column + j);
					}
				}
			}
		} // given an O, check for adjacent S cells forming an SOS
		else if (boardCells[row][column] == Cell.O) {
			if (isInBounds(row, column - 1) && isInBounds(row, column + 1) 
					&& getCell(row, column - 1) == Cell.S && getCell(row, column + 1) == Cell.S) {
				scoreMatchingCells(row, column - 1, row, column + 1);
			}
			if (isInBounds(row - 1, column) && isInBounds(row + 1, column)
					&& getCell(row - 1, column) == Cell.S && getCell(row + 1, column) == Cell.S) {
				scoreMatchingCells(row - 1, column, row + 1, column);
			}
			if (isInBounds(row - 1, column - 1) && isInBounds(row + 1, column + 1)
					&& getCell(row - 1, column - 1) == Cell.S && getCell(row + 1, column + 1) == Cell.S) {
				scoreMatchingCells(row - 1, column - 1, row + 1, column + 1);
			}	
			if (isInBounds(row + 1, column - 1) && isInBounds(row - 1, column + 1)
					&& getCell(row + 1, column - 1) == Cell.S && getCell(row - 1, column + 1) == Cell.S) {
				scoreMatchingCells(row + 1, column - 1, row - 1, column + 1);
			}
			
		}
	}
	
	
	public boolean makeMove(int row, int column) {
		if ((row > boardSize - 1) || (row < 0) || (column > boardSize - 1) || (column < 0) || gameOver) {
			return false;
		}
		else {
			Player currentPlayer = players[turn];
			if (boardCells[row][column] == Cell.EMPTY) {
				if (currentPlayer.getActivePiece() == Player.Piece.S) {
					boardCells[row][column] = Cell.S;
				}
				else {
					boardCells[row][column] = Cell.O;
				}
				
				
				int prevNumScores = scoredSPairs.size();
				checkScoreCondition(row, column);
				
				checkGameOver();
				
				if (!(scoredSPairs.size() != prevNumScores && currentGameMode == GameMode.GENERAL)
						&& (gameOver == false)) {
					toggleTurn();
				} 				
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	private boolean isBoardFull() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardCells[i][j] == Cell.EMPTY) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void decideWinner() {
		int highestScore = 0;
		Player winningPlayer = null;
		
		for (Player p : players) {
			if (p.getScore() > highestScore) {
				winningPlayer = p;
				highestScore = p.getScore();
			}
			else if (p.getScore() == highestScore && winningPlayer != null) {
				winningPlayer = null;
			}
		}
		
		winner = winningPlayer;
	}
	
	private void checkGameOver() {
		if (currentGameMode == GameMode.SIMPLE && scoredSPairs.size() > 0) {
			gameOver = true;
		}
		
		if (isBoardFull()) {
			gameOver = true;
		}
		
		if (gameOver) {
			decideWinner();
		}
		
	}
	
	public boolean isGameOver() {
		return gameOver;
	}
	
	private void toggleTurn() {
		turn++;
		if (turn >= NUM_PLAYERS) {
			turn = 0;
		}
	}
	
	public int getTurn() {
		return turn;
	}
	
	public void setPlayerPiece(int playerNum, Player.Piece pieceChoice) {
		players[playerNum].setActivePiece(pieceChoice);
	}
	
	public LinkedList<SPair> getScoredSPairs(){
		return scoredSPairs;
	}
	
	public Player getPlayer(int playerNum) {
		if (playerNum >= 0 && playerNum < NUM_PLAYERS) {
			return players[playerNum];
		}
		else {
			return null;
		}
	}
	
	public int getNumPlayers() {
		return NUM_PLAYERS;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	// for testing
	public void printBoard() {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardCells[i][j] == Cell.S) {
					System.out.print("S");
				}
				else if (boardCells[i][j] == Cell.O) {
					System.out.print("O");
				}
				else {
					System.out.print("_");
				}
				System.out.print(" ");
			}
			System.out.println();
		}
		
		System.out.println();
	}
	
}
