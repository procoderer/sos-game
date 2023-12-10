package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SosBoard extends JPanel {
    private final Sos sos;
    private final JLabel status;
    private final JLabel p1Points;
    private final JLabel p2Points;
    private int squareLength;
    private float fontSize;
    public static final int SMALL_SQUARE_LENGTH = 40;
    public static final int BIG_SQUARE_LENGTH = 50;
    public static final float SMALL_FONT_SIZE = 24;
    public static final float BIG_FONT_SIZE = 30;
    public static final int SMALL_BIG_BOUNDARY = 10;

    /**
     * Initializes the game board.
     */
    public SosBoard(JLabel statusInit, JLabel p1PointsInit, JLabel p2PointsInit) {
        // Enable keyboard focus on the board area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        sos = new Sos();
        status = statusInit;
        p1Points = p1PointsInit;
        p2Points = p2PointsInit;
        squareLength = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_SQUARE_LENGTH
                : SMALL_SQUARE_LENGTH;
        fontSize = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_FONT_SIZE : SMALL_FONT_SIZE;

        /*
         * Listens for mouseclicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                int r = p.x / squareLength;
                int c = p.y / squareLength;
                if (r < sos.getLength() && c < sos.getLength()) {
                    sos.playMove(r, c);
                }

                updateStatus();
                repaint();
            }
        });
    }

    /**
     * Resets the game to its initial state.
     */
    public void reset() {
        sos.reset();
        status.setText("Player 1's Turn");
        p1Points.setText("Player 1: 0");
        p2Points.setText("Player 2: 0");
        squareLength = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_SQUARE_LENGTH
                : SMALL_SQUARE_LENGTH;
        fontSize = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_FONT_SIZE : SMALL_FONT_SIZE;
        repaint();

        // Makes sure this component has keyboard/mouse focus
        requestFocusInWindow();
    }

    /**
     * Sets the piece to be played.
     */
    public void setPiece(int m) {
        sos.setPiece(m);
    }

    /**
     * Undoes a move.
     */
    public void undo() {
        if (sos.undoMove()) {
            updateStatus();
            repaint();
        }
    }

    /**
     * Saves the current game state.
     */
    public void save() {
        sos.saveGame();
    }

    /**
     * Loads the saved game state if there is one.
     */
    public void load() {
        sos.loadGame();
        squareLength = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_SQUARE_LENGTH
                : SMALL_SQUARE_LENGTH;
        fontSize = sos.getLength() <= SMALL_BIG_BOUNDARY ? BIG_FONT_SIZE : SMALL_FONT_SIZE;
        updateStatus();
        repaint();
    }

    /**
     * Updates the JLabel to reflect the current state of the game.
     */
    private void updateStatus() {
        if (sos.getCurrentPlayer()) {
            status.setText("Player 1's Turn");
        } else {
            status.setText("Player 2's Turn");
        }

        p1Points.setText("Player 1: " + sos.getP1Points());
        p2Points.setText("Player 2: " + sos.getP2Points());

        int winner = sos.checkWinner();
        if (winner == 1) {
            status.setText("Player 1 wins!");
        } else if (winner == 2) {
            status.setText("Player 2 wins!");
        } else if (winner == 3) {
            status.setText("It's a tie.");
        }
    }

    /**
     * Draws the game board.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(g.getFont().deriveFont(fontSize));

        // Draws grid
        int lineLength = sos.getLength() * squareLength;
        for (int i = 0; i <= sos.getLength(); i++) {
            int pos = i * squareLength;
            g.drawLine(pos, 0, pos, lineLength);
            g.drawLine(0, pos, lineLength, pos);
        }

        // Draws S's and O's
        for (int r = 0; r < sos.getLength(); r++) {
            for (int c = 0; c < sos.getLength(); c++) {
                int y = (int) ((c + 0.69) * squareLength);
                if (sos.getCell(r, c) == Sos.S) {
                    int x = (int) ((r + 0.35) * squareLength);
                    g.drawString("S", x, y);
                } else if (sos.getCell(r, c) == Sos.O) {
                    int x = (int) ((r + 0.28) * squareLength);
                    g.drawString("O", x, y);
                }
            }
        }
    }

    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(sos.getLength() * squareLength, sos.getLength() * squareLength);
    }
}
