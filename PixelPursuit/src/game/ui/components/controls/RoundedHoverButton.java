package game.ui.components.controls;
import game.ui.theme.GameFonts;

import javax.swing.*;
import java.awt.*;

/**
 * Rounded UI button with a subtle hover grow effect:
 *  - Renders a white rounded rectangle with a light gray border.
 *  - Slightly scales up on hover, using a smaller idle scale the rest of the time.
 *  - Provides helpers to build fixed-size menu buttons and two-button rows for layouts.
 */
public class RoundedHoverButton extends JButton {

    private static final long serialVersionUID = 1L;

    // ---------- CONSTANTS ----------

    private static final int ARC = 22;                         // corner radius for rounded button
    public static final float IDLE_SCALE = 0.9f;               // default scale when not hovered
    private static final Color BUTTON_FILL   = new Color(255, 255, 255);  // white fill
    private static final Color BUTTON_BORDER = BUTTON_FILL.darker();      // light gray border

    // ---------- CONSTRUCTORS ----------

    // RoundedHoverButton - Creates a rounded button with hover "grow" effect
    public RoundedHoverButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    // ---------- FACTORY HELPERS ----------

    // createMenuButton - Builds a fixed-size menu button with responsive font size
    public static RoundedHoverButton createMenuButton(
            String text,
            int width,
            int height,
            int screenHeight
    ) {
        RoundedHoverButton button = new RoundedHoverButton(text);

        // Center button in BoxLayout rows/columns
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Lock preferred/maximum/minimum size to the same value
        Dimension size = new Dimension(width, height);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);

        // Scale font roughly with screen height, but clamp to a reasonable range
        int fontSize = Math.max(18, Math.min(screenHeight / 34, 30));
        button.setFont(GameFonts.get((float) fontSize, Font.BOLD));

        return button;
    }

    // createButtonRow - Creates a horizontal row with two buttons and a fixed gap
    public static JPanel createButtonRow(
            RoundedHoverButton left,
            RoundedHoverButton right,
            int gap
    ) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        // [left button] --gap-- [right button]
        row.add(left);
        row.add(Box.createRigidArea(new Dimension(gap, 0)));
        row.add(right);

        return row;
    }

    // ---------- PAINTING ----------

    // paintComponent - Draws scaled rounded background, border, and centered label text
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Grow button slightly on hover, shrink a bit when idle
        float scale = getModel().isRollover() ? 1.0f : IDLE_SCALE;

        int scaledW = (int) (w * scale);
        int scaledH = (int) (h * scale);
        int x = (w - scaledW) / 2;
        int y = (h - scaledH) / 2;

        // Fill rounded background
        g2.setColor(BUTTON_FILL);
        g2.fillRoundRect(x, y, scaledW - 1, scaledH - 1, ARC, ARC);

        // Draw rounded border
        g2.setColor(BUTTON_BORDER);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(x + 1, y + 1, scaledW - 3, scaledH - 3, ARC, ARC);

        // Centered button label text
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

    // paintBorder - Disabled; border is handled in paintComponent
    @Override
    protected void paintBorder(Graphics g) { /* no-op */ }
}
