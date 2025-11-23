package game.account;

import java.io.*;
import java.util.*;

public class AccountManager {

    private static final String FILE_NAME = "accounts.txt";

    private Map<String, Account> accounts = new HashMap<>();

    public AccountManager() {
        loadAccounts();
    }

    // ---------- FILE I/O ----------

    private void loadAccounts() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return; // no accounts yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Account acc = Account.fromFileLine(line);
                accounts.put(acc.getUsername(), acc);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAccounts() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Account acc : accounts.values()) {
                writer.println(acc.toFileLine());
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
        // new account: 0 freeGold, 0 vaultGold, 0 bestTime
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
