package game.ui;

import javax.swing.JButton;
import java.awt.*;

/**
 * Rounded white button with light gray border and a subtle hover "grow" effect.
 */
public class RoundedHoverButton extends JButton {

    private static final long serialVersionUID = 1L;

    private static final int ARC = 22;
    public static final float IDLE_SCALE = 0.9f;  // used in LogInWindow sizing math

    private static final Color BUTTON_FILL   = new Color(255, 255, 255);      // white
    private static final Color BUTTON_BORDER = BUTTON_FILL.darker();         // light gray

    public RoundedHoverButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        float scale = getModel().isRollover() ? 1.0f : IDLE_SCALE;

        int scaledW = (int) (w * scale);
        int scaledH = (int) (h * scale);
        int x = (w - scaledW) / 2;
        int y = (h - scaledH) / 2;

        // Fill
        g2.setColor(BUTTON_FILL);
        g2.fillRoundRect(x, y, scaledW - 1, scaledH - 1, ARC, ARC);

        // Border
        g2.setColor(BUTTON_BORDER);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, ARC, ARC);

        // Text
        FontMetrics fm = g2.getFontMetrics(getFont());
        String text = getText();
        int textWidth  = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int tx = (w - textWidth) / 2;
        int ty = (h + textHeight) / 2 - fm.getDescent();

        g2.setColor(Color.BLACK);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Border already painted in paintComponent
    }
}
