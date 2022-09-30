package assignment;

import java.awt.Point;
import java.util.*;

/**
 * An immutable representation of a tetris piece in a particular rotation.
 * 
 * All operations on a TetrisPiece should be constant time, except for its
 * initial construction. This means that rotations should also be fast - calling
 * clockwisePiece() and counterclockwisePiece() should be constant time! You may
 * need to do pre-computation in the constructor to make this possible.
 */
public final class TetrisPiece implements Piece {

    /**
     * Construct a tetris piece of the given type. The piece should be in its spawn orientation,
     * i.e., a rotation index of 0.
     * 
     * You may freely add additional constructors, but please leave this one - it is used both in
     * the runner code and testing code.
     */

    private static Point[] rotateClockwise(Point[] points, int width, int height) {
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

    private static final Map<PieceType, List<Piece>> rotationComputations = new HashMap<PieceType, List<Piece>>();
    static {
        for (PieceType pt : PieceType.values()) {
            List<Piece> rotations = new ArrayList<Piece>();
            // initial body
            rotations.add(new TetrisPiece(pt, 0, pt.getSpawnBody()));

            for (int i = 0; i < 3; i++) {
                // rotate the previous one and add it to the list as a new piece
                Piece previous = rotations.get(i);
                Point[] newBody = rotateClockwise(previous.getBody(), previous.getWidth() - 1, previous.getHeight() - 1);
                rotations.add(new TetrisPiece(pt, i + 1, newBody));
            }

            rotationComputations.put(pt, rotations);
        }
    }

    private final PieceType type;
    private final int rotationIndex;
    private final Point[] body;
    private final int[] skirt;

    public TetrisPiece(PieceType type) {
        this(type, 0);
    }

    // Utilized for lookup
    private TetrisPiece(PieceType type, int rotationIndex) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = rotationComputations.get(type).get(rotationIndex).getBody();
        this.skirt = rotationComputations.get(type).get(rotationIndex).getSkirt();
    }

    // Utilized for precomputing the tetris piece
    private TetrisPiece(PieceType type, int rotationIndex, Point[] body) {
        this.type = type;
        this.rotationIndex = rotationIndex;
        this.body = body;
        this.skirt = new int[getWidth()];
        Arrays.fill(this.skirt, Integer.MAX_VALUE);

        for (Point p : body) {
            int x = (int) (p.getX());
            int y = (int) (p.getY());
            this.skirt[x] = Math.min(this.skirt[x], y);
        }
    }

    @Override
    public PieceType getType() {
        return this.type;
    }

    @Override
    public int getRotationIndex() {
        return this.rotationIndex;
    }

    @Override
    public Piece clockwisePiece() {
        // move one forwards in the list of rotations, wrapping around if needed
        int newRotationIndex = (rotationIndex + 1) % (rotationComputations.get(type).size());
        return rotationComputations.get(type).get(newRotationIndex);
    }

    @Override
    public Piece counterclockwisePiece() {
        // move one backwards in the list of rotations, wrapping around if needed
        int newRotationIndex = (rotationIndex - 1 + rotationComputations.get(type).size()) % (rotationComputations.get(type).size());
        return rotationComputations.get(type).get(newRotationIndex);
    }

    @Override
    public int getWidth() {
        return (int) (this.type.getBoundingBox().getWidth());
    }

    @Override
    public int getHeight() {
        return (int) (this.type.getBoundingBox().getHeight());
    }

    @Override
    public Point[] getBody() {
        return this.body;
    }

    @Override
    public int[] getSkirt() {
        return this.skirt;
    }

    @Override
    public boolean equals(Object other) {
        // Ignore objects which aren't also tetris pieces.
        if(!(other instanceof TetrisPiece)) return false;
        TetrisPiece otherPiece = (TetrisPiece) other;

        return (this.getType() == otherPiece.getType()) && (this.getRotationIndex() == otherPiece.getRotationIndex());
    }
}
