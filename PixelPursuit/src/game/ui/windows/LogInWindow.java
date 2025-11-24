package game.ui.windows;

import game.account.*;
import game.ui.*;
import game.ui.components.*;
import game.ui.components.controls.RoundedHoverButton;
import game.ui.components.controls.RoundedPasswordField;
import game.ui.components.controls.RoundedTextField;
import game.ui.components.panels.BackgroundPanel;
import game.ui.theme.GameFonts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Login window for Pixel Pursuit.
 */
public class LogInWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private final AccountManager accountManager;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private Dimension fieldSize;
    private int buttonHeight;
    private int screenHeight;
    private int screenWidth;

    public LogInWindow() {
        super("Pixel Pursuit - Login");

        accountManager = new AccountManager();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);

        // ---------- SCREEN-BASED SIZING ----------
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth  = screenSize.width;
        screenHeight = screenSize.height;

        fieldSize    = new Dimension(screenWidth / 4, screenHeight / 16);
        buttonHeight = (int) (fieldSize.height * 0.8);

        // ---------- BACKGROUND ----------
        JPanel mainPanel = new BackgroundPanel("/game/resources/images/loginBackground.png");
        mainPanel.setLayout(new GridBagLayout());
        add(mainPanel);

        // ---------- LOGIN PANEL ----------
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setOpaque(false);
        loginPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel loginLabel = new JLabel("Login or Create Account");
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setFont(GameFonts.get(screenHeight / 30f, Font.BOLD));
        loginLabel.setForeground(Color.WHITE);

        // Rounded text fields
        usernameField = new RoundedTextField();
        passwordField = new RoundedPasswordField();
        styleTextField(usernameField);
        styleTextField(passwordField);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        int labelFontSize = Math.max(16, Math.min(screenHeight / 40, 26));
        userLabel.setFont(GameFonts.get((float) labelFontSize, Font.PLAIN));
        passLabel.setFont(GameFonts.get((float) labelFontSize, Font.PLAIN));
        userLabel.setForeground(Color.WHITE);
        passLabel.setForeground(Color.WHITE);

        userLabel.setHorizontalAlignment(SwingConstants.LEFT);
        passLabel.setHorizontalAlignment(SwingConstants.LEFT);

        // Center rows as a group
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        RoundedHoverButton loginButton  = createMenuButton("Log In");
        RoundedHoverButton createButton = createMenuButton("Create Account");
        RoundedHoverButton exitButton   = createMenuButton("Exit");

        messageLabel = new JLabel(" ");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(GameFonts.get(screenHeight / 45f, Font.PLAIN));

        // ---------- LAYOUT MATH ----------

        // 1) Shrink gap between labels and fields
        int labelGap  = Math.max(8, screenHeight / 120);
        int buttonGap = Math.max(10, screenHeight / 80);

        // Give labels a fixed width so rows align nicely
        int labelWidth = screenWidth / 8;  // tweak if needed
        Dimension labelSize = new Dimension(labelWidth, userLabel.getPreferredSize().height);
        userLabel.setPreferredSize(labelSize);
        userLabel.setMinimumSize(labelSize);
        userLabel.setMaximumSize(labelSize);
        passLabel.setPreferredSize(labelSize);
        passLabel.setMinimumSize(labelSize);
        passLabel.setMaximumSize(labelSize);

        // Total width of "Username: [box]" row
        int rowWidth = labelWidth + labelGap + fieldSize.width;

        // 2) Make login + gap + create visually (0.9 scale) == rowWidth
        float idleScale = RoundedHoverButton.IDLE_SCALE;  // 0.9f
        int loginButtonWidth = Math.round((rowWidth - buttonGap) / (2 * idleScale));
        Dimension loginButtonSize = new Dimension(loginButtonWidth, buttonHeight);
        loginButton.setPreferredSize(loginButtonSize);
        loginButton.setMaximumSize(loginButtonSize);
        loginButton.setMinimumSize(loginButtonSize);
        createButton.setPreferredSize(loginButtonSize);
        createButton.setMaximumSize(loginButtonSize);
        createButton.setMinimumSize(loginButtonSize);

        // Exit wider for visual balance
        int exitPhysicalWidth = (int) Math.round(rowWidth * 1.175);
        Dimension exitSize = new Dimension(exitPhysicalWidth, buttonHeight);
        exitButton.setPreferredSize(exitSize);
        exitButton.setMaximumSize(exitSize);
        exitButton.setMinimumSize(exitSize);

        // ---------- BUILD LAYOUT ----------

        loginPanel.add(loginLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 80)));

        // Username row
        JPanel userRow = new JPanel();
        userRow.setOpaque(false);
        userRow.setLayout(new BoxLayout(userRow, BoxLayout.X_AXIS));
        userRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        userRow.add(userLabel);
        userRow.add(Box.createRigidArea(new Dimension(labelGap, 0)));
        userRow.add(usernameField);

        loginPanel.add(userRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 120)));

        // Password row
        JPanel passRow = new JPanel();
        passRow.setOpaque(false);
        passRow.setLayout(new BoxLayout(passRow, BoxLayout.X_AXIS));
        passRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        passRow.add(passLabel);
        passRow.add(Box.createRigidArea(new Dimension(labelGap, 0)));
        passRow.add(passwordField);

        loginPanel.add(passRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 80)));

        // Buttons row: Log In + Create Account side by side
        JPanel buttonRow = new JPanel();
        buttonRow.setOpaque(false);
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonRow.add(loginButton);
        buttonRow.add(Box.createRigidArea(new Dimension(buttonGap, 0)));
        buttonRow.add(createButton);

        loginPanel.add(buttonRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 100)));

        // Exit row (centered, wider)
        JPanel exitRow = new JPanel();
        exitRow.setOpaque(false);
        exitRow.setLayout(new BoxLayout(exitRow, BoxLayout.X_AXIS));
        exitRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitRow.add(exitButton);

        loginPanel.add(exitRow);
        loginPanel.add(Box.createRigidArea(new Dimension(0, screenHeight / 100)));

        loginPanel.add(messageLabel);

        // Place the whole login panel slightly down from the top
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(screenHeight / 22, 0, 0, 0);
        mainPanel.add(loginPanel, gbc);

        // ---------- BUTTON ACTIONS ----------
        loginButton.addActionListener(e -> handleLogin());
        createButton.addActionListener(e -> handleCreateAccount());
        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    // ---------- logic ----------

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setMessage("Enter username and password.", Color.RED);
            return;
        }

        Account acc = accountManager.login(username, password);
        if (acc == null) {
            setMessage("Invalid username or password.", Color.RED);
        } else {
            setMessage("Login successful!", new Color(126, 217, 87));
            openMainMenu(acc);
        }
    }

    private void handleCreateAccount() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setMessage("Enter username and password.", Color.RED);
            return;
        }

        Account acc = accountManager.createAccount(username, password);
        if (acc == null) {
            setMessage("Username already taken.", Color.RED);
        } else {
            setMessage("Account created! Logging in...", new Color(126, 217, 87));
            openMainMenu(acc);
        }
    }

    private void openMainMenu(Account acc) {
        this.dispose();
        new MainMenuWindow(acc);
    }

    private void setMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setForeground(color);
    }

    // ---------- styling helpers ----------

    private void styleTextField(JTextField field) {
        field.setPreferredSize(fieldSize);
        field.setMaximumSize(fieldSize);
        field.setMinimumSize(fieldSize);

        int fontSize = screenHeight / 32;
        fontSize = Math.max(18, Math.min(fontSize, 30));
        field.setFont(GameFonts.get((float) fontSize, Font.PLAIN));

        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        // padding handled by RoundedTextField / RoundedPasswordField
    }

    private RoundedHoverButton createMenuButton(String text) {
        RoundedHoverButton button = new RoundedHoverButton(text);
        int buttonFontSize = Math.max(18, Math.min(screenHeight / 34, 30));
        button.setFont(GameFonts.get((float) buttonFontSize, Font.BOLD));
        return button;
    }
}