package assignment;

import com.sun.net.httpserver.Authenticator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.awt.*;

public class TestCustomBoards {

    @RepeatedTest(10000)
    void testPiecePlaced() {
        int height = 10;
        int width = 10;

        Board board = new TetrisBoard(width, height + 4);
        while (board.getMaxHeight() < 10) {
            int index = (int)(Math.random() * Piece.PieceType.values().length);
            Piece piece = new TetrisPiece(Piece.PieceType.values()[index]);

            if (piece.getHeight() + board.getMaxHeight() >= height) break;

            board.nextPiece(piece, new Point(board.getWidth() / 2 - piece.getWidth() / 2, height));
            board.move(Board.Action.DROP);
            Assertions.assertEquals(Board.Result.PLACE, board.getLastResult());
        }
    }

    @Test
    void testDropUnderCave() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        };

        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        Piece piece = new TetrisPiece(Piece.PieceType.SQUARE);

        board.nextPiece(piece, new Point(7, grid.length));
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.DOWN);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);
        board.move(Board.Action.LEFT);

        board.updateBlocksFilledAndMaxHeight();

//        System.out.println(xCoordinate);
//        System.out.println(piece.getType());
//        board.printBoard();

        // check the drop height
        int highest = 0;
        for (Point point : piece.getBody()) {
            int x = (int)(point.getX() + board.getCurrentPiecePosition().getX());
            for (int j = (int)board.getCurrentPiecePosition().getY(); j >= 0; j--) {
                if (board.getGrid(x, j) != null) {
                    highest = Math.max(highest, j + 1 - (int)point.getY());
                    break;
                }
            }
        }

        Assertions.assertEquals(highest, board.findDropHeight(piece, (int)board.getCurrentPiecePosition().getX()));
    }

    @RepeatedTest(10000)
    void testDropHeight() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = new int[10][10];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (Math.random() < 0.5) {
                    grid[i][j] = 1;
                }
            }
        }

        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        int index = (int)(Math.random() * Piece.PieceType.values().length);
        Piece piece = new TetrisPiece(Piece.PieceType.values()[index]);

        int xCoordinate = (int)(Math.random() * (10 - piece.getWidth()));
        board.nextPiece(piece, new Point(xCoordinate, grid.length));

        board.updateBlocksFilledAndMaxHeight();

//        System.out.println(xCoordinate);
//        System.out.println(piece.getType());
//        board.printBoard();

        // check the drop height
        int highest = 0;
        for (Point point : piece.getBody()) {
            int x = (int)(point.getX() + xCoordinate);
            for (int j = grid.length - 1; j >= 0; j--) {
                if (board.getGrid(x, j) != null) {
                    highest = Math.max(highest, j + 1 - (int)point.getY());
                    break;
                }
            }
        }

        Assertions.assertEquals(highest, board.dropHeight(piece, xCoordinate));
        Assertions.assertEquals(highest, board.findDropHeight(piece, xCoordinate));
    }

    @Test
    void testRowClearing() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0, 1, 1, 1, 1, 1},
        };

        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        Piece piece = new TetrisPiece(Piece.PieceType.STICK);
        piece = piece.clockwisePiece();

        board.nextPiece(piece, new Point(4 - 2, 5));
        board.move(Board.Action.DROP);

        board.printBoard();
        Assertions.assertEquals(4, board.getRowsCleared());
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Assertions.assertEquals(null, board.getGrid(j, i));
            }
        }
    }

    @Test
    void testGridWithBlocks() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 0, 0, 0, 0},
                {1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
        };

        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        for (int i = 0; i < board.getWidth(); i++) {
            Piece piece = new TetrisPiece(Piece.PieceType.STICK);
            piece = piece.clockwisePiece();

            board.nextPiece(piece, new Point(i - 2, 10));
            board.move(Board.Action.DROP);

            // board.printBoard();

            if (i < 3 ) {
                Assertions.assertEquals(piece.getType(), board.getGrid(i, 2));
            } else if (i < 6) {
                Assertions.assertEquals(piece.getType(), board.getGrid(i, 3));
            } else {
                Assertions.assertEquals(piece.getType(), board.getGrid(i, 1));
            }
        }
    }

    @RepeatedTest(10000)
    void testGetGrid() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = new int[10][10];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (Math.random() < 0.5) {
                    grid[i][j] = 1;
                }
            }
        }

        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, j);
                }
            }
        }

        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Assertions.assertEquals(board.getGrid(i, j), p);
                } else {
                    Assertions.assertEquals(board.getGrid(i, j), null);
                }
            }
        }

        // out of bounds locations
        Assertions.assertEquals(board.getGrid(-1, 5), null);
        Assertions.assertEquals(board.getGrid(5, -1), null);
    }

    @RepeatedTest(10000)
    void testUpdates() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = new int[10][10];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (Math.random() < 0.5) {
                    grid[i][j] = 1;
                }
            }
        }

        int maxHeight = 0;
        int[] blocksFilledPerRow = new int[grid.length];
        int[] blocksFilledPerColumn = new int[grid[0].length];
        TetrisBoard board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    maxHeight = Math.max(maxHeight, (grid.length - 1 - j) + 1);
                    blocksFilledPerRow[grid.length - 1 - j]++;
                    blocksFilledPerColumn[i] = Math.max(blocksFilledPerColumn[i], (grid.length - 1 - j) + 1);
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        board.updateBlocksFilledAndMaxHeight();

        Assertions.assertEquals(maxHeight, board.getMaxHeight());
        for (int i = 0; i < blocksFilledPerRow.length; i++) {
            Assertions.assertEquals(blocksFilledPerRow[i], board.getRowWidth(i));
        }
        for (int i = 0; i < blocksFilledPerColumn.length; i++) {
            Assertions.assertEquals(blocksFilledPerColumn[i], board.getColumnHeight(i));
        }
    }
}