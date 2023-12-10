package main;

import java.util.LinkedList;

public class Sos {
    private int[][] board;
    private int p1Points;
    private int p2Points;
    private boolean p1Turn;
    private int piece;
    private boolean gameOver;
    private LinkedList<Move> moves;
    private SosIterator savedGame;
    public static final int EMPTY = 0;
    public static final int O = 1;
    public static final int S = 2;

    /**
     * Constructor that sets up game state.
     */
    public Sos() {
        reset();
    }

    /**
     * Resets the game state to start a new game. The length of the board's sides is
     * randomly chosen to be between 3 and 15, inclusive.
     */
    public void reset() {
        int len = (int) (Math.random() * 13) + 3;
        board = new int[len][len];
        p1Points = 0;
        p2Points = 0;
        p1Turn = true;
        piece = S;
        gameOver = false;
        moves = new LinkedList<>();
        savedGame = new SosIterator();
    }

    /**
     * Allows players to play a move. Returns true if the move is successful
     * and false otherwise. If the turn is successful and the player has no more
     * available moves that turn (either the game ended, the player did not make
     * an SOS, or they cannot make another SOS in another move), the player is
     * changed. Otherwise, the player is not changed.
     *
     * @param c column to play in
     * @param r row to play in
     * @return true if move is successful, false otherwise
     */
    public boolean playMove(int c, int r) {
        if (board[r][c] != EMPTY || gameOver) {
            return false;
        }

        int gainedPoints = getAdditionalSOS(c, r, piece);
        if (p1Turn) {
            p1Points += gainedPoints;
        } else {
            p2Points += gainedPoints;
        }
        board[r][c] = piece;
        moves.addLast(new Move(c, r, p1Turn, gainedPoints));

        if (checkWinner() == 0 && gainedPoints == 0) {
            p1Turn = !p1Turn;
        }
        return true;
    }

    /**
     * Allows the current player to undo the most recent move. Returns true
     * if the undo is successful and false otherwise.
     *
     * @return true if undo is successful, false otherwise
     */
    public boolean undoMove() {
        if (moves.isEmpty() || gameOver) {
            return false;
        }

        Move move = moves.removeLast();
        board[move.getRow()][move.getColumn()] = EMPTY;
        if (move.isP1Turn()) {
            p1Points -= move.getPointsGained();
            p1Turn = true;
        } else {
            p2Points -= move.getPointsGained();
            p1Turn = false;
        }
        return true;
    }

    /**
     * Sets the current piece to play the given piece.
     *
     * @param m the current piece to play
     */
    public void setPiece(int m) {
        piece = m;
    }

    /**
     * Checks whether the game has reached a win condition.
     *
     * @return 0 if nobody has won yet, 1 if Player 1 has won, 2 if Player 2
     *         has won, and 3 if tied
     */
    public int checkWinner() {
        if (moves.size() == board.length * board.length) {
            gameOver = true;
            if (p1Points > p2Points) {
                return 1;
            } else if (p2Points > p1Points) {
                return 2;
            } else {
                return 3;
            }
        } else {
            return 0;
        }
    }

    /**
     * Saves the current game state. If the game has already ended, nothing happens.
     */
    public void saveGame() {
        if (!gameOver) {
            savedGame.rewriteToFile(board, p1Points, p2Points, p1Turn, piece, moves);
            savedGame.reset();
        }
    }

    /**
     * Loads the saved game state if there is one. If there is no saved game state,
     * nothing changes.
     */
    public void loadGame() {
        if (!savedGame.hasNext()) {
            return;
        }

        gameOver = false;

        try {
            p1Points = Integer.parseInt(savedGame.next());
            p2Points = Integer.parseInt(savedGame.next());
            p1Turn = Boolean.parseBoolean(savedGame.next());
            piece = Integer.parseInt(savedGame.next());

            int boardLength = Integer.parseInt(savedGame.next());
            board = new int[boardLength][boardLength];
            for (int r = 0; r < boardLength; r++) {
                String[] row = savedGame.next().split(",");
                for (int c = 0; c < boardLength; c++) {
                    board[r][c] = Integer.parseInt(row[c]);
                }
            }

            moves = new LinkedList<>();
            while (savedGame.hasNext()) {
                String[] move = savedGame.next().split(",");
                int column = Integer.parseInt(move[0]);
                int row = Integer.parseInt(move[1]);
                boolean turn = Boolean.parseBoolean(move[2]);
                int points = Integer.parseInt(move[3]);
                moves.addLast(new Move(column, row, turn, points));
            }

            savedGame.reset();
        } catch (Exception e) {
            System.out.println("gamestate.csv may have been tampered with");
        }
    }

    /**
     * Returns true if it is possible to make another SOS on the board with
     * a single S or O, false otherwise.
     *
     * @return true if it is possible to make another SOS on the board with a single
     *         S or O
     */
    public boolean possibleSOS() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                if (getAdditionalSOS(c, r, O) > 0 || getAdditionalSOS(c, r, S) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the number of potential additional SOS's if a move m is made
     * at a given empty spot on the board.
     *
     * @param c column that could be played in
     * @param r row that could be played in
     * @param m move that could be played
     * @return number of potential additional SOS's
     */
    public int getAdditionalSOS(int c, int r, int m) {
        int sosCount = 0;

        if (m == O) {
            // horizontal check
            if (c - 1 >= 0 && c + 1 < board.length &&
                    board[r][c - 1] == S && board[r][c + 1] == S) {
                sosCount++;
            }
            // vertical check
            if (r - 1 >= 0 && r + 1 < board.length &&
                    board[r - 1][c] == S && board[r + 1][c] == S) {
                sosCount++;
            }
            // diagonals check
            if (c - 1 >= 0 && c + 1 < board.length && r - 1 >= 0 && r + 1 < board.length) {
                if (board[r - 1][c - 1] == S && board[r + 1][c + 1] == S) {
                    sosCount++;
                }
                if (board[r - 1][c + 1] == S && board[r + 1][c - 1] == S) {
                    sosCount++;
                }
            }
        } else {
            // horizontal check
            if (c - 2 >= 0 && board[r][c - 2] == S && board[r][c - 1] == O) {
                sosCount++;
            }
            if (c + 2 < board.length && board[r][c + 1] == O && board[r][c + 2] == S) {
                sosCount++;
            }
            // vertical check
            if (r - 2 >= 0 && board[r - 2][c] == S && board[r - 1][c] == O) {
                sosCount++;
            }
            if (r + 2 < board.length && board[r + 1][c] == O && board[r + 2][c] == S) {
                sosCount++;
            }
            // left diagonals check
            if (c - 2 >= 0) {
                // upper left diagonal check
                if (r - 2 >= 0 && board[r - 2][c - 2] == S && board[r - 1][c - 1] == O) {
                    sosCount++;
                }
                // lower left diagonal check
                if (r + 2 < board.length && board[r + 2][c - 2] == S && board[r + 1][c - 1] == O) {
                    sosCount++;
                }
            }
            // right diagonals check
            if (c + 2 < board.length) {
                // upper right diagonal check
                if (r - 2 >= 0 && board[r - 2][c + 2] == S && board[r - 1][c + 1] == O) {
                    sosCount++;
                }
                // lower right diagonal check
                if (r + 2 < board.length && board[r + 2][c + 2] == S && board[r + 1][c + 1] == O) {
                    sosCount++;
                }
            }
        }

        return sosCount;
    }

    /**
     * Returns true if it's Player 1's turn and false if it's Player 2's turn.
     *
     * @return true if it's Player 1's turn,
     *         false if it's Player 2's turn.
     */
    public boolean getCurrentPlayer() {
        return p1Turn;
    }

    /**
     * Gets the contents of the given cell.
     *
     * @param c column to retrieve
     * @param r row to retrieve
     * @return an integer denoting the contents of the corresponding cell on the
     *         game board. -1 = empty, 0 = O, 1 = S
     */
    public int getCell(int c, int r) {
        return board[r][c];
    }

    /**
     * Get the length of each side of the board.
     *
     * @return the length of each side of the board
     */
    public int getLength() {
        return board.length;
    }

    /**
     * Returns true if the game is over, false otherwise.
     *
     * @return true if the game is over
     */
    public boolean gameIsOver() {
        return gameOver;
    }

    /**
     * Gets the number of points Player 1 has.
     *
     * @return Player 1's points
     */
    public int getP1Points() {
        return p1Points;
    }

    /**
     * Gets the number of points Player 2 has.
     *
     * @return Player 2's points
     */
    public int getP2Points() {
        return p2Points;
    }

    /**
     * Gets the current piece that is being played.
     *
     * @return the current piece that is being played
     */
    public int getPiece() {
        return piece;
    }
}