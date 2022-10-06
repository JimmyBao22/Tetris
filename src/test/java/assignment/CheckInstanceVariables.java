package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class CheckInstanceVariables {

    @Test
    public static void main(String[] args) {
        int width = 10;
        int height = 10;
        int topSpace = 4;
        int numIterations = 1000;
        int numPieceTypes = Piece.PieceType.values().length;

        for (int i = 0; i < numIterations; i++) {
            Board board = new TetrisBoard(width, height + topSpace);
            // place 5 random blocks
            for (int j = 0; j < 5; j++) {
                int pieceIndex = (int)(Math.random() * numPieceTypes);
                Piece piece = null;
                for (Piece.PieceType p : Piece.PieceType.values()) {
                    if (p.ordinal() == pieceIndex) {
                        // add this current piece
                        piece = new TetrisPiece(p);
                    }
                }

                int rotationIndex = 0;
                int moveClockwise = (int)(Math.random() * 4);
                for (int k = 0; k < moveClockwise; k++) {
                    piece = piece.clockwisePiece();
                    rotationIndex++;
                }
                // TODO error sometimes v
                Assertions.assertTrue(rotationIndex == piece.getRotationIndex());

                // put it randomly on the board
                board.nextPiece(piece, new Point(board.getWidth() / 2 - piece.getWidth() / 2, height));

                Assertions.assertTrue(board.getCurrentPiece().equals(piece));

                int moveLeft = (int)(Math.random() * 6);
                for (int k = 0; k < moveLeft; k++) {
                    board.move(Board.Action.LEFT);
                }

                int moveRight = (int)(Math.random() * 4);
                for (int k = 0; k < moveRight; k++) {
                    board.move(Board.Action.RIGHT);
                }

                board.move(Board.Action.DROP);
            }

            // check instance variables

            int maxHeight = 0;
            for (int j = 0; j < board.getWidth(); j++) {
                maxHeight = Math.max(maxHeight, board.getColumnHeight(j));
            }
            Assertions.assertTrue(maxHeight == board.getMaxHeight());

            for (int j = 0; j < board.getWidth(); j++) {
                int columnHeight = -1;
                for (int k = 0; k < board.getHeight(); k++) {
                    if (board.getGrid(j, k) != null) columnHeight = k;
                }
                System.out.println(columnHeight + " " + board.getColumnHeight(j));
                // TODO error sometimes v
                Assertions.assertTrue((columnHeight + 1) == board.getColumnHeight(j));
            }

            for (int j = 0; j < board.getHeight(); j++) {
                int rowBlocks = 0;
                for (int k = 0; k < board.getWidth(); k++) {
                    if (board.getGrid(k ,j) != null) rowBlocks++;
                }
                Assertions.assertTrue(rowBlocks == board.getRowWidth(j));

                // if rowblocks is width, the row should've cleared
                Assertions.assertTrue(rowBlocks != width);
            }
        }
    }
}
