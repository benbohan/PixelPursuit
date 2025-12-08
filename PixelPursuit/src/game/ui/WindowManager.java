package game.ui;

import game.account.Account;
import game.account.AccountManager;
import game.ui.windows.*;

import javax.swing.*;

/**
 * WindowManager - Central controller for creating, showing, and updating all game windows.
 */
public class WindowManager {

    // ---------- FIELDS ----------

    private MainMenuWindow mainMenuWindow;

    private final AccountManager accountManager;
    private Account currentAccount;

    // Tracks whichever window is currently on screen
    private JFrame currentWindow;

    // ---------- CONSTRUCTORS ----------

    // WindowManager - Creates a new manager with its own AccountManager
    public WindowManager() {
        this.accountManager = new AccountManager();
    }

    // ---------- ACCESSORS ----------

    // getAccountManager - Returns the shared AccountManager instance
    public AccountManager getAccountManager() {
        return accountManager;
    }

    // getCurrentAccount - Returns the currently active account, or null
    public Account getCurrentAccount() {
        return currentAccount;
    }

    // setCurrentAccount - Updates the active account reference
    public void setCurrentAccount(Account acc) {
        this.currentAccount = acc;
    }

    // ---------- WINDOW SWITCHING ----------

    // showWindow - Disposes the current window and shows the given one
    private void showWindow(JFrame newWindow) {
        if (currentWindow != null && currentWindow != newWindow) {
            currentWindow.dispose();
        }
        currentWindow = newWindow;
        currentWindow.setLocationRelativeTo(null);
        currentWindow.setVisible(true);
    }

    // ---------- PUBLIC WINDOW APIS ----------

    // showLoginWindow - Shows the login / create account screen
    public void showLoginWindow() {
        showWindow(new LogInWindow(this));
    }

    // showMainMenu - Shows the main menu for the current account
    public void showMainMenu() {
        mainMenuWindow = new MainMenuWindow(this, currentAccount);
        showWindow(mainMenuWindow);
    }

    // showGameWindow - Starts a new game window for the current account
    public void showGameWindow() {
        showWindow(new GameWindow(this, currentAccount));
    }

    // showLeaderboardWindow - Opens the leaderboard as a separate window
    public void showLeaderboardWindow() {
        new LeaderboardWindow(this);
    }

    // showCustomizeWindow - Opens the customization window as a dialog-style window
    public void showCustomizeWindow() {
        new CustomizeWindow(this, currentAccount);
    }

    // ---------- ACCOUNT / UI SYNC ----------

    // updateAccount - Saves account changes and refreshes any dependent UI
    public void updateAccount(Account account) {
        if (account == null) return;

        this.currentAccount = account;
        accountManager.updateAccount(account);

        if (mainMenuWindow != null) {
            mainMenuWindow.refreshLootDisplay();
        }
    }

    // refreshMainMenuLoot - Refreshes loot display if the main menu is open
    public void refreshMainMenuLoot() {
        if (mainMenuWindow != null) {
            mainMenuWindow.refreshLootDisplay();
        }
    }

    // exitGame - Closes the active window and exits the application
    public void exitGame() {
        if (currentWindow != null) {
            currentWindow.dispose();
        }
        System.exit(0);
    }
}
