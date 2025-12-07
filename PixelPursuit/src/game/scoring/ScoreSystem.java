package game.scoring;

import game.gameplay.Session;
import game.settings.*;

/**
 * Responsible for turning raw Session numbers (time survived, gold earned)
 * into a final payout using the current Difficulty and multiplier rules.
 *
 * Scoring model:
 *   - baseGold = timeGold + pickupGold
 *   - finalGold = baseGold * multiplier
 *   - finalDiamonds = pickupDiamonds * multiplier
 *   - multiplier is 0x on death, 1x on easy escape, 2x on hard escape.
 */
public final class ScoreSystem {


    // Compute - a complete SessionResult for the given run.
    public SessionResult compute(Session session,
                                 Difficulty difficulty,
                                 boolean escaped) {

    	return compute(session, difficulty, escaped, -1);
    }
    
    public SessionResult compute(Session session,
            Difficulty difficulty,
            boolean escaped,
            int accountMultIndex) {

		if (difficulty == null) {
		difficulty = GameConfig.getCurrentDifficulty();
	}

        int timeGold   = session.getTimeGold();
        int pickupGold = session.getPickupGold();
        int baseGold   = timeGold + pickupGold;
        int diamondsCollected = session.getPickupDiamonds();

        Multiplier multiplier = Multiplier.forOutcome(difficulty, escaped, accountMultIndex);

        int finalGold = (int) Math.round(baseGold * multiplier.getValue());
        int finalDiamonds = (int) Math.round(diamondsCollected * multiplier.getValue());
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
