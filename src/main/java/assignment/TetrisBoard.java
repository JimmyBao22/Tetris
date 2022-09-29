package assignment;

import java.awt.*;
import java.util.Arrays;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {

    // JTetris will use this constructor

    private int width, height, maxHeight, rowsCleared;
    private Piece[][] board;
    private int[] blocksFilledPerRow, blocksFilledPerColumn;
    private Action lastAction;
    private Result lastResult;
    private Piece currentPiece;
    private Point currentPosition;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxHeight = 0;
        this.rowsCleared = 0;
        // board[i][j] is true if it is filled. Otherwise, it is false
        this.board = new Piece[width][height];
        this.blocksFilledPerRow = new int[height];
        this.blocksFilledPerColumn = new int[width];
    }

    // Create this board by cloning an old one
    public TetrisBoard(TetrisBoard oldBoard) {
        this.width = oldBoard.getWidth();
        this.height = oldBoard.getHeight();
        this.maxHeight = oldBoard.getMaxHeight();
        this.rowsCleared = oldBoard.getRowsCleared();
        this.board = new Piece[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                this.board[i][j] = oldBoard.board[i][j];
            }
        }
        this.blocksFilledPerRow = new int[this.height];
        for (int i = 0; i < this.height; i++) {
            this.blocksFilledPerRow[i] = oldBoard.getRowWidth(i);
        }
        this.blocksFilledPerColumn = new int[this.width];
        for (int i = 0; i < this.width; i++) {
            this.blocksFilledPerColumn[i] = oldBoard.getColumnHeight(i);
        }
    }

    @Override
    public Result move(Action act) {
        // TODO implement and set lastResult to the returned result

        lastAction = act;
        return Result.NO_PIECE;
    }

    @Override
    public Board testMove(Action act) {
        Board newBoard = new TetrisBoard(this);
        newBoard.move(act);
        return newBoard;
    }

    @Override
    public Piece getCurrentPiece() { return this.currentPiece; }

    @Override
    public Point getCurrentPiecePosition() {
        return this.currentPosition;
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        Point[] body = p.getBody();
        // make sure the piece is in bounds and does not intersect
        for (int i = 0; i < body.length; i++) {
            int currentPointX = (int)(spawnPosition.getX() + body[i].getX());
            int currentPointY = (int)(spawnPosition.getY() + body[i].getY());

            // point is out of bounds
            if (currentPointX >= width || currentPointX < 0 || currentPointY >= height || currentPointY < 0) {
                throw new IllegalArgumentException("Piece is out of bounds");
            }

            // point is already occupied
            if (board[currentPointX][currentPointY] != null) {
                throw new IllegalArgumentException("Piece intersects with existing piece");
            }
        }

        // place the piece if all preconditions are passed
        for (int i = 0; i < body.length; i++) {
            int x = (int) (spawnPosition.getX() + body[i].getX());
            int y = (int) (spawnPosition.getY() + body[i].getY());
            this.board[x][y] = p;
        }
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris boards.
        if(!(other instanceof TetrisBoard)) return false;
        TetrisBoard otherBoard = (TetrisBoard) other;

        // TODO make this faster?
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!this.board[i][j].equals(otherBoard.board[i][j])) {
                    return false;
                }
            }
        }

        // ensure they have the same current piece at the same location
        return (this.getCurrentPiece().equals(otherBoard.getCurrentPiece())) && (this.getCurrentPiecePosition().equals(otherBoard.getCurrentPiecePosition()));
    }

    @Override
    public Result getLastResult() { return this.lastResult; }

    @Override
    public Action getLastAction() { return this.lastAction; }

    @Override
    public int getRowsCleared() { return this.rowsCleared; }

    @Override
    public int getWidth() { return this.width; }

    @Override
    public int getHeight() { return this.height; }

    @Override
    public int getMaxHeight() { return this.maxHeight; }

    @Override
    public int dropHeight(Piece piece, int x) {
        int[] skirt = piece.getSkirt();
        // TODO asked about x on piazza
        int height = 0;
        for (int i = 0; i < skirt.length; i++) {
            if (skirt[i] != Integer.MAX_VALUE) {
                height = Math.max(getColumnHeight(x + i) - skirt[i], height);
            }
        }
        return height;
    }

    @Override
    public int getColumnHeight(int x) {
        return this.blocksFilledPerColumn[x];
    }

    @Override
    public int getRowWidth(int y) {
        return this.blocksFilledPerRow[y];
    }

    @Override
    public Piece.PieceType getGrid(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight() || this.board[x][y] == null || this.board[x][y].equals(currentPiece)) {
            return null;
        } else {
            return this.board[x][y].getType();
        }
    }
}