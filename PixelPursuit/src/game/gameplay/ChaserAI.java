package game.gameplay;

/**
 * AI contract for controlling a Chaser:
 *  - Session calls update(...) once per tick for each active Chaser.
 *  - Implementations decide how the Chaser moves based on the Session state.
 *  - Can use maze layout, runner position, timers, or randomness to drive behavior.
 */
public interface ChaserAI {

    // ---------- CONTRACT ----------

    // update - Called once per tick so this AI can control the given chaser
    void update(Chaser chaser, Session session);
}
