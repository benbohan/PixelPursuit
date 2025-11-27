package game.scoring;

import game.gameplay.Session;
import game.settings.Difficulty;
import game.settings.GameConfig;

/**
 * Responsible for turning raw Session numbers (time survived, gold earned)
 * into a final payout using the current Difficulty and multiplier rules.
 *
 * Scoring model:
 *   - baseGold = timeGold + pickupGold
 *   - finalGold = baseGold * multiplier
 *   - multiplier is 0x on death, 1x on easy escape, 2x on hard escape.
 */
public final class ScoreSystem {

    /**
     * Compute a complete SessionResult for the given run.
     */
    public SessionResult compute(Session session,
                                 Difficulty difficulty,
                                 boolean escaped) {

        if (difficulty == null) {
            // Use whatever the global setting is
            difficulty = GameConfig.getCurrentDifficulty();
        }

        int timeGold   = session.getTimeGold();
        int pickupGold = session.getPickupGold();
        int baseGold   = timeGold + pickupGold;

        Multiplier multiplier = Multiplier.forOutcome(difficulty, escaped);

        int finalGold = (int) Math.round(baseGold * multiplier.getValue());
        double timeSeconds = session.getElapsedTimeSeconds();

        return new SessionResult(
                timeSeconds,
                timeGold,
                pickupGold,
                baseGold,
                finalGold,
                multiplier,
                difficulty,
                escaped
        );
    }
}
