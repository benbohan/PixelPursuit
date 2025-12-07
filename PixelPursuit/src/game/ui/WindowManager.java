package game.ui;

import game.account.Account;
import game.account.AccountManager;
import game.ui.windows.*;

import javax.swing.*;

public class WindowManager {

    private MainMenuWindow mainMenuWindow;

    private final AccountManager accountManager;
    private Account currentAccount;

    // Keep track of whatever window is currently on screen
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
        if (currentWindow != null && currentWindow != newWindow) {
            currentWindow.dispose();
        }
        currentWindow = newWindow;
        currentWindow.setLocationRelativeTo(null);
        currentWindow.setVisible(true);
    }

    // --------- Public APIs used by your windows ---------

    public void showLoginWindow() {
        showWindow(new LogInWindow(this));
    }

    public void showMainMenu() {
        // Build once, reuse so we can refresh its loot HUD
        if (mainMenuWindow == null) {
            mainMenuWindow = new MainMenuWindow(this, currentAccount);
        }
        showWindow(mainMenuWindow);
    }

    public void showGameWindow() {
        showWindow(new GameWindow(this, currentAccount));
    }

    public void showLeaderboardWindow() {
        new LeaderboardWindow(this);
    }

    public void showCustomizeWindow() {
        // Customize is a separate dialog-style window; no need to replace main menu
        new CustomizeWindow(this, currentAccount);
    }

    /**
     * Persist account to disk and refresh any open UI that depends on it.
     */
    public void updateAccount(Account account) {
        if (account == null) return;

        // keep manager’s copy in sync
        this.currentAccount = account;

        // save to accounts.txt (or wherever AccountManager writes)
        accountManager.updateAccount(account);

        // if the main menu is open, refresh its loot display
        if (mainMenuWindow != null) {
            mainMenuWindow.refreshLootDisplay();
        }
    }

    public void exitGame() {
        if (currentWindow != null) {
            currentWindow.dispose();
        }
        System.exit(0);
    }
}
