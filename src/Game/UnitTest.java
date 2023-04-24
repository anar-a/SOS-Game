package Game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import Game.Board.Cell;
import Game.Player.Piece;

class UnitTest {
	int validBoardSize = (Board.MAXIMUM_BOARD_SIZE + Board.MINIMUM_BOARD_SIZE)/2;

	@Test
	public void testBoardSizeSetup() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		assertEquals(validBoardSize, board.getBoardSize());
	}
	
	@Test
	public void testMinBoardSize() {
		Board board = new Board(Board.MINIMUM_BOARD_SIZE - 1, Board.GameMode.SIMPLE);
		assertEquals(Board.MINIMUM_BOARD_SIZE, board.getBoardSize());
		
		board = new Board(Board.MINIMUM_BOARD_SIZE - 1, Board.GameMode.GENERAL);
		assertEquals(Board.MINIMUM_BOARD_SIZE, board.getBoardSize());
	}
	
	@Test
	public void testMaxBoardSize() {
		Board board = new Board(Board.MAXIMUM_BOARD_SIZE + 1, Board.GameMode.SIMPLE);
		assertEquals(Board.MAXIMUM_BOARD_SIZE, board.getBoardSize());
		
		board = new Board(Board.MAXIMUM_BOARD_SIZE + 1, Board.GameMode.GENERAL);
		assertEquals(Board.MAXIMUM_BOARD_SIZE, board.getBoardSize());
	}
	
	@Test
	public void testSelectSimpleGame() {
		Board board = new Board(0, Board.GameMode.SIMPLE) ;
		assertEquals(Board.GameMode.SIMPLE, board.getGameMode());
	}
	
	@Test
	public void testSelectGeneralGame() {
		Board board = new Board(0, Board.GameMode.GENERAL);
		assertEquals(Board.GameMode.GENERAL, board.getGameMode());
	}

	
	@Test
	public void testSelectSimpleGameNSize() {		
		for (int i = Board.MINIMUM_BOARD_SIZE; i <= Board.MAXIMUM_BOARD_SIZE; i++) {
			Board board = new Board(i, Board.GameMode.SIMPLE);
			assertEquals(i, board.getBoardSize());
			assertEquals(Board.GameMode.SIMPLE, board.getGameMode());
			
		}
		
	}
	
	@Test
	public void testSelectGeneralGameNSize() {
		for (int i = Board.MINIMUM_BOARD_SIZE; i <= Board.MAXIMUM_BOARD_SIZE; i++) {
			Board board = new Board(i, Board.GameMode.GENERAL);
			assertEquals(i, board.getBoardSize());
			assertEquals(Board.GameMode.GENERAL, board.getGameMode());
		}	
	}
	
	@Test
	public void testValidSimpleGameMove() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		assertEquals(true, board.makeMove(0, 0));
	}
	
	@Test
	public void testOutOfBoundsSimpleGameMove() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		assertEquals(false, board.makeMove(Board.MAXIMUM_BOARD_SIZE + 1, 0));
	}
	
	@Test
	public void testSimpleGameOccupiedMove() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		
		board.makeMove(0, 0);
		assertEquals(false, board.makeMove(0, 0));
	}
	
	@Test
	public void testSimpleGameTurnToggle() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		
		int prevTurn = board.getTurn();
		board.makeMove(0,0);
		
		assertEquals(false, board.getTurn() == prevTurn);
	}
	
	
	@Test
	public void testValidGeneralGameMove() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		assertEquals(true, board.makeMove(0, 0));
	}
	
	@Test
	public void testOutOfBoundsGeneralGameMove() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		assertEquals(false, board.makeMove(Board.MAXIMUM_BOARD_SIZE + 1, 0));
	}
	
	@Test
	public void testGeneralGameOccupiedMove() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		
		board.makeMove(0, 0);
		assertEquals(false, board.makeMove(0, 0));
	}
	
	@Test
	public void testGeneralGameTurnToggle() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		
		int prevTurn = board.getTurn();
		board.makeMove(0,0);
		
		assertEquals(false, board.getTurn() == prevTurn);
	}
	
	@Test
	public void testGeneralGameDoubleTurn() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
	
		board.makeMove(0, 0);
		
		board.getPlayer(1).setActivePiece(Piece.O);
		board.makeMove(1, 1);
		
		board.makeMove(2, 2);
		
		assertEquals(0, board.getTurn());
	}
	
	
	@Test
	public void testSimpleGameOverWin() {
		Board board = new Board(3, Board.GameMode.SIMPLE);
		board.makeMove(0, 0);
		
		board.getPlayer(1).setActivePiece(Piece.O);
		board.makeMove(1, 1);
		
		board.makeMove(2, 2);
		
		assertEquals(true, board.isGameOver());
		assertEquals(board.getPlayer(0), board.getWinner());
	}
	
	@Test
	public void testSimpleGameOverDraw() {
		Board board = new Board(3, Board.GameMode.SIMPLE);
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				board.makeMove(i, j);
			}
		}
		
		assertEquals(true, board.isGameOver());
		assertEquals(null, board.getWinner());
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
		
		assertEquals(true, board.isGameOver());
		assertEquals(board.getPlayer(1), board.getWinner());
		assertEquals(3, board.getPlayer(1).getScore());
		assertEquals(1, board.getPlayer(0).getScore());
	}
	
	@Test
	public void testGeneralGameOverDraw() {
		Board board = new Board(3, Board.GameMode.GENERAL);
		
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				board.makeMove(i, j);
			}
		}
		
		assertEquals(true, board.isGameOver());
		assertEquals(null, board.getWinner());

	}
	
	@Test
	public void testComputerMoveStart() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		
		boolean oneCellOccupied = false;
		board.setPlayerComputerStatus(board.getPlayer(0), true);
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				if (board.getCell(i, j) != Cell.EMPTY) {
					oneCellOccupied = true;
					break;
				}
			}
		}
		
		assertEquals(true, oneCellOccupied);
	}
	
	@Test
	public void testComputerMoveResponse() {
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL);
		
		board.makeMove(0, 0);
		board.setPlayerComputerStatus(board.getPlayer(1), true);
		
		int occupiedCells = 0;
		for (int i = 0; i < board.getBoardSize(); i++) {
			for (int j = 0; j < board.getBoardSize(); j++) {
				if (board.getCell(i, j) != Cell.EMPTY) {
					occupiedCells++;
				}
			}
		}
		
		assertEquals(2, occupiedCells);
	}
	
	@Test
	public void testAllComputerPlayersValidGame() {		
		Board board = new Board(validBoardSize, Board.GameMode.GENERAL) {
			private int occupiedCells = 0;
			
			@Override
			public void onMoveEvent() {
				occupiedCells++;
				if (this.isGameOver()) {
					assertEquals(true, this.isGameOver());
					assertEquals(validBoardSize * validBoardSize, occupiedCells);
				}
			}
		};
		board.setComputerMoveDelay(0);
		
		board.setPlayerComputerStatus(board.getPlayer(0), true);
		board.setPlayerComputerStatus(board.getPlayer(1), true);

	}
	
	@Test
	public void testSwitchPlayerType() {
		Board board = new Board(validBoardSize, Board.GameMode.SIMPLE);
		
		assertEquals(false, board.getPlayer(0).isComputer());
		assertEquals(false, board.getPlayer(1).isComputer());

		
		board.setPlayerComputerStatus(board.getPlayer(0), true);
		board.setPlayerComputerStatus(board.getPlayer(1), true);
		assertEquals(true, board.getPlayer(0).isComputer());
		assertEquals(true, board.getPlayer(1).isComputer());
		
		board.setPlayerComputerStatus(board.getPlayer(0), false);
		board.setPlayerComputerStatus(board.getPlayer(1), false);
		assertEquals(false, board.getPlayer(0).isComputer());
		assertEquals(false, board.getPlayer(1).isComputer());

	}
}
