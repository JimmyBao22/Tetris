package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class CheckInstanceVariables {
    private Board board;
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final int TOP_SPACE = 4;
    private static final int NUM_PIECE_TYPES = Piece.PieceType.values().length;

    @Test
    TetrisBoard makeRandomBoard() {
        TetrisBoard newBoard;
        outer:
        while (true) {
            newBoard = new TetrisBoard(WIDTH, HEIGHT + TOP_SPACE);

            // place 5 random blocks
            for (int j = 0; j < 5; j++) {
                Piece piece = getRandomRotatedPiece();

                newBoard.nextPiece(piece, new Point(newBoard.getWidth() / 2 - piece.getWidth() / 2, HEIGHT));

                int move = (int) (Math.random() * 10) - 5;
                while (move < 0) {
                    newBoard.move(Board.Action.RIGHT);
                    Assertions.assertEquals(newBoard.getLastAction(), Board.Action.RIGHT);
                    move++;
                }

                while (move > 0) {
                    newBoard.move(Board.Action.LEFT);
                    Assertions.assertEquals(newBoard.getLastAction(), Board.Action.LEFT);
                    move--;
                }

                if (newBoard.dropHeight(piece, (int) (newBoard.getCurrentPiecePosition().getX())) + piece.getHeight() >= HEIGHT) {
                    continue outer;
                }
                newBoard.move(Board.Action.DROP);
                Assertions.assertEquals(newBoard.getLastAction(), Board.Action.DROP);
            }

            break;
        }
        return newBoard;
    }
    @BeforeEach
    void constructRandomBoard() {
        this.board = makeRandomBoard();
    }

    private Piece getRandomPiece() {
        int pieceIndex = (int) (Math.random() * NUM_PIECE_TYPES);
        return new TetrisPiece(Piece.PieceType.values()[pieceIndex]);
    }

    private Piece getRandomRotatedPiece() {
        Piece p = getRandomPiece();
        int numRotations = (int)(Math.random() * 4);
        for (int i = 0; i < numRotations; i++) {
            p = p.clockwisePiece();
        }
        return p;
    }

    @RepeatedTest(10000)
    void testRotation() {
        Piece piece = getRandomPiece();

        int rotationIndex = 0;
        int moveClockwise = (int) (Math.random() * 1000);
        for (int k = 0; k < moveClockwise; k++) {
            piece = piece.clockwisePiece();
            rotationIndex++;
        }
        rotationIndex %= 4;

        Assertions.assertEquals(rotationIndex, piece.getRotationIndex());

        int moveCounterClockwise = (int) (Math.random() * 1000);
        for (int k = 0; k < moveCounterClockwise; k++) {
            piece = piece.counterclockwisePiece();
            rotationIndex--;
        }
        rotationIndex %= 4;
        rotationIndex += 4;
        rotationIndex %= 4;
        Assertions.assertEquals(rotationIndex, piece.getRotationIndex());
    }

    @RepeatedTest(10000)
    void testMaxHeight() {
        int maxHeight = 0;
        for (int j = 0; j < board.getWidth(); j++) {
            maxHeight = Math.max(maxHeight, board.getColumnHeight(j));
        }
        Assertions.assertTrue(maxHeight == board.getMaxHeight());
    }

    @RepeatedTest(10000)
    void testColumnHeight() {
        for (int j = 0; j < board.getWidth(); j++) {
            int columnHeight = -1;
            for (int k = 0; k < board.getHeight(); k++) {
                if (board.getGrid(j, k) != null) {
                    columnHeight = k;
                }
            }
            Assertions.assertTrue((columnHeight + 1) == board.getColumnHeight(j));
        }
    }

    @RepeatedTest(1000)
    void testRowWidth() {
        for (int j = 0; j < board.getHeight(); j++) {
            int rowBlocks = 0;
            for (int k = 0; k < board.getWidth(); k++) {
                if (board.getGrid(k ,j) != null) rowBlocks++;
            }
            Assertions.assertTrue(rowBlocks == board.getRowWidth(j));

            // if rowblocks is width, the row should've cleared
            Assertions.assertTrue(rowBlocks != WIDTH);
        }
    }

    @Test
    void testNoPiece() {
        Board board = new TetrisBoard(10, 10);
        Assertions.assertEquals(board.move(null), Board.Result.NO_PIECE);
    }
}
