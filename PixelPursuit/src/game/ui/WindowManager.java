package game.ui;

import game.account.Account;
import game.account.AccountManager;
import game.ui.windows.*;

import javax.swing.*;

public class WindowManager {

    private final AccountManager accountManager;
    private Account currentAccount;

    // Optional: keep references if you want to reuse windows
    private JFrame currentWindow;

    public WindowManager() {
        this.accountManager = new AccountManager();
    }

    // --------- Accessors ---------

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account acc) {
        this.currentAccount = acc;
    }

    // --------- Window switching helpers ---------

    private void showWindow(JFrame newWindow) {
        // Close previous window if it exists
        if (currentWindow != null) {
            currentWindow.dispose();
        }
        currentWindow = newWindow;
        currentWindow.setLocationRelativeTo(null);
        currentWindow.setVisible(true);
    }

    // ---- Public APIs used by your windows ----

    public void showLoginWindow() {
        showWindow(new LogInWindow(this));
    }

    public void showMainMenu() {
        // Assumes currentAccount is already set (e.g., after login)
        showWindow(new MainMenuWindow(this, currentAccount));
    }

    public void showGameWindow() {
        // You can pass currentAccount or other game config if needed
        showWindow(new GameWindow(this, currentAccount));
    }

    public void showLeaderboardWindow() {
        showWindow(new LeaderboardWindow(this));
    }

    public void showCustomizeWindow() {
    	new CustomizeWindow(this, currentAccount);
    }

    public void exitGame() {
        if (currentWindow != null) {
            currentWindow.dispose();
        }
        System.exit(0);
    }
}
