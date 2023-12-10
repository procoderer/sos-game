package main;

import javax.swing.*;
import java.awt.*;

public class RunSos implements Runnable {
    public static final float LARGER_FONT_SIZE = 15;

    public void run() {
        // Top-level frame in which game components live
        final JFrame frame = new JFrame("SOS");

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        // Status label
        final JLabel status = new JLabel("Setting up...");
        status.setFont(status.getFont().deriveFont(LARGER_FONT_SIZE));
        status_panel.add(status);

        // Player 1 points panel
        final JPanel p1Points_panel = new JPanel();
        // Player 1 points label
        final JLabel p1Points = new JLabel("Player 1: 0");
        p1Points.setPreferredSize(new Dimension(90, 20));
        p1Points.setFont(p1Points.getFont().deriveFont(LARGER_FONT_SIZE));
        p1Points_panel.add(p1Points);
        // Player 2 points panel
        final JPanel p2Points_panel = new JPanel();
        // Player 2 points label
        final JLabel p2Points = new JLabel("Player 2: 0");
        p2Points.setPreferredSize(new Dimension(90, 20));
        p2Points.setFont(p2Points.getFont().deriveFont(LARGER_FONT_SIZE));
        p2Points_panel.add(p2Points);
        // Game board
        final SosBoard board = new SosBoard(status, p1Points, p2Points);
        // Board and points combined panel
        final JPanel boardAndPoints_panel = new JPanel();
        boardAndPoints_panel.setLayout(new BoxLayout(boardAndPoints_panel, BoxLayout.X_AXIS));
        boardAndPoints_panel.add(new JPanel().add(new JLabel("           "))); // adds empty space
        boardAndPoints_panel.add(board);
        boardAndPoints_panel.add(p1Points_panel);
        boardAndPoints_panel.add(p2Points_panel);
        frame.add(boardAndPoints_panel, BorderLayout.CENTER);

        // Control panel
        final JPanel control_panel = new JPanel();
        frame.add(control_panel, BorderLayout.NORTH);
        // S button
        final JButton s = new JButton("S");
        s.addActionListener(e -> board.setPiece(Sos.S));
        control_panel.add(s);
        // O button
        final JButton o = new JButton("O");
        o.addActionListener(e -> board.setPiece(Sos.O));
        control_panel.add(o);
        // Undo button
        final JButton undo = new JButton("Undo");
        undo.addActionListener(e -> board.undo());
        control_panel.add(undo);
        // Reset button
        final JButton reset = new JButton("Reset");
        reset.addActionListener(e -> {
            board.reset();
            board.revalidate();
        });
        control_panel.add(reset);
        // Save button
        final JButton save = new JButton("Save");
        save.addActionListener(e -> board.save());
        control_panel.add(save);
        // Load button
        final JButton load = new JButton("Load");
        load.addActionListener(e -> {
            board.load();
            board.revalidate();
        });
        control_panel.add(load);
        // Help button
        final JButton help = new JButton("Help");
        help.addActionListener(e -> openInstructions());
        control_panel.add(help);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1300, 750));
        frame.setVisible(true);

        // Start the game
        board.reset();
    }

    private static void openInstructions() {
        JFrame instructionsFrame = new JFrame("Help");
        instructionsFrame.setLayout(new BorderLayout());

        JTextArea instructionsText = new JTextArea();
        instructionsText.setText(
                "\nRules\n" +
                        "Players are presented with an n-by-n square grid, with n randomized " +
                        "between 3 and 15, inclusive. Players take turns to add either an \"S\" " +
                        "or an \"O\" to any square, with no requirement to use the same letter " +
                        "each turn. The objective of the game is for each player to attempt to " +
                        "create the straight sequence S-O-S among connected squares either " +
                        "diagonally, horizontally, or vertically, and to create as many such " +
                        "sequences as they can. If a player succeeds in creating an SOS, that " +
                        "player immediately takes another turn, and continues to do so until no " +
                        "SOS can be created on their turn. Otherwise, turns alternate between " +
                        "players after each move.\n" +
                        "\nControls\n" +
                        "Click \"S\" or \"O\" to choose a move.\n" +
                        "Click inside of a square to play a move.\n" +
                        "Click \"Undo\" to undo a move.\n" +
                        "Click \"Reset\" to reset the game and play on a new board.\n" +
                        "Click \"Save\" to save the current game state.\n" +
                        "Click \"Load\" to load the most recently saved game."
        );
        instructionsText.setEditable(false);
        instructionsText.setWrapStyleWord(true);
        instructionsText.setLineWrap(true);
        instructionsText.setFont(instructionsText.getFont().deriveFont(LARGER_FONT_SIZE));

        JScrollPane scrollPane = new JScrollPane(instructionsText);
        instructionsFrame.add(scrollPane, BorderLayout.CENTER);

        instructionsFrame.setSize(500, 450);
        instructionsFrame.setVisible(true);
    }
}
