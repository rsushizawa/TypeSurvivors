package Config;

/**
 * Enemy-specific tuning and defaults.
 */
public final class EnemyConfig {
	private EnemyConfig() {}

	// Base movement speed applied to enemies before per-type adjustments
	public static final double BASE_SPEED = 0.5;

	// Per-wave speed scaling (multiplicative)
	public static final double SPEED_SCALE_PER_WAVE = 1.03;

	// Reference sprite width used to normalize sprite scaling (kept consistent with GameConfig)
	public static final int REFERENCE_SPRITE_WIDTH = GameConfig.REFERENCE_SPRITE_WIDTH;

	public static final int REFERENCE_PROJECTILE_WIDTH = GameConfig.REFERENCE_SPRITE_WIDTH*2;

	// Horizontal spawn margins (px)
	public static final int SPAWN_BORDER = GameConfig.SPAWN_HORIZONTAL_BORDER;
	public static final int MARGIN_SMALL = GameConfig.MARGIN_SMALL;
	public static final int MARGIN_MEDIUM = GameConfig.MARGIN_MEDIUM;
	public static final int MARGIN_LARGE = GameConfig.MARGIN_LARGE;

	// Global multiplier applied to zSpeed to tune overall difficulty
	public static final double ENEMY_SPEED_MULTIPLIER = 0.8;

	// Bounce / easing tuning
	public static final double BOUNCE_OVERSHOOT_REDUCTION = GameConfig.BOUNCE_OVERSHOOT_REDUCTION;
	public static final double BOUNCE_FACTOR_SMALL = GameConfig.BOUNCE_FACTOR_SMALL;
	public static final double BOUNCE_FACTOR_MEDIUM = GameConfig.BOUNCE_FACTOR_MEDIUM;
	public static final double BOUNCE_FACTOR_LARGE = GameConfig.BOUNCE_FACTOR_LARGE;

	// LouvaDeus (mantis) specific defaults
	// How many seconds after spawn before the mantis stops and performs its attack
	public static final double LOUVADEUS_ATTACK_DELAY_SECONDS = 7.0;

	// Number of projectiles (letters) the mantis will launch at the player when attacking
	public static final int LOUVADEUS_ATTACK_PROJECTILES = 1;

	// Row index in the sprite sheet for the attack animation (0-indexed). Change as needed.
	public static final int LOUVADEUS_ATTACK_ROW = 1;

	// Small z push applied when a correct letter is typed (pushes enemy slightly away)
	public static final double ENEMY_HIT_PUSHBACK = 0.008;
    
	// Cooldown (in seconds) between subsequent attacks for LouvaDeus. Set to <= 0 for a single attack only.
	public static final double LOUVADEUS_ATTACK_COOLDOWN_SECONDS = 6.0;

	// Maximum number of attacks per spawn. -1 = unlimited / keep attacking every cooldown.
	public static final int LOUVADEUS_MAX_ATTACKS = -1;

	// Speed (pixels per tick) for projectiles launched by LouvaDeus
	public static final int LOUVADEUS_PROJECTILE_SPEED = 2;
}
