package assignment;

import java.awt.*;

public class InvalidInputs {

    public static void main(String[] args) {

    }

    private static void nullNextPiece() {
        Board board = new TetrisBoard(10, 10);
//        board.nextPiece(null, new Point(5, 5));
//        board.nextPiece(new TetrisPiece(Piece.PieceType.T), null);
//        board.nextPiece(new TetrisPiece(Piece.PieceType.T), new Point(-10, 5));

        // test that it states that there already exists a piece
        board.nextPiece(new TetrisPiece(Piece.PieceType.T), new Point(0, 1));
        board.move(Board.Action.DROP);
        board.nextPiece(new TetrisPiece(Piece.PieceType.T), new Point(0, 0));
    }

    private static void negativeBoardCreatioin() {
//        Board board = new TetrisBoard(-1, 2);
        Board board = new TetrisBoard(2, -1);
    }

    private static void nullTestMove() {
        Board board = new TetrisBoard(10, 10);
        board.testMove(null);
    }
}
