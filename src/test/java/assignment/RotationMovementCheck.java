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

    @Test
    void testSpecificRotationL() {
        int width = 2;
        int height = 2;
        Point[] before = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) };
        /*

            x - -   ->   - x x  ->  - - -   ->  - x -
            x x x   ->   - x -  ->  x x x   ->  - x -
            - - -   ->   - x -  ->  - - x   ->  x x -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2,2)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(2,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    @Test
    void testRotationL() {
        int width = 2;
        int height = 2;
        Point[] before = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) };
        /*

            x - -   ->   - x x  ->  - - -   ->  - x -
            x x x   ->   - x -  ->  x x x   ->  - x -
            - - -   ->   - x -  ->  - - x   ->  x x -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2,2)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(2,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    @Test
    void testSkirtL() {
        Piece piece = new TetrisPiece(Piece.PieceType.LEFT_L);
        int[] skirt = new int[]{1, 1, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{Integer.MAX_VALUE, 0, 2};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{1, 1, 0};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{0, 0, Integer.MAX_VALUE};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }
    }

    @Test
    void testSpecificRotationDog() {
        int width = 2;
        int height = 2;
        Point[] before = new Point[] { new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2) };
        /*

            - x x   ->   - x -  ->  - - -   ->  x - -
            x x -   ->   - x x  ->  - x x   ->  x x -
            - - -   ->   - - x  ->  x x -   ->  - x -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(1, 2), new Point(1, 1), new Point(2, 1), new Point(2,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(2, 1), new Point(1, 1), new Point(1, 0), new Point(0,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(1, 1), new Point(0, 1), new Point(0,2)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    @Test
    void testSkirtDog() {
        Piece piece = new TetrisPiece(Piece.PieceType.RIGHT_DOG);
        int[] skirt = new int[]{1, 1, 2};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{Integer.MAX_VALUE, 1, 0};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{0, 0, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{1, 0, Integer.MAX_VALUE};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }
    }

    @Test
    void testSpecificRotationT() {
        int width = 2;
        int height = 2;
        Point[] before = new Point[] { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) };
        /*

            - x -   ->   - x -  ->  - - -   ->  - x -
            x x x   ->   - x x  ->  x x x   ->  x x -
            - - -   ->   - x -  ->  - x -   ->  - x -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(1, 2), new Point(1, 1), new Point(1, 0), new Point(2,1)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(2, 1), new Point(1, 1), new Point(0, 1), new Point(1,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(0,1)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    @Test
    void testSkirtT() {
        Piece piece = new TetrisPiece(Piece.PieceType.T);
        int[] skirt = new int[]{1, 1, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{Integer.MAX_VALUE, 0, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{1, 0, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{1, 0, Integer.MAX_VALUE};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }
    }

    @Test
    void testSpecificRotationStick() {
        int width = 3;
        int height = 3;
        Point[] before = new Point[] { new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2) };
        /*

            - - - -  ->   - - x -  ->  - - - -  ->  - x - -
            x x x x  ->   - - x -  ->  - - - -  ->  - x - -
            - - - -  ->   - - x -  ->  x x x x  ->  - x - -
            - - - -  ->   - - x -  ->  - - - -  ->  - x - -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(2, 3), new Point(2, 2), new Point(2, 1), new Point(2,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(3, 1), new Point(2, 1), new Point(1, 1), new Point(0,1)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1,3)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    @Test
    void testSkirtStick() {
        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        int[] skirt = new int[]{2, 2, 2, 2};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, 0, Integer.MAX_VALUE};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{1, 1, 1, 1};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }

        piece = piece.clockwisePiece();
        skirt = new int[]{Integer.MAX_VALUE, 0, Integer.MAX_VALUE, Integer.MAX_VALUE};
        for (int i = 0 ; i < skirt.length; i++) {
            Assertions.assertEquals(skirt[i], piece.getSkirt()[i]);
        }
    }

    @Test
    void testSpecificRotationRandom() {
        int width = 2;
        int height = 2;
        Point[] before = new Point[] { new Point(0, 1), new Point(0, 2), new Point(1, 0), new Point(2, 2) };
        /*

            x - x   ->   - x x  ->  - x -   ->  x - -
            x - -   ->   x - -  ->  - - x   ->  - - x
            - x -   ->   - - x  ->  x - x   ->  x x -

         */

        Point[] after = rotateClockwise(before, width, height);
        Point[] supposedAfter = new Point[] { new Point(1, 2), new Point(2, 2), new Point(0, 1), new Point(2,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(2, 1), new Point(2, 0), new Point(1, 2), new Point(0,0)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }

        copyArray(before, after);
        after = rotateClockwise(before, width, height);
        supposedAfter = new Point[] { new Point(1, 0), new Point(0, 0), new Point(2, 1), new Point(0,2)};
        for (int i = 0 ; i < 4; i++) {
            Assertions.assertTrue(after[i].equals(supposedAfter[i]));
        }
    }

    private void copyArray(Point[] a, Point[] b) {
        for (int i = 0; i < b.length; i++) {
            a[i] = new Point((int)b[i].getX(), (int)b[i].getY());
        }
    }

    private Point[] rotateClockwise(Point[] points, int width, int height) {
        int pointsLength = points.length;
        Point[] rotatedPoints = new Point[pointsLength];

        // for each point, translate it to the origin, rotate it, then translate it back
        for (int i = 0; i < pointsLength; i++) {
            double translatedY = (points[i].getY() - width / 2.0);
            double translatedX = (points[i].getX() - height / 2.0);
            rotatedPoints[i] = new Point((int)(translatedY + width / 2.0), (int)(-translatedX + height / 2.0));
        }
        return rotatedPoints;
    }
}