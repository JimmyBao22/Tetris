package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class RotationMovementCheck {

    @RepeatedTest(10000)
    void testMovements() {

        int width = 10;
        int height = 10;

        for (Piece.PieceType p : Piece.PieceType.values()) {
            Piece piece = new TetrisPiece(p);
            // x times one way = 4 - x times the other way
            Assertions.assertTrue(piece.clockwisePiece().clockwisePiece().clockwisePiece().equals(piece.counterclockwisePiece()));
            Assertions.assertTrue(piece.clockwisePiece().clockwisePiece().equals(piece.counterclockwisePiece().counterclockwisePiece()));
            Assertions.assertTrue(piece.clockwisePiece().equals(piece.counterclockwisePiece().counterclockwisePiece().counterclockwisePiece()));
            // one way each should equal original
            Assertions.assertTrue(piece.clockwisePiece().counterclockwisePiece().equals(piece));
            Assertions.assertTrue(piece.counterclockwisePiece().clockwisePiece().equals(piece));

            // four times one way should equal original
            Assertions.assertTrue(piece.clockwisePiece().clockwisePiece().clockwisePiece().clockwisePiece().equals(piece));
            Assertions.assertTrue(piece.counterclockwisePiece().counterclockwisePiece().counterclockwisePiece().counterclockwisePiece().equals(piece));

            // check skirt
            int[] skirt = piece.getSkirt();
            for (int i = 0 ; i < piece.getWidth(); i++) {
                outer: for (int j = 0; j < piece.getHeight(); j++) {
                    for (Point point : piece.getBody()) {
                        if (point.getX() == i && point.getY() == j) {
                            // found a piece here
                            Assertions.assertTrue(skirt[i] == j);
                            break outer;
                        }
                    }
                }
            }

            // moving left and right = original
            Board board = new TetrisBoard(width, height);
            board.nextPiece(piece, new Point(width/2,height/2));
            board.move(Board.Action.LEFT);
            board.move(Board.Action.RIGHT);
            Board board2 = new TetrisBoard(width, height);
            board2.nextPiece(piece, new Point(width/2, height/2));
            Assertions.assertTrue(board.equals(board2));

            // keep moving down = drop
            do {
                board.move(Board.Action.DOWN);
            } while (board.getLastResult() != Board.Result.PLACE);
            board2.move(Board.Action.DROP);
            Assertions.assertTrue(board.equals(board2));
        }
    }
}