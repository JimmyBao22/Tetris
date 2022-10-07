package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class TestBrain extends JTetris {
    private WeightBrain brain;

    public static void main(String[] args) {
        TestBrain self = new TestBrain();
        createGUI(self);
    }

    TestBrain() {
        brain = new WeightBrain(board.getWidth(), board.getHeight(), null);

//        brain = new LameBrain();

        run();
    }

    @Test
    void run() {
        timer = new javax.swing.Timer(DELAY, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick(brain.nextMove(board));
                Assertions.assertTrue(Board.Result.SUCCESS.equals(board.getLastResult()) || (Board.Result.PLACE.equals(board.getLastResult())));
            }
        });
    }

    @Test
    void testCountHoles() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 0, 1, 0, 0},
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

        board.updateBlocksFilledAndMaxHeight();
        Assertions.assertEquals(3, brain.countHoles(board));

        grid = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        board.updateBlocksFilledAndMaxHeight();
        Assertions.assertEquals(90, brain.countHoles(board));

        grid = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        board.updateBlocksFilledAndMaxHeight();
        Assertions.assertEquals(45, brain.countHoles(board));
    }

    @Test
    void testColumnHeightDifferences() {
        Piece.PieceType p = Piece.PieceType.T;

        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 0, 1, 0, 0},
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

        board.updateBlocksFilledAndMaxHeight();
        for (int i = 1; i < grid[0].length; i++) {
            int index1 = 0;
            int index2 = 0;
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    index1 = Math.max(index1, grid.length - 1 - j);
                }
                if (grid[j][i-1] == 1) {
                    index2 = Math.max(index2, grid.length - 1 - j);
                }
            }
            Assertions.assertEquals(Math.abs(index1 - index2), brain.columnHeight(board, i));
        }

        grid = new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        board.updateBlocksFilledAndMaxHeight();
        for (int i = 1; i < grid[0].length; i++) {
            int index1 = 0;
            int index2 = 0;
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    index1 = Math.max(index1, grid.length - 1 - j);
                }
                if (grid[j][i-1] == 1) {
                    index2 = Math.max(index2, grid.length - 1 - j);
                }
            }
            Assertions.assertEquals(Math.abs(index1 - index2), brain.columnHeight(board, i));
        }

        grid = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        };

        board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        board.updateBlocksFilledAndMaxHeight();
        for (int i = 1; i < grid[0].length; i++) {
            int index1 = 0;
            int index2 = 0;
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    index1 = Math.max(index1, grid.length - 1 - j);
                }
                if (grid[j][i-1] == 1) {
                    index2 = Math.max(index2, grid.length - 1 - j);
                }
            }
            Assertions.assertEquals(Math.abs(index1 - index2), brain.columnHeight(board, i));
        }
    }

    @Test
    void testIntervals() {
        Piece.PieceType p = Piece.PieceType.T;

        double[] temporary = new double[(int)(1e6)];

        int[][] grid = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 0, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 0, 1, 0, 0},
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

        brain.findIntervals(board, temporary, 0);
        Assertions.assertEquals(0, temporary[0]);
        Assertions.assertEquals(-4, temporary[1]);
        Assertions.assertEquals(-2, temporary[2]);
        Assertions.assertEquals(-1, temporary[3]);
        Assertions.assertEquals(-1, temporary[8]);

        grid = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, 1, 1, 1, 0, 0},
                {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                {0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
                {1, 0, 1, 0, 0, 1, 0, 0, 0, 0},
        };

        board = new TetrisBoard(grid[0].length, grid.length + 4);
        for (int i = 0; i < grid[0].length; i++) {
            for (int j = grid.length-1; j >= 0; j--) {
                if (grid[j][i] == 1) {
                    Piece piece = new TetrisPiece(p);
                    board.setOneBlock(piece, i, grid.length - 1 - j);
                }
            }
        }

        Arrays.fill(temporary, 0);
        brain.findIntervals(board, temporary, 0);
        Assertions.assertEquals(-3, temporary[0]);
        Assertions.assertEquals(-3, temporary[1]);
        Assertions.assertEquals(-5, temporary[2]);
        Assertions.assertEquals(-2, temporary[3]);
        Assertions.assertEquals(-2, temporary[4]);
    }
}
