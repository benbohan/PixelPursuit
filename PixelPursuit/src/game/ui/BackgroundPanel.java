package game.ui;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
	private static final long serialVersionUID = 1L;
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        // imagePath should look like "/game/resources/background.png"
        java.net.URL imgURL = getClass().getResource(imagePath);
        if (imgURL != null) {
            backgroundImage = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Could not find background image: " + imagePath);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
