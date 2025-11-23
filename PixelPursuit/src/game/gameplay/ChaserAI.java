package game.gameplay;

/**
 * AI for a Chaser. Each tick, Session calls update() so the AI can move it.
 */
public interface ChaserAI {

    /**
     * Called once per update to let this AI control the given chaser.
     *
     * @param chaser  the chaser this AI controls
     * @param session the current game session (access to runner, maze, etc.)
     */
    void update(Chaser chaser, Session session);
}
