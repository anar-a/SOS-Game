package Game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Game.Board.Cell;
import Game.Player.Piece;

class UnitTest {
	@Test
	public void testBoardSizeSetup() {
		Board board = new Board(8, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), 8);
	}
	
	@Test
	public void testMinBoardSize() {
		Board board = new Board(Board.MINIMUM_BOARD_SIZE - 1, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), Board.MINIMUM_BOARD_SIZE);
		
		board = new Board(Board.MINIMUM_BOARD_SIZE - 1, Board.GameMode.GENERAL);
		assertEquals(board.getBoardSize(), Board.MINIMUM_BOARD_SIZE);
	}
	
	@Test
	public void testMaxBoardSize() {
		Board board = new Board(Board.MAXIMUM_BOARD_SIZE + 1, Board.GameMode.SIMPLE);
		assertEquals(board.getBoardSize(), Board.MAXIMUM_BOARD_SIZE);
		
		board = new Board(Board.MAXIMUM_BOARD_SIZE + 1, Board.GameMode.GENERAL);
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

	
	@Test
	public void testSelectSimpleGameNSize() {		
		for (int i = Board.MINIMUM_BOARD_SIZE; i <= Board.MAXIMUM_BOARD_SIZE; i++) {
			Board board = new Board(i, Board.GameMode.SIMPLE);
			assertEquals(board.getBoardSize(), i);
			assertEquals(board.getGameMode(), Board.GameMode.SIMPLE);
		}
		
	}
	
	@Test
	public void testSelectGeneralGameNSize() {
		for (int i = Board.MINIMUM_BOARD_SIZE; i <= Board.MAXIMUM_BOARD_SIZE; i++) {
			Board board = new Board(i, Board.GameMode.GENERAL);
			assertEquals(board.getBoardSize(), i);
			assertEquals(board.getGameMode(), Board.GameMode.GENERAL);
		}	
	}
	
	@Test
	public void testValidSimpleGameMove() {
		int boardSize = (Board.MAXIMUM_BOARD_SIZE + Board.MINIMUM_BOARD_SIZE)/2;
		Board board = new Board(boardSize, Board.GameMode.SIMPLE);
		assertEquals(board.makeMove(0, 0), true);
	}
	
	@Test
	public void testOutOfBoundsSimpleGameMove() {
		int boardSize = (Board.MAXIMUM_BOARD_SIZE + Board.MINIMUM_BOARD_SIZE)/2;
		Board board = new Board(boardSize, Board.GameMode.SIMPLE);
		assertEquals(board.makeMove(Board.MAXIMUM_BOARD_SIZE + 1, 0), false);
	}
	
	@Test
	public void testValidGeneralGameMove() {
		int boardSize = (Board.MAXIMUM_BOARD_SIZE + Board.MINIMUM_BOARD_SIZE)/2;
		Board board = new Board(boardSize, Board.GameMode.GENERAL);
		assertEquals(board.makeMove(0, 0), true);
	}
	
	@Test
	public void testOutOfBoundsGeneralGameMove() {
		int boardSize = (Board.MAXIMUM_BOARD_SIZE + Board.MINIMUM_BOARD_SIZE)/2;
		Board board = new Board(boardSize, Board.GameMode.GENERAL);
		assertEquals(board.makeMove(Board.MAXIMUM_BOARD_SIZE + 1, 0), false);
	}
	
	@Test
	public void testSimpleGameOverWin() {
		Board board = new Board(3, Board.GameMode.SIMPLE);
		board.makeMove(0, 0);
		
		board.getPlayer(1).setActivePiece(Piece.O);
		board.makeMove(1, 1);
		
		board.makeMove(2, 2);
		assertEquals(board.isGameOver(), true);
		assertEquals(board.getWinner(), board.getPlayer(0));
	}
	
	@Test
	public void testSimpleGameOverDraw() {
		Board board = new Board(3, Board.GameMode.SIMPLE);
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				board.makeMove(i, j);
			}
		}
		
		assertEquals(board.isGameOver(), true);
		assertEquals(board.getWinner(), null);
	}
	
	@Test
	public void testGeneralGameOverWin() {
		Board board = new Board(3, Board.GameMode.GENERAL);
		
		board.makeMove(0, 0);
		
		board.getPlayer(1).setActivePiece(Piece.O);
		board.makeMove(1, 1);
		
		board.makeMove(2, 2);
		
		board.getPlayer(1).setActivePiece(Piece.S);

		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				if (board.getCell(i, j) == Cell.EMPTY) {
					board.makeMove(i, j);
				}
			}
		}
		
		assertEquals(board.isGameOver(), true);
		assertEquals(board.getWinner(), board.getPlayer(1));
		assertEquals(board.getPlayer(1).getScore(), 3);
		assertEquals(board.getPlayer(0).getScore(), 1);
		
	}
	
	@Test
	public void testGeneralGameOverDraw() {
		Board board = new Board(3, Board.GameMode.GENERAL);
		
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				board.makeMove(i, j);
			}
		}
		
		assertEquals(board.isGameOver(), true);
		assertEquals(board.getWinner(), null);

	}
}
