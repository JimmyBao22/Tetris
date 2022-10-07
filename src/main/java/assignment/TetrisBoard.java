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

    // Create an empty TetrisBoard
    public TetrisBoard(int width, int height) {
        this.width = width;
        this.height = height;
        this.maxHeight = 0;
        this.rowsCleared = 0;
        this.board = new Piece[width][height];
        this.blocksFilledPerRow = new int[height];
        this.blocksFilledPerColumn = new int[width];
    }

    // Create a board by cloning an old one
    public TetrisBoard(TetrisBoard oldBoard) {
        // copy all instance variables
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

        this.currentPiece = oldBoard.getCurrentPiece();
        this.currentPosition = oldBoard.getCurrentPiecePosition();

        this.lastResult = oldBoard.getLastResult();
        this.lastAction = oldBoard.getLastAction();
    }

    // Given an action, update the board and set the result appropriately
    @Override
    public Result move(Action act) {
        if (currentPiece == null) {
            lastResult = Result.NO_PIECE;
            return lastResult;
        }

        Point[] body = currentPiece.getBody();
        Point newPosition;
        switch (act) {
            case LEFT:
                newPosition = new Point((int)(currentPosition.getX()) - 1, (int)(currentPosition.getY()));
                // will set lastResult internally
                movePieceToNewPosition(body, newPosition);
                break;
            case RIGHT:
                newPosition = new Point((int)(currentPosition.getX()) + 1, (int)(currentPosition.getY()));
                // will set lastResult internally
                movePieceToNewPosition(body, newPosition);
                break;
            case DOWN:
                newPosition = new Point((int)(currentPosition.getX()), (int)(currentPosition.getY()) - 1);
                // will set lastResult internally
                movePieceToNewPosition(body, newPosition);
                checkIfPiecePlaced(body);
                break;
            case DROP:
                int height = findDropHeight(currentPiece, (int)(currentPosition.getX()));
                newPosition = new Point((int)(currentPosition.getX()), height);
                // will set lastResult internally
                movePieceToNewPosition(body, newPosition);
                checkIfPiecePlaced(body);
                break;
            case CLOCKWISE:
                // will set lastResult internally
                rotateCurrentPiece(true);
                break;
            case COUNTERCLOCKWISE:
                // will set lastResult internally
                rotateCurrentPiece(false);
                break;
            case HOLD:
                lastResult = Result.SUCCESS;
                break;
            case NOTHING:
                lastResult = Result.SUCCESS;
                break;
        }

        lastAction = act;
        return lastResult;
    }

    // try to rotate the current piece, set last result to success or out of bounds
    private void rotateCurrentPiece(boolean clockwise) {
        // store which wallkicks to use depending on piece type and rotation direction
        Point[][] wallKickLookup;
        if (currentPiece.getType() == Piece.PieceType.STICK) {
            wallKickLookup = clockwise ? Piece.I_CLOCKWISE_WALL_KICKS : Piece.I_COUNTERCLOCKWISE_WALL_KICKS;
        } else {
            wallKickLookup = clockwise ? Piece.NORMAL_CLOCKWISE_WALL_KICKS : Piece.NORMAL_COUNTERCLOCKWISE_WALL_KICKS;
        }

        // remove the old body
        setPiece(null, currentPiece.getBody(), currentPosition);
        int sourceRotationIndex = currentPiece.getRotationIndex();

        // store the rotated piece
        Piece rotatedPiece = clockwise ? currentPiece.clockwisePiece() : currentPiece.counterclockwisePiece();

        // try all the wallkick displacements
        for (Point potentialMovement : wallKickLookup[sourceRotationIndex]) {
            int newX = (int) (currentPosition.getX() + potentialMovement.getX());
            int newY = (int) (currentPosition.getY() + potentialMovement.getY());

            Point newPosition = new Point(newX, newY);
            if (checkPiece(rotatedPiece, newPosition) == 0) {
                // we found the rotation + translation that doesn't collide with anything
                currentPiece = rotatedPiece;
                currentPosition = newPosition;
                setPiece(currentPiece, currentPiece.getBody(), currentPosition);
                lastResult = Result.SUCCESS;
                return;
            }
        }

        // we checked all rotation + translations, none work
        // put the old body back where it was and report the issue
        setPiece(currentPiece, currentPiece.getBody(), currentPosition);
        lastResult = Result.OUT_BOUNDS;
    }

    // after the current piece is placed, check the rows it is a part of and clear them as needed
    private void clearRows() {
        // check each row of the piece
        for (int i = currentPiece.getHeight() - 1; i >= 0; i--) {
            int y = (int) (currentPosition.getY()) + i;
            // if this row is full
            if (y >= 0 && (y < getHeight()) && (getRowWidth(y) == getWidth())) {
                // replace every cell with the contents of the one above
                for (int x = 0; x < getWidth(); x++) {
                    for (int row = y; row < getHeight() - 1; row++) {
                        board[x][row] = board[x][row + 1];
                    }

                    // top row is empty now
                    board[x][getHeight() - 1] = null;

                    // we removed 1 block from each column
                    blocksFilledPerColumn[x]--;
                }

                // shift blocks per row counts down by one row
                for (int row = y; row < getHeight() - 1; row++) {
                    blocksFilledPerRow[row] = blocksFilledPerRow[row + 1];
                }

                // no blocks in the top row
                blocksFilledPerRow[getHeight() - 1] = 0;

                rowsCleared++;
            }
        }
    }

    // check if the piece can be placed
    private void checkIfPiecePlaced(Point[] body) {
        // checks if the drop height of the piece already equals the current piece's location. If it does,
        // that means the piece is placed.
        if (findDropHeight(currentPiece, (int)(currentPosition.getX())) == (int)(currentPosition.getY())) {
            clearRows();
            currentPosition = null;
            currentPiece = null;
            updateBlocksFilledAndMaxHeight();
            lastResult = Result.PLACE;
        }
    }

    // updates the instance variables for max height and blocks filled per column/row
    private void updateBlocksFilledAndMaxHeight() {
        maxHeight = 0;
        Arrays.fill(blocksFilledPerRow, 0);
        for (int x = 0; x < getWidth(); x++) {
            blocksFilledPerColumn[x] = 0;
            for (int y = 0; y < getHeight(); y++) {
                if (getGrid(x, y) != null) {
                    blocksFilledPerColumn[x] = (y + 1);
                    blocksFilledPerRow[y]++;
                }

                if (blocksFilledPerColumn[x] > maxHeight) {
                    maxHeight = blocksFilledPerColumn[x];
                }
            }
        }
    }

    // moves the piece to the new position if applicable, and sets the result
    private void movePieceToNewPosition(Point[] body, Point newPosition) {
        int result = checkPiece(currentPiece, newPosition);
        if (result == 0) {
            // can place the piece in this new position

            // remove the piece from the current position
            setPiece(null, body, currentPosition);

            // add the piece to the new position
            setPiece(currentPiece, body, newPosition);
            currentPosition = newPosition;

            lastResult = Result.SUCCESS;
        }
        else {
            lastResult = Result.OUT_BOUNDS;
        }
    }

    // return a new board reflecting the current board if an action were to be applied
    @Override
    public Board testMove(Action act) {
        if (act == null) {
            return null;
        }
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

    // Put a new piece on the board
    @Override
    public void nextPiece(Piece p, Point spawnPosition) {
        if (p == null || spawnPosition == null) {
            throw new IllegalArgumentException("Piece does not exist on board");
        }

        Point[] body = p.getBody();
        // throw corresponding error based on result of checkPiece function
        int result = checkPiece(p, spawnPosition);
        if (result == 1) {
            throw new IllegalArgumentException("Piece is out of bounds");
        }
        if (result == 2) {
            throw new IllegalArgumentException("Piece intersects with existing piece");
        }

        // place the piece if all preconditions are passed
        setPiece(p, body, spawnPosition);

        this.currentPiece = p;
        this.currentPosition = spawnPosition;
    }

    // adds a piece p to the given position. If you want to remove a piece, then a null piece can be passed in
    private void setPiece(Piece p, Point[] body, Point position) {
        if (body == null || position == null) return;

        for (int i = 0; i < body.length; i++) {
            int x = (int) (position.getX() + body[i].getX());
            int y = (int) (position.getY() + body[i].getY());
            board[x][y] = p;
        }
    }

    // checks whether a piece p can be placed in the given position
    // returns integer: 0 means it works, 1 means piece is out of bounds, 2 means piece intersects
    private int checkPiece(Piece p, Point position) {
        if (p == null || position == null) return 2;

        Point[] body = p.getBody();
        // make sure the piece is in bounds and does not intersect
        for (int i = 0; i < body.length; i++) {
            int currentPointX = (int)(position.getX() + body[i].getX());
            int currentPointY = (int)(position.getY() + body[i].getY());

            // point is out of bounds
            if (outOfBounds(currentPointX, currentPointY)) {
                return 1;
            }

            // point is already occupied by a piece (excluding the current piece)
            if (getGrid(currentPointX, currentPointY) != null) {
                return 2;
            }
        }
        return 0;
    }

    // determine whether two tetrisboards are equal (same pieces on the board, same current piece and current position)
    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris boards.
        if(!(other instanceof TetrisBoard)) return false;
        TetrisBoard otherBoard = (TetrisBoard) other;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // TODO update based on piazza
                if (this.board[i][j] == null) {
                    if (otherBoard.board[i][j] != null) {
                        return false;
                    }
                } else if (!this.board[i][j].equals(otherBoard.board[i][j])) {
                    return false;
                }
            }
        }

        if (this.getCurrentPiece() == null) {
            return otherBoard.getCurrentPiece() == null;
        }
        if (this.getCurrentPiecePosition() == null) {
            return otherBoard.getCurrentPiecePosition() == null;
        }
        // ensure they have the same current piece at the same location
        return this.getCurrentPiece().equals(otherBoard.getCurrentPiece()) &&
                this.getCurrentPiecePosition().equals(otherBoard.getCurrentPiecePosition());
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

    // TODO copy method over and then redo this override method
    // determine the height at which a piece would rest if dropped at in a given column
    @Override
    public int dropHeight(Piece piece, int x) {
        if (piece == null) return 0;
        int[] skirt = piece.getSkirt();
        int height = 0;

        for (int i = 0; i < skirt.length; i++) {
            // The drop height will depend on each element in the skirt array with the respective
            // height the piece needs to go down at this index
            if (skirt[i] != Integer.MAX_VALUE && x+i < getWidth()) {
                height = Math.max(getColumnHeight(x + i) - skirt[i], height);
            }
        }

        return height;
    }

    // finds the height this piece will drop down to going dowm from this x position (assuming its the current piece)
    public int findDropHeight(Piece piece, int x) {
        if (piece == null) return 0;
        int[] skirt = piece.getSkirt();

        // find the minimum value of the skirt, so that point can be placed at the bottom
        int minSkirt = skirt[0];
        for (int skirtVal : skirt) {
            minSkirt = Math.min(skirtVal, minSkirt);
        }

        int dropY = -minSkirt;

        for (int i = 0; i < skirt.length; i++) {
            // The drop height will depend on each element in the skirt array with the respective
            // height the piece needs to go down at this index
            if (skirt[i] != Integer.MAX_VALUE) {
                int y = (int) (currentPosition.getY() + skirt[i]);

                // check each cell below the current one in the skirt
                for (int j = y; j >= 0; j--) {
                    if (getGrid(x + i, j) != null) {
                        // there exists a piece at this index (x, j), therefore we can move it to height j+1
                        dropY = Math.max(dropY, j + 1 - skirt[i]);
                        break;
                    }
                }
            }
        }

        return dropY;
    }

    @Override
    public int getColumnHeight(int x) {
        if (outOfBounds(x, 0)) return 0;
        return this.blocksFilledPerColumn[x];
    }

    @Override
    public int getRowWidth(int y) {
        if (outOfBounds(0, y)) return 0;
        return this.blocksFilledPerRow[y];
    }

    // determine whether a point is on the current piece
    private boolean isPointOnCurrentPiece(Point toCheck) {
        if (currentPiece == null || currentPosition == null || toCheck == null) {
            return false;
        }

        for (Point p : currentPiece.getBody()) {
            Point realCurrentPoint = new Point((int) (p.getX() + currentPosition.getX()), (int) (p.getY() + currentPosition.getY()));
            if (realCurrentPoint.equals(toCheck)) {
                return true;
            }
        }

        return false;
    }

    // show the type of the piece at a certain location on the grid, not counting the current piece
    @Override
    public Piece.PieceType getGrid(int x, int y) {
        if (outOfBounds(x, y) || this.board[x][y] == null || isPointOnCurrentPiece(new Point(x, y))) {
            return null;
        } else {
            return this.board[x][y].getType();
        }
    }

    // determine whether coordinates are on the grid
    private boolean outOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= getWidth() || y >= getHeight();
    }

    private void printBoard() {
        for (int i = 0 ; i< width; i++) {
            for (int j = 0; j < height; j++) {
                if (getGrid(i, j) != null) {
                    System.out.print("Y ");
                }
                else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }
}