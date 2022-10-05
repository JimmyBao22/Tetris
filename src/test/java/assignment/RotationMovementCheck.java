package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RotationMovementCheck {

    @Test
    public static void main(String[] args) {
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
        }

        // moving left and right = original
    }
}