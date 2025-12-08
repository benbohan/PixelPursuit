package game.persistence;

import game.account.Account;

import java.util.*;

/**
 * Leaderboard helper:
 *  - Builds a sorted list of leaderboard entries from all accounts.
 *  - Sorts by best run time (bestTime) in descending order.
 *  - Ignores accounts with no recorded time (bestTime <= 0).
 */
public class Leaderboard {

    // ---------- BUILDING ----------

    // buildFromAccounts - Builds a sorted leaderboard from the given accounts (up to maxEntries)
    public List<LeaderboardEntry> buildFromAccounts(Collection<Account> accounts, int maxEntries) {
        List<LeaderboardEntry> list = new ArrayList<>();

        for (Account acc : accounts) {
            double best = acc.getBestTime();
            if (best > 0.0) {
                list.add(new LeaderboardEntry(acc.getUsername(), best));
            }
        }

        Collections.sort(list);

        if (list.size() > maxEntries) {
            return new ArrayList<>(list.subList(0, maxEntries));
        }
        return list;
    }
}
