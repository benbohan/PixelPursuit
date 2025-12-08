package game.ui.components.controls;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Rounded text input field for username and simple text entries:
 *  - Draws a semi-transparent dark rounded rectangle behind the text.
 *  - Adds inner padding so the text doesn’t sit against the edges.
 *  - Uses a simple gray rounded border to stay consistent with other inputs.
 */
public class RoundedTextField extends JTextField {

    private static final long serialVersionUID = 1L;

    // ---------- CONSTANTS ----------

    private static final int ARC = 18;                         // corner radius for rounded rectangle
    private static final Color FIELD_FILL   = new Color(0, 0, 0, 140); // semi-transparent dark fill
    private static final Color FIELD_BORDER = Color.DARK_GRAY;         // border color

    // ---------- CONSTRUCTORS ----------

    // RoundedTextField - Creates a rounded text field with internal padding and custom painting
    public RoundedTextField() {
        super();
        setOpaque(false);
        // Inner padding so text does not hug the rounded border
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    // ---------- PAINTING ----------

    // paintComponent - Fills the rounded background, then lets JTextField draw the text and caret
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Rounded filled background behind the text
        g2.setColor(FIELD_FILL);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);

        g2.dispose();
        // Let the default text field paint the text and caret on top
        super.paintComponent(g);
    }

    // paintBorder - Draws a rounded outline around the text field
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
