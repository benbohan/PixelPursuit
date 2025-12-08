package game.ui.components.controls;

import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Rounded password input field for login dialogs:
 *  - Draws a semi-transparent dark rounded rectangle as the background.
 *  - Adds inner padding so text doesn’t sit against the edge.
 *  - Uses a simple gray rounded border to match the game’s UI theme.
 */
public class RoundedPasswordField extends JPasswordField {

    private static final long serialVersionUID = 1L;

    // ---------- CONSTANTS ----------

    private static final int ARC = 18;                         // corner radius for rounded rectangle
    private static final Color FIELD_FILL   = new Color(0, 0, 0, 140); // semi-transparent dark fill
    private static final Color FIELD_BORDER = Color.DARK_GRAY;         // border color

    // ---------- CONSTRUCTORS ----------

    // RoundedPasswordField - Creates a rounded password field with padding and custom painting
    public RoundedPasswordField() {
        super();
        setOpaque(false);
        // Inner padding so text does not hug the rounded border
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    // ---------- PAINTING ----------

    // paintComponent - Fills the rounded background, then lets JPasswordField draw text/caret
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Rounded filled background behind the password text
        g2.setColor(FIELD_FILL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
        // Let the default component paint the password text and caret on top
        super.paintComponent(g);
    }

    // paintBorder - Draws a rounded outline around the password field
    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Rounded border around the field
        g2.setColor(FIELD_BORDER);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
    }
}
