package main;

public class Move {
    private final int column;
    private final int row;
    private final boolean p1Turn;
    private final int pointsGained;

    /**
     * Constructor that constructs a move based on given arguments.
     *
     * @param c      column move is played in
     * @param r      row move is played in
     * @param p1Move true if Player 1's move, false if Player 2's move
     * @param points number of points gained by move
     */
    public Move(int c, int r, boolean p1Move, int points) {
        column = c;
        row = r;
        p1Turn = p1Move;
        pointsGained = points;
    }

    /**
     * Gets the column where the move was played.
     *
     * @return the column index of the move
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the row where the move was played.
     *
     * @return the row index of the move
     */
    public int getRow() {
        return row;
    }

    /**
     * Checks if it was Player 1's turn when this move was made.
     *
     * @return true if it was Player 1's turn, false otherwise
     */
    public boolean isP1Turn() {
        return p1Turn;
    }

    /**
     * Gets the number of points gained from this move.
     *
     * @return the number of points gained
     */
    public int getPointsGained() {
        return pointsGained;
    }
}
