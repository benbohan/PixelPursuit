package game.ui.windows;

import game.ui.WindowManager;
import game.ui.theme.GameFonts;
import game.ui.components.controls.RoundedHoverButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * PauseWindow - Dialog shown when the player pauses the game.
 *  - Freezes gameplay by leaving GameWindow in a paused state.
 *  - Lets the player resume the run or return to the main menu.
 */
public class PauseWindow extends JDialog {

    private static final long serialVersionUID = 1L;

    // ---------- FIELDS ----------

    /** Owner game window for pause/resume callbacks. */
    private final GameWindow gameWindow;

    /** Window manager, kept for consistency with other windows. */
    //private final WindowManager windowManager;

    // ---------- CONSTRUCTORS ----------

    // PauseWindow - Builds a modal dialog owned by the running GameWindow.
    public PauseWindow(GameWindow owner, WindowManager windowManager) {
        // true = modal; blocks input to the game window while open
        super(owner, "Paused", true);
        this.gameWindow = owner;
        //this.windowManager = windowManager;

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        buildUi();
        pack();

        int minW = 420;
        int minH = 260;
        setSize(Math.max(getWidth(), minW), Math.max(getHeight(), minH));

        setLocationRelativeTo(owner);
        installEscToResume();
    }

    // ---------- UI BUILD ----------

    // buildUi - Constructs the pause dialog layout and wires button actions.
    private void buildUi() {
        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);

        JLabel titleLabel = new JLabel("Game Paused");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(GameFonts.get(28f, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 24)));

        RoundedHoverButton resumeBtn = new RoundedHoverButton("Resume");
        RoundedHoverButton menuBtn   = new RoundedHoverButton("Menu");

        Dimension buttonSize = new Dimension(260, 60);
        for (JButton b : new JButton[] { resumeBtn, menuBtn }) {
            b.setPreferredSize(buttonSize);
            b.setMinimumSize(buttonSize);
            b.setMaximumSize(buttonSize);
            b.setFont(GameFonts.get(22f, Font.BOLD));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        content.add(resumeBtn);
        content.add(Box.createRigidArea(new Dimension(0, 14)));
        content.add(menuBtn);

        setContentPane(content);

        // Resume - unpause the game and close the dialog.
        resumeBtn.addActionListener(e -> {
            gameWindow.resumeGameFromPause();
            dispose();
        });

        // Menu - ask GameWindow to exit to main menu, then close dialog.
        menuBtn.addActionListener(e -> {
            gameWindow.returnToMenuFromPause();
            dispose();
        });
    }

    // ---------- KEYBINDINGS ----------

    // installEscToResume - Binds ESC inside the pause dialog to act like the Resume button.

    private void installEscToResume() {
        getRootPane().registerKeyboardAction(
            e -> {
                gameWindow.resumeGameFromPause();
                dispose();
            },
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
}
