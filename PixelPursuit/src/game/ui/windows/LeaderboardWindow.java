package game.ui.windows;

import game.account.AccountManager;
import game.persistence.Leaderboard;
import game.persistence.LeaderboardEntry;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard window:
 *  - Shows top runs ordered by best time (longest survival).
 *  - Uses a dark background + white pixel font, matching other dialogs.
 *  - Reads account data through AccountManager and formats as mm:ss rows.
 */
public class LeaderboardWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    // ---------- CONSTANTS ----------

    private static final Color BG_COLOR = new Color(30, 30, 30);
    private static final Dimension CLOSE_BUTTON_SIZE = new Dimension(200, 48);

    // ---------- FIELDS ----------

    private final WindowManager windowManager;

    // ---------- CONSTRUCTORS ----------

    // LeaderboardWindow - Builds and shows the leaderboard dialog
    public LeaderboardWindow(WindowManager windowManager) {
        super("Pixel Pursuit - Leaderboard");
        this.windowManager = windowManager;
        initUI();
    }

    // ---------- UI SETUP ----------

    // initUI - Lays out the leaderboard content and wires the Close button
    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Slightly wider + taller so long usernames / times fit comfortably
        setSize(700, 460);
        setLocationRelativeTo(null);
        setResizable(false);

        // ----- LOAD DATA -----
        AccountManager am = (windowManager != null)
                ? windowManager.getAccountManager()
                : new AccountManager(); // fallback if ever used standalone

        Leaderboard lb = new Leaderboard();
        List<LeaderboardEntry> entries = lb.buildFromAccounts(am.getAllAccounts(), 10);

        // ----- ROOT PANEL -----
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(20, 40, 20, 40));
        root.setOpaque(true);
        root.setBackground(BG_COLOR);

        // Title
        JLabel title = new JLabel("Longest Runs");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(32f, Font.BOLD));
        title.setForeground(Color.WHITE);
        root.add(title);
        root.add(Box.createRigidArea(new Dimension(0, 16)));

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No runs recorded yet.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setFont(GameFonts.get(20f, Font.PLAIN));
            empty.setForeground(Color.LIGHT_GRAY);
            root.add(empty);
        } else {
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                double bestSeconds = entry.getBestTimeSeconds();

                // Format strictly as mm:ss
                String timeStr = formatTime(bestSeconds);

                String rowText = String.format("%2d. %-18s  %s",
                        rank, entry.getUsername(), timeStr);

                JLabel row = new JLabel(rowText);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                row.setFont(GameFonts.get(20f, Font.PLAIN));
                row.setForeground(Color.WHITE);
                root.add(row);
                root.add(Box.createRigidArea(new Dimension(0, 6)));

                rank++;
            }
        }

        root.add(Box.createRigidArea(new Dimension(0, 18)));

        // ----- BUTTON ROW -----
        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));

        RoundedHoverButton close = new RoundedHoverButton("Close");
        close.setAlignmentX(Component.CENTER_ALIGNMENT);
        close.setFont(GameFonts.get(18f, Font.BOLD));
        close.setMaximumSize(CLOSE_BUTTON_SIZE);
        close.setPreferredSize(CLOSE_BUTTON_SIZE);
        close.setMinimumSize(CLOSE_BUTTON_SIZE);
        close.addActionListener(e -> dispose());

        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(close);
        buttonRow.add(Box.createHorizontalGlue());

        root.add(buttonRow);

        setContentPane(root);
        setVisible(true);
    }

    // ---------- FORMAT HELPERS ----------

    // formatTime - Converts seconds into mm:ss, returning "00:00" for non-positive values
    private String formatTime(double seconds) {
        if (seconds <= 0) {
            return "00:00";
        }
        int total = (int) Math.floor(seconds);
        int mins  = total / 60;
        int secs  = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
