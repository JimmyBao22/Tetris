package assignment;

import java.awt.*;

/**
 * Represents a Tetris board -- essentially a 2D grid of piece types (or nulls). Supports
 * tetris pieces and row clearing.  Does not do any drawing or have any idea of
 * pixels. Instead, just represents the abstract 2D board.
 */
public final class TetrisBoard implements Board {

    // JTetris will use this constructor

    private int width, height, maxHeight, rowsCleared;
    private boolean[][] board;
    private int[] blocksFilledPerRow, blocksFilledPerColumn;
    private Action lastAction;
    private Result lastResult;
    private Piece currentPiece;
    private Point currentPosition;

    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        maxHeight = 0;
        rowsCleared = 0;
        // board[i][j] is true if it is filled. Otherwise, it is false
        board = new boolean[height][width];
        blocksFilledPerRow = new int[height];
        blocksFilledPerColumn = new int[width];
    }

    @Override
    public Result move(Action act) {
        // TODO implement and set lastResult to the returned result

        lastAction = act;
        return Result.NO_PIECE;
    }

    @Override
    public Board testMove(Action act) { return null; }

    @Override
    public Piece getCurrentPiece() { return this.currentPiece; }

    @Override
    public Point getCurrentPiecePosition() {
        return this.currentPosition;
    }

    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        Point[] body = p.getBody();
        for (int i = 0; i < body.length; i++) {
            int currentPointX = (int)(spawnPosition.getX() + body[i].getX());
            int currentPointY = (int)(spawnPosition.getY() + body[i].getY());

            // point is out of bounds
            if (currentPointX >= width || currentPointX < 0 || currentPointY >= height || currentPointY < 0) {
                throw new IllegalArgumentException();
            }

            // point is already occupied
            if (board[currentPointX][currentPointY]) {
                throw new IllegalArgumentException();
            }
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
                if (this.board[i][j] != otherBoard.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
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
        // TODO try to increase speed
        int[] skirt = piece.getSkirt();
        // TODO asked about x on piazza
        int height = 0;
        for (int i = 0; i < skirt.length; i++) {
            if (skirt[i] != Integer.MAX_VALUE) {
                height = Math.max(getColumnHeight(x + i) + skirt[i], height);
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
    public Piece.PieceType getGrid(int x, int y) { return null; }
}