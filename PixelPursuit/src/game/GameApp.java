package game;

import javax.swing.SwingUtilities;
import game.ui.LogInWindow;

public class GameApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LogInWindow();
        });
    }
}