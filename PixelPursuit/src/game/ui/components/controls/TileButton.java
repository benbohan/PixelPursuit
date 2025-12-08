package game.ui.components.controls;

import game.ui.theme.GameFonts;

import javax.swing.*;
import java.awt.*;

/**
 * Tile-style UI button for the customize menu:
 *  - Draws a rounded, light gray tile with centered icon or text.
 *  - Highlights with a thicker, brighter border when selected.
 *  - Can show a dark overlay when locked to indicate unavailable cosmetics.
 */
public class TileButton extends JButton {

    private static final long serialVersionUID = 1L;

    // ---------- STATE ----------

    private boolean selected = false;  // whether this tile is currently selected
    private boolean locked   = false;  // whether this tile is locked (overlay shown)

    // ---------- CONSTRUCTORS ----------

    // TileButton - Creates a rounded, transparent tile-style button with game font
    public TileButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        // Slightly translucent white text when non-empty
        setForeground(new Color(255, 255, 255, text.isEmpty() ? 0 : 220));
        setFont(GameFonts.get(18f, Font.BOLD));
    }

    // ---------- LOCK / SELECTION API ----------

    // setSelected - Marks this tile as the currently selected one and repaints
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    // setLocked - Locks or unlocks the tile and repaints
    public void setLocked(boolean locked) {
        this.locked = locked;
        repaint();
    }

    // isLocked - Returns true if this tile is locked
    public boolean isLocked() {
        return locked;
    }

    // ---------- PAINTING ----------

    // paintComponent - Custom drawing for tile background, border, content, and lock overlay
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 26;
        int w = getWidth();
        int h = getHeight();

        // Base rounded background
        g2.setColor(new Color(210, 210, 210));
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // Border that thickens and brightens when selected
        g2.setColor(selected ? Color.WHITE : new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(selected ? 5f : 2f));
        g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

        // Centered icon (used for cosmetics)
        Icon icon = getIcon();
        if (icon != null) {
            int iw = icon.getIconWidth();
            int ih = icon.getIconHeight();
            int ix = (w - iw) / 2;
            int iy = (h - ih) / 2;
            icon.paintIcon(this, g2, ix, iy);
        }

        // Centered text (used for multipliers or "None")
        String text = getText();
        if (text != null && !text.isEmpty()) {
            FontMetrics fm = g2.getFontMetrics(getFont());
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();
            int tx = (w - tw) / 2;
            int ty = (h + th) / 2 - fm.getDescent();
            g2.setColor(getForeground());
            g2.drawString(text, tx, ty);
        }

        // Semi-transparent overlay when locked
        if (locked) {
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }

        g2.dispose();
    }

    // paintBorder - Disabled; border is fully handled in paintComponent
    @Override
    protected void paintBorder(Graphics g) { /* no-op */ }
}
