package game.items.weapons;

/**
 * Coating types that can be applied to certain weapons.
 *
 * <p><b>REQ4</b>: Used by {@link CoatableWeapon} to modify on-hit behavior.</p>
 */
public enum Coating {
    /** No coating applied. */
    NONE,
    /** Yew Berry coating: applies a poison DoT on hit. */
    YEWBERRY,
    /** Snow coating: applies a frostbite warmth drain on hit (unless immune). */
    SNOW
}
