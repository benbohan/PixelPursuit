package game.ui.components.controls;

import javax.swing.*;
import java.awt.*;

public class ColorTileButton extends JButton {

    private static final long serialVersionUID = 1L;

    // ---------- STATE ----------

    private boolean selected = false;     // selected color tile
    private boolean locked   = false;     // locked color tile
    private final Color fill;            // fill tile color 

    // ---------- CONSTRUCTORS ----------

    // ColorTileButton - Creates a solid color tile-style button with custom painting
    public ColorTileButton(Color fill) {
        this.fill = fill;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    // ---------- LOCK / SELECTION API ----------

    // setSelected - Marks this tile as selected and repaints
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    // setLocked - Locks or unlocks this tile and repaints
    public void setLocked(boolean locked) {
        this.locked = locked;
        repaint();
    }

    // isLocked - Returns true if this tile is locked
    public boolean isLocked() {
        return locked;
    }

    // ---------- PAINTING ----------

    // paintComponent - Draws the base color, selection border, and lock overlay
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 26;
        int w = getWidth();
        int h = getHeight();

        // Base rounded color fill
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Thick white border when selected
        if (selected) {
            g2.setStroke(new BasicStroke(5f));
            g2.setColor(Color.WHITE);
            g2.drawRoundRect(2, 2, w - 5, h - 5, arc, arc);
        }

        // Semi-transparent overlay when locked
        if (locked) {
            g2.setColor(new Color(0, 0, 0, 140)); // darken tile to indicate locked
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }

        g2.dispose();
    }

    // paintBorder - Disabled; border drawn manually in paintComponent
    @Override
    protected void paintBorder(Graphics g) { /* no-op */ }
}
