package game.ui.windows;

import game.account.AccountManager;
import game.persistence.Leaderboard;
import game.persistence.LeaderboardEntry;
import game.ui.WindowManager;
import game.ui.theme.GameFonts;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Standalone leaderboard window for Pixel Pursuit.
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
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // ----- LOAD DATA -----
        AccountManager am = (windowManager != null)
                ? windowManager.getAccountManager()
                : new AccountManager(); // fallback if ever used standalone

        Leaderboard lb = new Leaderboard();
        List<LeaderboardEntry> entries = lb.buildFromAccounts(am.getAllAccounts(), 10);

        // ----- BUILD UI -----
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 40, 20, 40));
        panel.setOpaque(true);
        panel.setBackground(new Color(30, 30, 30));

        JLabel title = new JLabel("Longest Runs");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(28f, Font.BOLD));
        title.setForeground(Color.WHITE);
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        if (entries.isEmpty()) {
            JLabel empty = new JLabel("No runs recorded yet.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.setFont(GameFonts.get(20f, Font.PLAIN));
            empty.setForeground(Color.LIGHT_GRAY);
            panel.add(empty);
        } else {
            int rank = 1;
            for (LeaderboardEntry entry : entries) {
                String timeStr = formatTime(entry.getBestTimeSeconds());
                String rowText = String.format("%2d. %-12s  %s",
                        rank, entry.getUsername(), timeStr);

                JLabel row = new JLabel(rowText);
                row.setAlignmentX(Component.CENTER_ALIGNMENT);
                row.setFont(GameFonts.get(20f, Font.PLAIN));
                row.setForeground(Color.WHITE);
                panel.add(row);
                panel.add(Box.createRigidArea(new Dimension(0, 6)));

                rank++;
            }
        }

        panel.add(Box.createRigidArea(new Dimension(0, 16)));

        // Buttons row
        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));

        JButton close = new JButton("Close");
        close.setFont(GameFonts.get(18f, Font.BOLD));
        close.addActionListener(e -> {
            // Just close this window
            dispose();
            // Optionally go back to main menu via windowManager if you want:
            // if (windowManager != null) windowManager.showMainMenu();
        });

        buttonRow.add(Box.createHorizontalGlue());
        buttonRow.add(close);
        buttonRow.add(Box.createHorizontalGlue());

        panel.add(buttonRow);

        setContentPane(panel);
        setVisible(true);
    }

    /** Simple mm:ss formatter for leaderboard rows. */
    private String formatTime(double seconds) {
        int total = (int) Math.floor(seconds);
        int mins = total / 60;
        int secs = total % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
