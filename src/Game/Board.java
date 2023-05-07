package Game;

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import Game.Player.Piece;

public class Board {
	public static final int MINIMUM_BOARD_SIZE = 3;
	public static final int MAXIMUM_BOARD_SIZE = 16;
	
	public static final int MINIMUM_NUM_PLAYERS = 2;
	public static final int MAXIMUM_NUM_PLAYERS = 4;
	
	private int numPlayers;

	private int boardSize;
	private Cell[][] boardCells;
		
	private GameMode currentGameMode;	
	
	private Player[] players;
	
	private int turn = 0;
	private boolean gameOver = false;
	private Player winner;
	
	private LinkedList<SPair> scoredSPairs = new LinkedList<SPair>();
	private LinkedList<CellPoint> moveHistory = new LinkedList<CellPoint>();
	
	private String[] playerNames = {"Blue", "Red", "Green", "Magenta"};
	private Color[] playerColors = {new Color(50, 100, 255),
			new Color(255, 100, 50),
			new Color(100, 255, 50),
			new Color(255, 0, 255)};
	
	private Thread t1;
	private int computerMoveDelay = 1000;
	
	
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
		public Player owner;
		
		CellPoint(int row, int column){
			this.row = row;
			this.column = column;
		}
		
		CellPoint(int row, int column, Player owner){
			this.row = row;
			this.column = column;
			this.owner = owner;
		}
		
		
		public boolean isEqual(CellPoint B) {
			if (this.row == B.row && this.column == B.column) {
				return true;
			}
			else {
				return false;
			}
		}
		
		public boolean isCellInBounds() {
			return isInBounds(row, column);
		}
	}
	
	
	public Board(int boardSizeGiven, GameMode mode, int playerCount) {
		setBoardSize(boardSizeGiven);
		setGameMode(mode);
				
		boardCells = new Cell[boardSize][boardSize]; // limit checked in setBoardSize()
		setAllCellsEmpty();
		
		setNumPlayers(playerCount);
		players = new Player[numPlayers];
		populatePlayerArray();

		
	}
	
	private void setNumPlayers(int playerCount) {
		if (playerCount > MAXIMUM_NUM_PLAYERS) {
			numPlayers = MAXIMUM_NUM_PLAYERS;
		}
		else if (playerCount < MINIMUM_NUM_PLAYERS) {
			numPlayers = MINIMUM_NUM_PLAYERS;
		}
		else {
			numPlayers = playerCount;
		}
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
	
	public boolean isPlayer(int playerNum) {
		if (playerNum > 0 && playerNum < numPlayers) {
			return true;
		}
		else {
			return false;
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
		for (int i = 0; i < numPlayers; i++) {
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
	private boolean scoreMatchingCells(CellPoint cellA, CellPoint cellB) {
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
	
	private int[][][] adjacentSOffsets = {
			// format of adjacent cells to the middle one which enforces score condition
			// Cell A: row offset, column offset; Cell B: row offset, column offset
		{{0,-1}, {0,1}},
		{{-1,0}, {1,0}},
		{{-1, -1}, {1, 1}},
		{{1, -1}, {-1, 1}}
	};
	
	private boolean areTwoCellsSPieces(CellPoint a, CellPoint b) {
		if (a.isCellInBounds() &&
				b.isCellInBounds() &&
				getCell(a.row, a.column) == Cell.S &&
				getCell(b.row, b.column) == Cell.S) {
			return true;
		}
		else {
			return false;
		}
	}
	
	private void grantScoreCondition(int row, int column) {
		// given an S, check for adjacent O, then recursively check the O
		if (boardCells[row][column] == Cell.S) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (isInBounds(row + i, column + j) && getCell(row + i, column + j) == Cell.O 
							&& (i != 0 || j != 0)) {
						grantScoreCondition(row + i, column + j);
					}
				}
			}
		} // given an O, check for adjacent S cells forming an SOS
		else if (boardCells[row][column] == Cell.O) {
			for (int[][] offsetPair : adjacentSOffsets) {
				int[] offsetA = offsetPair[0];
				int[] offsetB = offsetPair[1];
				
				CellPoint a = new CellPoint(row + offsetA[0], column + offsetA[1]);
				CellPoint b = new CellPoint(row + offsetB[0], column + offsetB[1]);

				if (areTwoCellsSPieces(a, b)) {
					scoreMatchingCells(a, b);
				}

			}

		}
	}
	
	private boolean checkScoreCondition(int row, int column) {
		// given an S, check for adjacent O, then recursively check the O
		if (boardCells[row][column] == Cell.S) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (isInBounds(row + i, column + j) && getCell(row + i, column + j) == Cell.O 
							&& (i != 0 || j != 0)) {
						return false || checkScoreCondition(row + i, column + j);
					}
				}
			}
		} // given an O, check for adjacent S cells forming an SOS
		else {
			for (int[][] offsetPair : adjacentSOffsets) {
				int[] offsetA = offsetPair[0];
				int[] offsetB = offsetPair[1];
				
				CellPoint a = new CellPoint(row + offsetA[0], column + offsetA[1]);
				CellPoint b = new CellPoint(row + offsetB[0], column + offsetB[1]);

				if (areTwoCellsSPieces(a, b) && !areAdjacentCellsScored(a, b)) {
					return true;
				}
				
				

			}
		}
		
		return false;
	}
	
	
		
	public boolean makeMove(int row, int column) {
		if ((row > boardSize - 1) || (row < 0) || (column > boardSize - 1) || (column < 0) || gameOver || t1 != null) {
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
				
				moveHistory.addFirst(new CellPoint(row, column, players[turn]));
				
				int prevNumScores = scoredSPairs.size();
				
				grantScoreCondition(row, column);
				checkGameOver();				

				if (!(scoredSPairs.size() != prevNumScores && currentGameMode == GameMode.GENERAL)
						&& (gameOver == false)) {
					toggleTurn();
				} 		
				
				onMoveEvent();
				
				if (getPlayer(turn).isComputer()) {
					t1 = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(computerMoveDelay);
								t1 = null;
								promptComputerMove();
							}
							catch (Exception e) {
								System.out.println(e.getStackTrace());
							};
						}
					});
					t1.start();
				}

				return true;
			}
			else {
				return false;
			}
		}
	}

	public void makeRandomMove() {
		while (true) {
			Random rand = new Random();
			int randomRow = rand.nextInt(0, boardSize);
			int randomColumn = rand.nextInt(0, boardSize);
			int randomPiece = rand.nextInt(0, 2);
			
			Piece p = randomPiece == 0 ? Piece.S : Piece.O;
			players[turn].setActivePiece(p);
			
			if (getCell(randomRow, randomColumn) == Cell.EMPTY) {
				makeMove(randomRow, randomColumn);
				return;
			}
		}
	}
	
	public boolean attemptOScore() {
		for (CellPoint c : moveHistory) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					if (i != 0 || j != 0) {
						if (isInBounds(c.row + i, c.column + j)){
							if (checkScoreCondition(c.row + i, c.column + j)) {
								getPlayer(turn).setActivePiece(Player.Piece.O);
								makeMove(c.row + i, c.column + j);
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean checkAllAdjacentsFull(CellPoint cell) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (isInBounds(cell.row + i, cell.column + j) &&
						getCell(cell.row + i, cell.column + j) == Cell.EMPTY) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean makeEducatedMove() {
		if (moveHistory.size() > 0) {
			CellPoint latestCell = moveHistory.getFirst();
			
			int i = 0;
			int j = 0;

			Random rand = new Random();
			
			while ((i == 0 && j == 0) || getCell(latestCell.row + i, latestCell.column + j) != Cell.EMPTY) {
				i = rand.nextInt(-1, 2);
				j = rand.nextInt(-1, 2);
				
				if (checkAllAdjacentsFull(latestCell)) {
					return false;
				}
			}
			
			Cell previous = getCell(latestCell.row, latestCell.column);
			getPlayer(turn).setActivePiece(previous == Cell.S ? Piece.O : Piece.S);
			
			makeMove(latestCell.row + i, latestCell.column + j);
			return true;
		}
		
		return false;
	}
	
	public void promptComputerMove() {
		if (isGameOver()) {
			return;
		}
		else if (getPlayer(turn).isComputer()) {
			if (moveHistory.size() == 0) {
				makeRandomMove();
			}
			else {
				if (!attemptOScore()) {
					if (!makeEducatedMove()) {
						makeRandomMove();
					}
				}
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
	
	public void terminateGame() {
		gameOver = true;
		winner = null;
	}
	
	private void toggleTurn() {
		turn++;
		if (turn >= numPlayers) {
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
	
	public LinkedList<CellPoint> getMoveHistory(){
		return moveHistory;
	}
	
	public Player getPlayer(int playerNum) {
		if (playerNum >= 0 && playerNum < numPlayers) {
			return players[playerNum];
		}
		else {
			return null;
		}
	}
	
	public int getNumPlayers() {
		return numPlayers;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public void setPlayerComputerStatus(Player player, boolean isComputer) {
		player.setComputerPlayer(isComputer);
		if (player == players[turn] && players[turn].isComputer()) {
			promptComputerMove();
		}
	}
	
	public void setComputerMoveDelay(int delayInMilliseconds) {
		if (delayInMilliseconds >= 0) {
			computerMoveDelay = delayInMilliseconds;
		}
	}
	
	// called for every valid move made
	public void onMoveEvent() {}
	
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
	
	public String getBoardString() {
		String s = "";
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (boardCells[i][j] == Cell.S) {
					s += "S";
				}
				else if (boardCells[i][j] == Cell.O) {
					s += "O";
				}
				else {
					s += "_";
				}
				s +=" ";
			}
			s += "\n";
		}
		
		s += "\n";
		return s;
	}
	
	public void printMoveHistory() {
		ListIterator<CellPoint> it = moveHistory.listIterator(moveHistory.size());
		while (it.hasPrevious()) {
			CellPoint p = it.previous();
			System.out.print("Row: ");
			System.out.print(p.row);
			System.out.print(" Column: ");
			System.out.println(p.column);
		}
	}
	
	public void makeHistoryFile() {
		ListIterator<CellPoint> it = moveHistory.listIterator(moveHistory.size());	
		
		try {
			File writeFile = new File("GameRecording.txt");
			writeFile.createNewFile();
			FileWriter fileWriter = new FileWriter(writeFile.getName());
			int moveCount = 1;
			
			while (it.hasPrevious()) {
				CellPoint p = it.previous();
				fileWriter.write(Integer.toString(moveCount));
				fileWriter.write(": ");
				fileWriter.write("Player ");
				fileWriter.write(p.owner.getName());
				fileWriter.write(" - Row: ");
				fileWriter.write(Integer.toString(p.row + 1));
				fileWriter.write(" Column: ");
				fileWriter.write(Integer.toString(p.column + 1));
				fileWriter.write("\n");
				moveCount++;
			}
			
			fileWriter.write(getBoardString());
			
			fileWriter.close();
			System.out.println("Game recording file successfully made.");
			
		} catch(IOException e) {
			System.out.println("Error creating the move history file.");
			e.printStackTrace();
		}
	}
	
	
}
