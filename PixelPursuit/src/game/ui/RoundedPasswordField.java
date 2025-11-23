package game.ui;

import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Rounded password field matching RoundedTextField styling.
 */
public class RoundedPasswordField extends JPasswordField {

    private static final long serialVersionUID = 1L;

    private static final int ARC = 18;
    private static final Color FIELD_FILL   = new Color(0, 0, 0, 140);
    private static final Color FIELD_BORDER = Color.DARK_GRAY;

    public RoundedPasswordField() {
        super();
        setOpaque(false);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(FIELD_FILL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(FIELD_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
    }
}
