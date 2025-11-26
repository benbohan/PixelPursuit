package game;

import javax.swing.SwingUtilities;
import game.ui.windows.LogInWindow;

public class GameApp {
    
	// Game Launch - Run file to launch PixelPursuit
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LogInWindow();
        });
    }
}