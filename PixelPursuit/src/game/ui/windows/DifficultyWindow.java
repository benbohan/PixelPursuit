package game.ui.windows;

import game.settings.Difficulty;
import game.settings.GameConfig;
import game.ui.WindowManager;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DifficultyWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final WindowManager windowManager;

    public DifficultyWindow(WindowManager windowManager) {
        super("Settings");
        this.windowManager = windowManager;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        Color bgColor = new Color(30, 30, 30);

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(20, 40, 24, 40));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(true);
        content.setBackground(bgColor);
        setContentPane(content);

        Difficulty current = GameConfig.getCurrentDifficulty();
        Difficulty[] options = Difficulty.values();

        // ----- Title -----
        JLabel title = new JLabel("Settings");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(GameFonts.get(28f, Font.BOLD));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Select difficulty");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setFont(GameFonts.get(18f, Font.PLAIN));
        subtitle.setForeground(Color.WHITE);

        JLabel hint = new JLabel("Harder runs pay out more final gold.");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setFont(GameFonts.get(14f, Font.PLAIN));
        hint.setForeground(Color.WHITE);

        content.add(title);
        content.add(Box.createRigidArea(new Dimension(0, 6)));
        content.add(subtitle);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.add(hint);
        content.add(Box.createRigidArea(new Dimension(0, 16)));

        // ----- Difficulty buttons -----
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));

        JLabel statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(GameFonts.get(14f, Font.PLAIN));
        statusLabel.setForeground(new Color(150, 255, 150));

        for (Difficulty d : options) {
            String labelText = d.getDisplayName();

            RoundedHoverButton diffButton = new RoundedHoverButton(labelText);
            diffButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            diffButton.setFont(GameFonts.get(18f, Font.BOLD));
            diffButton.setMaximumSize(new Dimension(260, 48));

            diffButton.addActionListener(e -> {
                GameConfig.setCurrentDifficulty(d);
                statusLabel.setText("Difficulty set to " + d.getDisplayName());
            });

            buttonsPanel.add(diffButton);
            buttonsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        content.add(buttonsPanel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(statusLabel);
        content.add(Box.createRigidArea(new Dimension(0, 18)));

        // ----- Back button -----
        RoundedHoverButton backButton = new RoundedHoverButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(GameFonts.get(18f, Font.BOLD));
        backButton.setMaximumSize(new Dimension(200, 48));

        backButton.addActionListener(e -> dispose());

        content.add(backButton);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
