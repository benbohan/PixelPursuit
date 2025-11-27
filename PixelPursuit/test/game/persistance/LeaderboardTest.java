package game.persistance;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import game.account.Account;
import game.persistence.Leaderboard;
import game.persistence.LeaderboardEntry;

public class LeaderboardTest {

    @Test
    void buildFromAccountsSortsAndTrimsAndSkipsZeroTimes() {
        Account a = new Account("alice", "pw", 0, 0, 10.0);
        Account b = new Account("bob",   "pw", 0, 0, 30.0);
        Account c = new Account("carl",  "pw", 0, 0, 20.0);
        Account d = new Account("dave",  "pw", 0, 0, 0.0); // no best time

        List<Account> accounts = Arrays.asList(a, b, c, d);

        Leaderboard lb = new Leaderboard();
        List<LeaderboardEntry> entries = lb.buildFromAccounts(accounts, 2);

        // dave should be dropped, and list shortened to top 2 scores
        assertEquals(2, entries.size());

        assertEquals("bob",  entries.get(0).getUsername());
        assertEquals(30.0,   entries.get(0).getBestTimeSeconds(), 1e-6);

        assertEquals("carl", entries.get(1).getUsername());
        assertEquals(20.0,   entries.get(1).getBestTimeSeconds(), 1e-6);
    }
}
