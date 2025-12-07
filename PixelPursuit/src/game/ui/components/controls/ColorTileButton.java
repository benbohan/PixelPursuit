package game.ui.components.controls;

import javax.swing.*;
import java.awt.*;

public class ColorTileButton extends JButton {

    private static final long serialVersionUID = 1L;

    private boolean selected = false;
    private boolean locked   = false;
    private final Color fill;

    public ColorTileButton(Color fill) {
        this.fill = fill;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        repaint();
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 26;
        int w = getWidth();
        int h = getHeight();

        // base color
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // selection border
        if (selected) {
            g2.setStroke(new BasicStroke(5f));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(2, 2, w - 5, h - 5, arc, arc);
        }

        // locked overlay
        if (locked) {
            g2.setColor(new Color(0, 0, 0, 140)); // semi–transparent black
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) { /* no-op */ }
}
