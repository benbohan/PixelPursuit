package game.account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import game.cosmetics.PlayerCosmetics;

/**
 * Manager for player accounts:
 *  - Loads/saves accounts to a simple text file on disk.
 *  - Provides create, login, and update operations for Account objects.
 *  - Exposes all accounts for use in leaderboards and other systems.
 */
public class AccountManager {

    // ---------- FIELDS ----------

    // Path to accounts.txt: PixelPursuit/src/game/resources/data/accounts.txt
    private static final String ACCOUNTS_PATH = "src/game/resources/data/accounts.txt";

    private final Map<String, Account> accounts = new HashMap<>();

    // ---------- CONSTRUCTORS ----------

    // AccountManager - Creates a manager and immediately loads accounts from disk
    public AccountManager() {
        loadAccounts();
    }

    // ---------- FILE I/O ----------

    // loadAccounts - Loads all accounts from accounts.txt into memory
    public void loadAccounts() {
        accounts.clear();

        File file = new File(ACCOUNTS_PATH);
        if (!file.exists()) {
            // No file: start empty
            return;
        }

        System.out.println("Loading accounts from: " + file.getAbsolutePath());

        // Open file for reading (1 line at a time)
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                try {
                    // Line --> Account object
                    Account acc = Account.fromFileLine(line);
                    accounts.put(acc.getUsername(), acc);
                } catch (Exception e) {
                    System.err.println("Skipping malformed account line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show loaded accounts
        System.out.println("Loaded accounts: " + accounts.keySet());
    }

    // saveAccounts - Writes all accounts back to accounts.txt
    private void saveAccounts() {
        File file = new File(ACCOUNTS_PATH);

        // Ensure the folder exists
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        System.out.println("Saving accounts to: " + file.getAbsolutePath());

        // Open file for writing
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // Write each Account
            for (Account acc : accounts.values()) {
                writer.println(acc.toFileLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- PUBLIC API ----------

    // createAccount - Creates a new account; returns null if username is taken
    public Account createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return null; // Username taken
        }

        // New account: 0 currencies / default equips / no unlocks
        Account acc = new Account(username, password,
                0, 0, 0, 0,
                0.0,
                14, // default color
                0,  // default cosmetic
                0,  // default multiplier
                0L);

        // Unlock default color
        PlayerCosmetics.unlockColor(acc, 14);

        accounts.put(username, acc);
        saveAccounts();
        return acc;
    }

    // login - Returns the Account or null if username/password is invalid
    public Account login(String username, String password) {
        Account acc = accounts.get(username);
        if (acc == null) return null;
        if (!acc.getPassword().equals(password)) return null;
        return acc;
    }

    // updateAccount - Writes changes for this account and saves all accounts to disk
    public void updateAccount(Account acc) {
        accounts.put(acc.getUsername(), acc);
        saveAccounts();
    }

    // getAllAccounts - Returns a collection view of all accounts
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
