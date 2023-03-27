package Game;

import java.util.Arrays;

public class Board {
	public static final int MINIMUM_BOARD_SIZE = 3;
	public static final int MAXIMUM_BOARD_SIZE = 16;
	
	public enum GameMode {
		SIMPLE,
		GENERAL
	}
	
	public enum Piece{
		S,
		O
	}
	
	public enum Cell{
		EMPTY,
		S,
		O
	}
	
	private int boardSize;
	private GameMode currentGameMode;
	
	private Piece blueActivePiece;
	private Piece redActivePiece;
	
	private Cell[][] boardCells;
	
	public Board(int boardSizeGiven, GameMode mode) {
		setBoardSize(boardSizeGiven);
		setGameMode(mode);
		
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
	
	private void setGameMode(GameMode mode) {
		currentGameMode = mode;
	}
	
	public void setBlueActivePiece(Piece piece) {
		blueActivePiece = piece;
	}
	
	public Piece getBlueActivePiece() {
		return blueActivePiece;
	}
	
	public Piece getRedActivePiece() {
		return redActivePiece;
	}
	
	public void setRedActivePiece(Piece piece) {
		redActivePiece = piece;
	}
	
	public int getBoardSize() {
		return boardSize;
	}
	
	public GameMode getGameMode() {
		return currentGameMode;
	}
}
