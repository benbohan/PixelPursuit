package game;

import javax.swing.SwingUtilities;
import game.ui.WindowManager;

public class GameApp {
    
    // Game Launch - Run file to launch PixelPursuit
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WindowManager windowManager = new WindowManager();
            windowManager.showLoginWindow();
        });
    }
}