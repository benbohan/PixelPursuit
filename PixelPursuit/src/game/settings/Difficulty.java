package game.settings;

/**
 * Overall difficulty for a run of Pixel Pursuit.
 *
 * For now only use easy and hard, but enum makes it easy to add
 * more levels later.
 */
public enum Difficulty {
	EASY("Easy"), HARD("Hard");

	private final String displayName;

	Difficulty(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
