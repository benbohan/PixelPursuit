package game.ui.windows;

import game.account.AccountManager;
import game.persistence.Leaderboard;
import game.persistence.LeaderboardEntry;
import game.ui.WindowManager;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Leaderboard window showing longest runs (best time in seconds).
 * Theme: black background + white pixel font, like other dialogs.
 */
public class LeaderboardWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final WindowManager windowManager;

    public LeaderboardWindow(WindowManager windowManager) {
        super("Pixel Pursuit - Leaderboard");
        this.windowManager = windowManager;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // ----- LOAD DATA -----
        AccountManager am = (windowManager != null)
                ? windowManager.getAccountManager()
                : new AccountManager(); // fallback if ever used standalone

        Leaderboard lb = new Leaderboard();
        List<LeaderboardEntry> entries = lb.buildFromAccounts(am.getAllAccounts(), 10);

        // Debug – see what the leaderboard actually has
        System.out.println("=== LEADERBOARD ENTRIES ===");
        for (LeaderboardEntry e : entries) {
            System.out.println("  " + e.getUsername() + "  best=" + e.getBestTimeSeconds());
        }

        // ----- ROOT PANEL -----
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(20, 40, 20, 40));
        root.setOpaque(true);
        root.setBackground(new Color(30, 30, 30));

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

                // Format strictly as mm:ss, no placeholders
                String timeStr = formatTime(bestSeconds);

                // Also log the exact row text we use
                String rowText = String.format("%2d. %-12s  %s",
                        rank, entry.getUsername(), timeStr);
                System.out.println("LB ROW: \"" + rowText + "\"");

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

        JButton close = new JButton("Close");
        close.setFont(GameFonts.get(18f, Font.BOLD));
        close.addActionListener(e -> dispose());

        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(close);
        buttonRow.add(Box.createHorizontalGlue());

        root.add(buttonRow);

        setContentPane(root);
        setVisible(true);
    }

    /** Format seconds as mm:ss. Never returns "...". */
    private String formatTime(double seconds) {
        if (seconds <= 0) {
            // if you prefer blank, change to "  --:--"
            return "00:00";
        }
        int total = (int) Math.floor(seconds);
        int mins  = total / 60;
        int secs  = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
