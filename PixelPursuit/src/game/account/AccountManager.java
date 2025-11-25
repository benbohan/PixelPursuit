package game.account;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AccountManager {

    // Path relative to the PROJECT ROOT:
    // PixelPursuit/src/game/resources/data/accounts.txt
    private static final String RELATIVE_PATH = "src/game/resources/data/accounts.txt";

    private final Map<String, Account> accounts = new HashMap<>();

    public AccountManager() {
        loadAccounts();
    }

    // ---------- PATH RESOLUTION ----------

    /**
     * Try to find accounts.txt. First from the project root, then from
     * one level up (if the working directory is bin/).
     */
    private File findAccountsFile() {
        // Show working dir so we can see what Eclipse is doing
        System.out.println("Working dir = " + new File(".").getAbsolutePath());

        // 1) Try from current working directory (usually project root)
        File f1 = new File(RELATIVE_PATH);
        if (f1.exists()) {
            System.out.println("Using accounts.txt at: " + f1.getAbsolutePath());
            return f1;
        }

        // 2) If we're running from bin/, go one level up
        File f2 = new File(".." + File.separator + RELATIVE_PATH);
        if (f2.exists()) {
            System.out.println("Using accounts.txt at: " + f2.getAbsolutePath());
            return f2;
        }

        // 3) Not found: we’ll create it later at project-root version
        System.out.println("accounts.txt not found, will create new at: " + f1.getAbsolutePath());
        return f1;
    }

    // ---------- FILE I/O ----------

    public void loadAccounts() {
        accounts.clear();

        File file = findAccountsFile();
        if (!file.exists()) {
            // No file yet – start empty
            return;
        }

        System.out.println("Loading accounts from: " + file.getAbsolutePath());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // username;password;freeGold;vaultGold;bestTime
                String[] parts = line.split(";");
                if (parts.length != 5) {
                    System.err.println("Skipping malformed account line: " + line);
                    continue;
                }

                String username = parts[0];
                String password = parts[1];
                int freeGold    = Integer.parseInt(parts[2]);
                int vaultGold   = Integer.parseInt(parts[3]);
                double bestTime = Double.parseDouble(parts[4]);

                Account acc = new Account(username, password, freeGold, vaultGold, bestTime);
                accounts.put(username, acc);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        // Debug: show what we loaded
        System.out.println("Loaded accounts: " + accounts.keySet());
    }

    private void saveAccounts() {
        File file = findAccountsFile();

        // Ensure the folder exists (src/game/resources/data)
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        System.out.println("Saving accounts to: " + file.getAbsolutePath());

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

            for (Account acc : accounts.values()) {
                writer.println(acc.toFileLine());  // must also be ';'-separated
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---------- PUBLIC API ----------

    /** Try to create a new account. Returns null if username already exists. */
    public Account createAccount(String username, String password) {
        if (accounts.containsKey(username)) {
            return null; // username taken
        }
        Account acc = new Account(username, password, 0, 0, 0.0);
        accounts.put(username, acc);
        saveAccounts();
        return acc;
    }

    /** Try to log in. Returns the Account if ok, null if wrong user/pass. */
    public Account login(String username, String password) {
        Account acc = accounts.get(username);
        if (acc == null) return null;
        if (!acc.getPassword().equals(password)) return null;
        return acc;
    }

    /** Call this after you update stats (gold, best time). */
    public void updateAccount(Account acc) {
        accounts.put(acc.getUsername(), acc);
        saveAccounts();
    }

    /** For leaderboard, etc. */
    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }
}
