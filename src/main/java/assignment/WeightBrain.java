package assignment;

import java.util.*;

public class WeightBrain implements Brain {
    private ArrayList<Board> options;
    private ArrayList<Board.Action> firstMoves;
    private int width, height;
    private double[] weights;
    private static final int NUM_PIECE_TYPES = Piece.PieceType.values().length;
    private static int numMetrics = 0;

    public WeightBrain(int width, int height, double[] weights) {
        // generate a random sequence of weights
        numMetrics = NUM_PIECE_TYPES + 1 + width + 2 + width - 1 + height;
        this.width = width;
        this.height = height;
        if (weights == null) {
            // randomly initialize weights
            this.weights = new double[numMetrics];
            for (int i = 0; i < numMetrics; i++) {
                this.weights[i] = Math.random();
            }
        } else {
            this.weights = weights;
        }
    }

    // Decide what the next move should be based on the state of the board.
    public Board.Action nextMove(Board currentBoard) {
        // Fill our options array with versions of the new Board
        options = new ArrayList<>();
        firstMoves = new ArrayList<>();
        enumerateOptions(currentBoard);

        double best = -Integer.MAX_VALUE;
        int bestIndex = 0;

        // Check all of the options and get the one with the highest score
        for (int i = 0; i < options.size(); i++) {
            double score = scoreBoard(options.get(i), currentBoard);
            if (score > best) {
                best = score;
                bestIndex = i;
            }
        }

        // We want to return the first move on the way to the best Board
        return firstMoves.get(bestIndex);
    }

    // Test all of the places we can put the current Piece.
    private void enumerateOptions(Board currentBoard) {
        // We can always drop our current Piece
        options.add(currentBoard.testMove(Board.Action.DROP));
        firstMoves.add(Board.Action.DROP);
        rotate(currentBoard);

        // Now we'll add all the places to the left we can DROP
        Board left = currentBoard.testMove(Board.Action.LEFT);
        while (left.getLastResult() == Board.Result.SUCCESS) {
            options.add(left.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.LEFT);
            rotate(left);
            left.move(Board.Action.LEFT);
        }

        // And then the same thing to the right
        Board right = currentBoard.testMove(Board.Action.RIGHT);
        while (right.getLastResult() == Board.Result.SUCCESS) {
            options.add(right.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.RIGHT);
            rotate(right);
            right.move(Board.Action.RIGHT);
        }
    }

    // check all three rotations and add them to the options list
    private void rotate(Board currentBoard) {
        Board rotate = currentBoard.testMove(Board.Action.CLOCKWISE);
        if (rotate.getLastResult() == Board.Result.SUCCESS) {
            options.add(rotate.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.CLOCKWISE);
        }
        rotate = currentBoard.testMove(Board.Action.COUNTERCLOCKWISE);
        if (rotate.getLastResult() == Board.Result.SUCCESS) {
            options.add(rotate.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.COUNTERCLOCKWISE);
        }

        rotate = rotate.testMove(Board.Action.COUNTERCLOCKWISE);
        if (rotate.getLastResult() == Board.Result.SUCCESS) {
            options.add(rotate.testMove(Board.Action.DROP));
            firstMoves.add(Board.Action.COUNTERCLOCKWISE);
        }
    }

    // calculates a score based on current metrics. Higher score indicates a better choice
    private double scoreBoard(Board newBoard, Board currentBoard) {
        double[] metrics = returnMetrics(newBoard, currentBoard, weights.length);
        double score = 0;
        for (int i = 0; i < weights.length; i++) {
            score += weights[i] * metrics[i];
        }
        return score;
    }

    // returns a series of metrics that can be used to calculate the scoreboard
    private double[] returnMetrics(Board newBoard, Board currentBoard, int n) {
        double[] metrics = new double[n];
        int i = 0;
        // set the current piece to 1, otherwise to 0. Matters more for matrix rather than vector multiplication
        for (int j = 0; j < NUM_PIECE_TYPES; j++) {
            metrics[i++] = 0;
        }
        metrics[i++] = -20 * (newBoard.getMaxHeight() - currentBoard.getMaxHeight());             // max height
        for (int j = 0; j < newBoard.getWidth(); j++) {
            metrics[i++] = -2 * (newBoard.getColumnHeight(j) - currentBoard.getColumnHeight(j));  // each column height
        }
        metrics[i++] = -300 * (countHoles(newBoard) - countHoles(currentBoard));                  // number of holes
        metrics[i++] = 50 * (newBoard.getRowsCleared() - currentBoard.getRowsCleared());          // number of rows cleared
        for (int j = 1; j < newBoard.getWidth(); j++) {
            metrics[i++] = -1 * (columnHeightDiff(newBoard, currentBoard, j) * columnHeightDiff (newBoard, currentBoard, j)
                    * columnHeightDiff (newBoard, currentBoard, j));  // differences in row height
        }

        findIntervals(newBoard, metrics, i);                                                      // number of open intervals

        return metrics;
    }

    // finds the number of open intervals within each row (including the beginnning and end)
    public void findIntervals(Board newBoard, double[] metrics, int i) {
        for (int j = 0; j < newBoard.getHeight(); j++) {
            // # of open intervals in each row
            int count = 0;
            boolean newSpace = false;
            for (int k = 0; k < newBoard.getWidth(); k++) {
                if (newBoard.getGrid(k, j) != null) {
                    // there exists a piece here
                    if (newSpace) {
                        count++;
                    }
                    newSpace = false;
                } else {
                    // there is no piece here
                    newSpace = true;
                }
            }
            if (newSpace) count++;
            metrics[i++] = -count;
        }
    }

    private int columnHeightDiff(Board newBoard, Board currentBoard, int j) {
        return columnHeight(newBoard, j) - columnHeight(currentBoard, j);
    }

    // counts the absolute difference between the column height at j and j-1. Further, finds the difference
        // of this value between the new board and the current board
    public int columnHeight(Board board, int j) {
        return Math.abs(board.getColumnHeight(j) - board.getColumnHeight(j-1));
    }

    // counts the number of holes in the board
    public int countHoles(Board board) {
        int countHoles = 0;
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = board.getColumnHeight(i) - 1; j >= 0; j--) {
                if (board.getGrid(i, j) == null) countHoles++;
            }
        }
        return countHoles;
    }
}