package Config;

/**
 * Enemy-specific tuning and defaults.
 */
public final class EnemyConfig {
	private EnemyConfig() {}

	// Base movement speed applied to enemies before per-type adjustments
	public static final double BASE_SPEED = 1.0;

	// Per-wave speed scaling (multiplicative)
	public static final double SPEED_SCALE_PER_WAVE = 1.03;

	// Reference sprite width used to normalize sprite scaling (kept consistent with GameConfig)
	public static final int REFERENCE_SPRITE_WIDTH = GameConfig.REFERENCE_SPRITE_WIDTH;

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
}
