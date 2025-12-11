package game.account;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountManagerTest {

    private AccountManager manager;

    @BeforeEach
    void setup() {
        // Override load and save operations to disable file I/O during testing
        manager = new AccountManager() {
            @Override
            public void loadAccounts() {
                // Disable file loading for isolated tests
                getAllAccounts().clear();
            }

            @Override
            protected void saveAccounts() {
                // Disable file writing
            }
        };

        // Ensure clean start
        manager.getAllAccounts().clear();
    }

    // ----------------------------
    // LOGIN TESTS
    // ----------------------------

    @Test
    void loginFailsWithUnknownUsername() {
        assertThrows(IllegalArgumentException.class, () -> {
            manager.login("noSuchUser", "pw");
        });
    }

    @Test
    void loginFailsWithWrongPassword() {
        manager.createAccount("user", "correct");

        assertThrows(IllegalArgumentException.class, () -> {
            manager.login("user", "wrong");
        });
    }

    @Test
    void loginSucceedsWithCorrectCredentials() {
        manager.createAccount("user", "pw");

        Account acc = manager.login("user", "pw");

        assertNotNull(acc);
        assertEquals("user", acc.getUsername());
    }

    // ----------------------------
    // ACCOUNT CREATION TESTS
    // ----------------------------

    @Test
    void createAccountSucceedsForNewUsername() {
        Account acc = manager.createAccount("newUser", "pw");

        assertNotNull(acc);
        assertEquals("newUser", acc.getUsername());
    }

    @Test
    void createAccountRejectsDuplicateUsername() {
        manager.createAccount("user", "pw");

        assertThrows(IllegalArgumentException.class, () -> {
            manager.createAccount("user", "anotherPw");
        });
    }
}
