package game.ui.components.panels;

import javax.swing.*;
import java.awt.*;

/**
 * BackgroundPanel - JPanel that draws a scaled background image behind its contents.
 */
public class BackgroundPanel extends JPanel {

    // ---------- FIELDS ----------

    private static final long serialVersionUID = 1L;
    private Image backgroundImage;

    // ---------- CONSTRUCTORS ----------

    // BackgroundPanel - Loads the background image from the given classpath path
    public BackgroundPanel(String imagePath) {
        java.net.URL imgURL = getClass().getResource(imagePath);
        if (imgURL != null) {
            backgroundImage = new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Could not find background image: " + imagePath);
        }
    }

    // ---------- PAINTING ----------

    // paintComponent - Draws the background image scaled to fill the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
