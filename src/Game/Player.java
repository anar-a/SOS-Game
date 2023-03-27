package Game;

import Game.Board.Piece;

public class Player {
	public static enum Piece{
		S,
		O
	}
	
	private Piece activePiece = Piece.S;
	
	public void setActivePiece(Piece piece) {
		activePiece = piece;
	}
	
	public Piece getActivePiece() {
		return activePiece;
	}
}
