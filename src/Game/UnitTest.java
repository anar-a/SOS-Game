package Game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnitTest {
	@Test
	public void testBoardSizeSetup() {
		Board board = new Board(8, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), 8);
	}
	
	@Test
	public void testMinBoardSize() {
		Board board = new Board(0, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), Board.MINIMUM_BOARD_SIZE);
	}
	
	@Test
	public void testMaxBoardSize() {
		Board board = new Board(100, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), Board.MAXIMUM_BOARD_SIZE);
	}
	
	@Test
	public void testSelectSimpleGame() {
		Board board = new Board(0, Board.GameMode.SIMPLE) ;
		assertEquals(board.getGameMode(), Board.GameMode.SIMPLE);
	}
	
	@Test
	public void testSelectGeneralGame() {
		Board board = new Board(0, Board.GameMode.GENERAL);
		assertEquals(board.getGameMode(), Board.GameMode.GENERAL);
	}

}
