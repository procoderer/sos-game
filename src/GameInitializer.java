import javax.swing.*;

public class GameInitializer {
    /**
     * Main method to start and run the game.
     */
    public static void main(String[] args) {
        Runnable game = new main.RunSos();

        SwingUtilities.invokeLater(game);
    }
}