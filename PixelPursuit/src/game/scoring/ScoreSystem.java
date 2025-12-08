package game.scoring;

import game.gameplay.Session;
import game.settings.Difficulty;
import game.settings.GameConfig;

/**
 * ScoreSystem:
 *  - Converts raw Session stats into final gold and diamond payouts.
 *  - Applies Difficulty and account multiplier rules via Multiplier.
 *  - Returns a SessionResult with all breakdown fields filled.
 */
public final class ScoreSystem {

    // ---------- SCORING ----------

    // compute - Convenience overload that ignores account multiplier (treated as 1x)
    public SessionResult compute(Session session,
                                 Difficulty difficulty,
                                 boolean escaped) {
        return compute(session, difficulty, escaped, -1);
    }

    // compute - Full scoring with difficulty, escape flag, and account multiplier index
    public SessionResult compute(Session session,
                                 Difficulty difficulty,
                                 boolean escaped,
                                 int accountMultIndex) {

        if (difficulty == null) {
            difficulty = GameConfig.getCurrentDifficulty();
        }

        int timeGold          = session.getTimeGold();
        int pickupGold        = session.getPickupGold();
        int baseGold          = timeGold + pickupGold;
        int diamondsCollected = session.getPickupDiamonds();

        Multiplier multiplier = Multiplier.forOutcome(difficulty, escaped, accountMultIndex);

        int finalGold      = (int) Math.round(baseGold * multiplier.getValue());
        int finalDiamonds  = (int) Math.round(diamondsCollected * multiplier.getValue());
        double timeSeconds = session.getElapsedTimeSeconds();

        return new SessionResult(
                timeSeconds,
                timeGold,
                pickupGold,
                baseGold,
                finalGold,
                diamondsCollected,
                finalDiamonds,
                multiplier,
                difficulty,
                escaped
        );
    }
}
