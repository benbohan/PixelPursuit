package game.ui.components.controls;

import game.ui.theme.GameFonts;

import java.awt.*;

/**
 * Small circular "i" button for opening the info / rules window.
 * - Reuses RoundedHoverButton styling so it matches the rest of the UI.
 */
public class InfoButton extends RoundedHoverButton {

    private static final long serialVersionUID = 1L;

    public InfoButton() {
        super("i");

        // Small, almost circular button
        Dimension d = new Dimension(72, 72);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);

        setFont(GameFonts.get(18f, Font.BOLD));
        setFocusPainted(false);
        setToolTipText("How to play");
    }
}
