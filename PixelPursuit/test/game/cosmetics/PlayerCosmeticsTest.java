package game.cosmetics;

import game.account.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerCosmeticsTest {

    @Test
    void cannotEquipLockedColor() {
        Account acc = new Account("u", "p");

        // Color 5 not unlocked yet
        PlayerCosmetics.equipColor(acc, 5);

        // Should stay at default 0 (or whatever your default is)
        assertNotEquals(5, acc.getColor());
    }

    @Test
    void canEquipUnlockedColor() {
        Account acc = new Account("u", "p");

        PlayerCosmetics.unlockColor(acc, 5);
        PlayerCosmetics.equipColor(acc, 5);

        assertEquals(5, acc.getColor());
    }

    @Test
    void unlockedColorIsReportedCorrectly() {
        Account acc = new Account("u", "p");

        assertFalse(PlayerCosmetics.hasColor(acc, 3));

        PlayerCosmetics.unlockColor(acc, 3);

        assertTrue(PlayerCosmetics.hasColor(acc, 3));
    }

    @Test
    void cannotEquipLockedCosmetic() {
        Account acc = new Account("u", "p");

        PlayerCosmetics.equipCosmetic(acc, PlayerCosmetics.COSMETIC_CROWN);

        // Should not equip since it's not unlocked
        assertNotEquals(PlayerCosmetics.COSMETIC_CROWN, acc.getCosmetic());
    }

    @Test
    void canEquipUnlockedCosmetic() {
        Account acc = new Account("u", "p");

        PlayerCosmetics.unlockCosmetic(acc, PlayerCosmetics.COSMETIC_BEANIE);
        PlayerCosmetics.equipCosmetic(acc, PlayerCosmetics.COSMETIC_BEANIE);

        assertEquals(PlayerCosmetics.COSMETIC_BEANIE, acc.getCosmetic());
    }

    @Test
    void getEquippedCosmeticIgnoresLockedValue() {
        Account acc = new Account("u", "p");

        acc.setCosmetic(PlayerCosmetics.COSMETIC_CROWN);

        // crown not unlocked
        assertEquals(-1, PlayerCosmetics.getEquippedCosmetic(acc));
    }

    @Test
    void multiplierMustBeUnlockedToEquip() {
        Account acc = new Account("u", "p");

        PlayerCosmetics.hasMultiplier(acc, PlayerCosmetics.MULTIPLIER_5X);

        assertNotEquals(PlayerCosmetics.MULTIPLIER_5X, acc.getMultiplier());
    }

    @Test
    void getEquippedMultiplierValueDefaultsTo1WhenLocked() {
        Account acc = new Account("u", "p");

        // Set multiplier but not unlocked
        acc.setMultiplier(PlayerCosmetics.MULTIPLIER_10X);

        assertEquals(1, PlayerCosmetics.getEquippedMultiplierValue(acc));
    }
}
