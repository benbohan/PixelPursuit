package game.persistence;

import game.account.Account;

import java.util.*;

public class Leaderboard {

    /**
     * Build a leaderboard list from all accounts, sorted by best time descending.
     * Only includes accounts with bestTime > 0.
     */
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
