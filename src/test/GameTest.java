package test;

import main.*;
import org.junit.jupiter.api.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static main.Sos.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    /**
     * Empties the file used for saving game state. Used after testing the
     * load and save functionality for Sos.
     */
    public static void emptyGameStateFile() {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(SosIterator.FILE_PATH, false));
            writer.write("");
        } catch (IOException ignored) {
        }
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ignored) {
        }
    }

    @Test
    public void testInitialization() {
        Sos game = new Sos();
        for (int r = 0; r < game.getLength(); r++) {
            for (int c = 0; c < game.getLength(); c++) {
                assertEquals(EMPTY, game.getCell(c, r));
            }
        }
        assertTrue(game.getCurrentPlayer());
        assertFalse(game.gameIsOver());
    }

    @Test
    public void testPlayTurnOnEmptyCell() {
        Sos game = new Sos();
        game.setPiece(S);
        assertTrue(game.playMove(0, 0), "Playing on an empty cell should be successful");
    }

    @Test
    public void testPlayTurnOnNonEmptyCell() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        assertFalse(game.playMove(0, 0), "Playing on a non-empty cell should fail");
    }

    @Test
    public void testPointsAwardedForSOS() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        game.setPiece(O);
        game.playMove(1, 0);
        game.setPiece(S);
        game.playMove(2, 0);
        assertEquals(1, game.getP1Points(), "Player 1 should have 1 point");
    }

    @Test
    public void testNoPointsAwardedWithoutSOS() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        assertEquals(0, game.getP1Points(), "No points should be awarded");
    }

    @Test
    public void testGameOverWhenBoardFull() {
        Sos game = new Sos();
        game.setPiece(S);
        for (int r = 0; r < game.getLength(); r++) {
            for (int c = 0; c < game.getLength(); c++) {
                game.playMove(c, r);
            }
        }
        assertTrue(game.gameIsOver());
    }

    @Test
    public void testPlayerTurnSwitchesAfterMove() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        assertFalse(game.getCurrentPlayer(), "It should be Player 2's turn after Player 1's move");
    }

    @Test
    public void testPlayerTurnDoesNotSwitchAfterSOS() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        game.setPiece(O);
        game.playMove(1, 0);
        game.setPiece(S);
        game.playMove(2, 0);
        assertTrue(game.getCurrentPlayer(), "Player 1's turn should continue after forming SOS");
    }

    @Test
    public void testBoardReset() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        game.reset();
        assertEquals(EMPTY, game.getCell(0, 0), "Cell should be empty after reset");
        assertTrue(game.getCurrentPlayer(), "It should be Player 1's turn after reset");
    }

    @Test
    public void testPlayTurnOnBoardBoundary() {
        Sos game = new Sos();
        int len = game.getLength();
        game.setPiece(S);
        assertTrue(game.playMove(0, len - 1), "Playing on boundary cell should be successful");
    }

    @Test
    public void testUndoMoveOnEmptyBoard() {
        Sos game = new Sos();
        assertFalse(game.undoMove(), "Undo should fail on an empty board");
    }

    @Test
    public void testUndoAffectsScoreCorrectly() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        game.setPiece(O);
        game.playMove(0, 1);
        game.setPiece(S);
        game.playMove(0, 2);
        int scoreBeforeUndo = game.getP1Points();
        game.undoMove();
        int scoreAfterUndo = game.getP1Points();
        assertEquals(scoreAfterUndo, scoreBeforeUndo - 1, "Score should decrease after undo");
    }

    @Test
    public void testUndoSwitchesTurnBack() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0);
        boolean turnBeforeUndo = game.getCurrentPlayer();
        game.undoMove();
        boolean turnAfterUndo = game.getCurrentPlayer();
        assertNotEquals(turnBeforeUndo, turnAfterUndo, "Player turn should switch back after undo");
    }

    @Test
    public void testMultipleUndoMoves() {
        Sos game = new Sos();
        game.setPiece(O);
        game.playMove(0, 0);
        game.setPiece(S);
        game.playMove(0, 1);
        assertTrue(game.undoMove());
        assertTrue(game.undoMove());
        assertEquals(EMPTY, game.getCell(0, 0), "Cell should be empty after undo");
        assertEquals(EMPTY, game.getCell(0, 1), "Cell should be empty after undo");
    }

    @Test
    public void testNoUndoWhenGameOver() {
        Sos game = new Sos();
        game.setPiece(S);
        for (int r = 0; r < game.getLength(); r++) {
            for (int c = 0; c < game.getLength(); c++) {
                game.playMove(c, r);
            }
        }
        assertFalse(game.undoMove());
    }

    @Test
    public void testLastCellPlayedFormsSOS() {
        Sos game = new Sos();
        boolean p1Turn = false;
        for (int r = 0; r < game.getLength(); r++) {
            for (int c = 0; c < game.getLength(); c++) {
                if (r == game.getLength() - 1 && c == game.getLength() - 2) {
                    // after this move, the next move should form an SOS
                    game.setPiece(O);
                    game.playMove(c, r);
                    p1Turn = game.getCurrentPlayer();
                }
                game.setPiece(S);
                game.playMove(c, r);
            }
        }
        if (p1Turn) {
            assertEquals(1, game.getP1Points());
            assertEquals(0, game.getP2Points());
        } else {
            assertEquals(1, game.getP2Points());
            assertEquals(0, game.getP1Points());
        }
    }

    @Test
    public void testLoadGameMultipleTimes() {
        Sos game = new Sos();
        game.setPiece(O);
        game.playMove(1, 1);
        game.saveGame();

        Sos firstLoad = new Sos();
        firstLoad.loadGame();
        Sos secondLoad = new Sos();
        secondLoad.loadGame();

        assertEquals(game.getLength(), firstLoad.getLength());
        assertEquals(firstLoad.getLength(), secondLoad.getLength());
        for (int r = 0; r < firstLoad.getLength(); r++) {
            for (int c = 0; c < firstLoad.getLength(); c++) {
                if (r == 1 && c == 1) {
                    assertEquals(O, firstLoad.getCell(1, 1));
                    assertEquals(O, secondLoad.getCell(1, 1));
                } else {
                    assertEquals(EMPTY, firstLoad.getCell(c, r));
                    assertEquals(EMPTY, secondLoad.getCell(c, r));
                }
            }
        }

        assertEquals(0, firstLoad.getP1Points());
        assertEquals(0, firstLoad.getP2Points());
        assertFalse(
                firstLoad.getCurrentPlayer(), "Should be Player 2's turn after Player 1's turn"
        );
        assertEquals(O, firstLoad.getPiece());
        assertFalse(firstLoad.gameIsOver());
        assertEquals(0, secondLoad.getP1Points());
        assertEquals(0, secondLoad.getP2Points());
        assertFalse(
                secondLoad.getCurrentPlayer(), "Should be Player 2's turn after Player 1's turn"
        );
        assertEquals(O, secondLoad.getPiece());
        assertFalse(secondLoad.gameIsOver());

        assertTrue(firstLoad.undoMove());
        assertFalse(firstLoad.undoMove(), "Should only be one move to undo");
        assertTrue(secondLoad.undoMove());
        assertFalse(secondLoad.undoMove(), "Should only be one move to undo");

        emptyGameStateFile();
    }

    @Test
    public void testSaveAndLoadAfterSeveralMoves() {
        Sos game = new Sos();
        game.setPiece(S);
        game.playMove(0, 0); // Player 1's move, player switches
        game.setPiece(O);
        game.playMove(0, 1); // Player 2's move, player switches
        game.setPiece(S);
        game.playMove(0, 2); // Player 1's move, player doesn't switch and Player 1 gains a point
        game.saveGame();

        Sos loadedGame = new Sos();
        loadedGame.loadGame();

        assertEquals(game.getLength(), loadedGame.getLength());
        for (int r = 0; r < loadedGame.getLength(); r++) {
            for (int c = 0; c < loadedGame.getLength(); c++) {
                if (c == 0 && (r == 0 || r == 2)) {
                    assertEquals(S, loadedGame.getCell(0, 0));
                } else if (r == 1 && c == 0) {
                    assertEquals(O, loadedGame.getCell(0, 1));
                } else {
                    assertEquals(EMPTY, loadedGame.getCell(c, r));
                }
            }
        }

        assertEquals(1, loadedGame.getP1Points());
        assertEquals(0, loadedGame.getP2Points());
        assertTrue(loadedGame.getCurrentPlayer());
        assertEquals(S, loadedGame.getPiece());
        assertFalse(loadedGame.gameIsOver());

        assertTrue(loadedGame.undoMove());
        assertTrue(loadedGame.undoMove());
        assertTrue(loadedGame.undoMove());
        assertFalse(loadedGame.undoMove(), "Should only be three moves to undo");

        emptyGameStateFile();
    }

    @Test
    public void testLoadWithoutPriorSave() {
        Sos game = new Sos();
        game.loadGame();
        for (int r = 0; r < game.getLength(); r++) {
            for (int c = 0; c < game.getLength(); c++) {
                assertEquals(EMPTY, game.getCell(c, r));
            }
        }
        assertTrue(game.getCurrentPlayer());
        assertFalse(game.gameIsOver());
    }
}
