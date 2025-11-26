package game.account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AccountManager {

    // Path to accounts.txt: PixelPursuit/src/game/resources/data/accounts.txt
    private static final String ACCOUNTS_PATH = "src/game/resources/data/accounts.txt";

    private final Map<String, Account> accounts = new HashMap<>();

    // Constructor
    public AccountManager() {
        loadAccounts();
    }

    // ---------- FILE I/O ----------

    // Load all accounts from accounts.txt
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

    // Save all accounts back to accounts.txt
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

    // Create a new account: Returns null if username taken.
    public Account createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return null; // Username taken
        }

        // New account: 0 currencies / default equips / no unlocks
        Account acc = new Account(username, password,
                                  0, 0,    // freeGold, freeDiamonds
                                  0, 0,    // vaultGold, vaultDiamonds
                                  0.0,     // bestTime
                                  0L, 0L,  // color, cosmetic
                                  0L);     // unlocks bit mask

        accounts.put(username, acc);
        saveAccounts();
        return acc;
    }

    // Log in: Returns Account OR null if wrong Username/Password.
    public Account login(String username, String password) {
        Account acc = accounts.get(username);
        if (acc == null) return null;
        if (!acc.getPassword().equals(password)) return null;
        return acc;
    }

    // Stores updated account to accounts.txt
    public void updateAccount(Account acc) {
        accounts.put(acc.getUsername(), acc);
        saveAccounts();
    }

    // Collect all accounts for Leaderboard
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
