package main;

import java.io.*;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class SosIterator implements Iterator<String> {
    private boolean nextExists;
    private String nextLine;
    private BufferedReader reader;
    public static final String FILE_PATH = "files/gamestate.csv";

    /**
     * Initializes a FileIterator.
     */
    public SosIterator() {
        reset();
    }

    /**
     * Resets the SosIterator.
     */
    public void reset() {
        try {
            reader = new BufferedReader(new FileReader(FILE_PATH));
            nextLine = reader.readLine();
            nextExists = nextLine != null;
        } catch (IOException e) {
            nextExists = false;
        }
    }

    /**
     * Returns true if there are lines left to read in the file, false otherwise.
     * If there are no more lines left, this method attempts to close the
     * BufferedReader and then reset the SosIterator.
     *
     * @return true if there is another line, false if there isn't another line
     */
    @Override
    public boolean hasNext() {
        if (!nextExists) {
            try {
                reader.close();
            } catch (IOException ignored) {
            }
        }
        return nextExists;
    }

    /**
     * Returns the next line from the file, or throws a NoSuchElementException
     * if there are no more strings left to return (i.e. hasNext() is false).
     *
     * @return the next line in the file
     * @throws java.util.NoSuchElementException if there are no more lines
     */
    @Override
    public String next() {
        if (nextExists) {
            String output = nextLine;
            try {
                nextLine = reader.readLine();
                nextExists = nextLine != null;
            } catch (IOException e) {
                nextExists = false;
            }
            return output;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Writes the given game state to the file
     *
     * @param board    the current board
     * @param p1Points the points Player 1 has
     * @param p2Points the points Player 2 has
     * @param p1Turn   true if Player 1's turn, false if Player 2's turn
     * @param piece    the selected piece
     * @param moves    the moves made in the game thus far
     */
    public void rewriteToFile(
            int[][] board, int p1Points, int p2Points,
            boolean p1Turn, int piece, List<Move> moves
    ) {
        File file = Paths.get(FILE_PATH).toFile();
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file, false));

            writer.write(String.valueOf(p1Points));
            writer.newLine();
            writer.write(String.valueOf(p2Points));
            writer.newLine();
            writer.write(String.valueOf(p1Turn));
            writer.newLine();
            writer.write(String.valueOf(piece));
            writer.newLine();
            writer.write(String.valueOf(board.length));

            for (int[] row : board) {
                writer.newLine();
                for (int i = 0; i < board.length; i++) {
                    writer.write(String.valueOf(row[i]));
                    if (i != board.length - 1) {
                        writer.write(",");
                    }
                }
            }

            for (Move move : moves) {
                writer.newLine();
                String column = String.valueOf(move.getColumn());
                String row = String.valueOf(move.getRow());
                String turn = String.valueOf(move.isP1Turn());
                String points = String.valueOf(move.getPointsGained());
                writer.write(column + "," + row + "," + turn + "," + points);
            }
        } catch (IOException e) {
            System.out.println("There is likely a bug in the code.");
        }

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ignored) {
        }
    }
}
