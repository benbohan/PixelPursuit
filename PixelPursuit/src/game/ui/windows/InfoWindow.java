package game.ui.windows;

import game.account.Account;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * InfoWindow:
 *  - Small dark-theme window that explains how to play Pixel Pursuit.
 *  - Opened from the main menu via the InfoButton in the bottom-right.
 */
public class InfoWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    //private final WindowManager windowManager;
    //private final Account account;

    // InfoWindow - Main constructor, wired from WindowManager
    public InfoWindow(WindowManager windowManager, Account account) {
        super("How to Play");
        //this.windowManager = windowManager;
        //this.account = account;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        Color bgColor = new Color(30, 30, 30);

        // ---------- ROOT PANEL ----------

        JPanel content = new JPanel(new BorderLayout(16, 16));
        content.setBorder(new EmptyBorder(20, 24, 20, 24));
        content.setOpaque(true);
        content.setBackground(bgColor);
        setContentPane(content);

        // ---------- TITLE ----------

        JLabel titleLabel = new JLabel("How to Play");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(GameFonts.get(24f, Font.BOLD));
        titleLabel.setForeground(Color.WHITE);

        content.add(titleLabel, BorderLayout.NORTH);

        // ---------- BODY TEXT ----------

        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setFocusable(false);
        infoText.setOpaque(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setForeground(Color.WHITE);
        infoText.setFont(GameFonts.get(16f, Font.PLAIN));

        infoText.setText(
                "• Run as far as you can while dodging obstacles.\n\n" +
                "• Collect gold and diamonds during each run.\n" +
                "    - Gold is used to unlock new colors and cosmetics.\n" +
                "    - Diamonds are used to unlock score multipliers.\n\n" +
                "• Difficulty affects how fast the game plays and how much loot you earn.\n\n" +
                "• In the Customize menu you can:\n" +
                "    - Change your runner color.\n" +
                "    - Equip cosmetics you’ve unlocked.\n" +
                "    - Equip any multiplier you’ve purchased.\n\n" +
                "• Your vault shows your total gold and diamonds across all runs.\n\n" +
                "Tip: Start on an easier difficulty to learn the timing, then move up " +
                "for better rewards once you’re comfortable."
        );

        JScrollPane scrollPane = new JScrollPane(
                infoText,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        content.add(scrollPane, BorderLayout.CENTER);

        // ---------- CLOSE BUTTON ----------

        RoundedHoverButton closeButton = new RoundedHoverButton("Close");
        Dimension btnSize = new Dimension(140, 48);
        closeButton.setPreferredSize(btnSize);
        closeButton.setMinimumSize(btnSize);
        closeButton.setMaximumSize(btnSize);
        closeButton.setFont(GameFonts.get(18f, Font.BOLD));

        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new BoxLayout(bottomBar, BoxLayout.X_AXIS));
        bottomBar.setOpaque(false);
        bottomBar.add(Box.createHorizontalGlue());
        bottomBar.add(closeButton);
        bottomBar.add(Box.createHorizontalGlue());

        content.add(bottomBar, BorderLayout.SOUTH);

        closeButton.addActionListener(e -> dispose());

        // ---------- SIZE & POSITION ----------

        pack();
        int minW = 800;
        int minH = 550;
        setSize(Math.max(getWidth(), minW), Math.max(getHeight(), minH));
        setLocationRelativeTo(null);

        setVisible(true);
    }

    // Convenience constructor when there is no WindowManager wired
    public InfoWindow(Account account) {
        this(null, account);
    }
}
