package Game;

import java.util.Arrays;

public class Board {
	public static final int MINIMUM_BOARD_SIZE = 3;
	public static final int MAXIMUM_BOARD_SIZE = 16;
	
	public static final int NUM_PLAYERS = 2;
	
	public enum GameMode {
		SIMPLE,
		GENERAL
	}
	
	public enum Cell{
		EMPTY,
		S,
		O
	}

	private int boardSize;
	private Cell[][] boardCells;
	
	private GameMode currentGameMode;	
	
	private Player[] players = new Player[NUM_PLAYERS];
	
	private int turn = 0;

	
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
			players[i] = new Player();
		}
	}
	
	
	public boolean makeMove(int row, int column) {
		if ((row > boardSize - 1) || (row < 0) || (column > boardSize - 1) || (column < 0)) {
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
								
				toggleTurn();
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	private void toggleTurn() {
		turn = (turn == 0) ? 1 : 0;
	}
	
	public int getTurn() {
		return turn;
	}
	
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
	
	// for testing
	public static void main(String[] args) {
		Board b = new Board(8, GameMode.SIMPLE);
		b.printBoard();
		b.makeMove(5,  5);
		b.makeMove(5, 7);
		b.makeMove(0, 1);
		b.printBoard();
	}
}
