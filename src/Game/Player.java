package Game;

import java.awt.Color;

public class Player {
	public static enum Piece{
		S,
		O
	}
	
	private Color playerColor = Color.BLACK;
	private Piece activePiece = Piece.S;
	
	private int score = 0;
	private String name;
	
	public void setActivePiece(Piece piece) {
		activePiece = piece;
	}
	
	public Piece getActivePiece() {
		return activePiece;
	}
	
	public void setColor(Color color) {
		playerColor = color;
	}
	
	public Color getColor() {
		return playerColor;
	}
	
	public void incrementScore() {
		score++;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
}
