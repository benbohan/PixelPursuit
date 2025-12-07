package game.ui.components.controls;

import game.ui.theme.GameFonts;

import javax.swing.*;
import java.awt.*;

public class TileButton extends JButton {

    private static final long serialVersionUID = 1L;

    private boolean selected = false;
    private boolean locked   = false;

    public TileButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(new Color(255, 255, 255, text.isEmpty() ? 0 : 220));
        setFont(GameFonts.get(18f, Font.BOLD));
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

        // background tile
        g2.setColor(new Color(210, 210, 210));
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        // border
        g2.setColor(selected ? Color.WHITE : new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(selected ? 5f : 2f));
        g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);

        // icon (for cosmetics)
        Icon icon = getIcon();
        if (icon != null) {
            int iw = icon.getIconWidth();
            int ih = icon.getIconHeight();
            int ix = (w - iw) / 2;
            int iy = (h - ih) / 2;
            icon.paintIcon(this, g2, ix, iy);
        }

        // text (for multipliers / "None")
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

        // locked overlay
        if (locked) {
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) { /* no-op */ }
}
