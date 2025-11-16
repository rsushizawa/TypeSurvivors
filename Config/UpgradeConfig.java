package Config;

/**
 * Defaults and scaling for upgrades.
 */
public final class UpgradeConfig {
    private UpgradeConfig() {}

    // Fire Ball
    public static final double FIREBALL_BASE_COOLDOWN = 8.0; // seconds
    public static final double FIREBALL_BASE_RADIUS = 60.0; // px
    public static final double FIREBALL_RADIUS_SCALE_PER_LEVEL = 1.1;
    // Visual limits/duration for fireball effect
    public static final double FIREBALL_MAX_RADIUS = 80.0; // px
    public static final double FIREBALL_VISUAL_DURATION = 0.6; // seconds

    // Insect Spray / Poison Wall
    public static final double INSECT_SPRAY_BASE_COOLDOWN = 12.0; // seconds
    public static final double INSECT_SPRAY_BASE_WIDTH = GameConfig.POISON_DEFAULT_WIDTH;
    public static final double INSECT_SPRAY_BASE_DURATION = GameConfig.POISON_DEFAULT_DURATION;
    public static final double INSECT_SPRAY_BASE_SLOW = GameConfig.POISON_DEFAULT_SLOW_FACTOR;

    // Split Shot
    public static final double SPLITSHOT_BASE_COOLDOWN = 6.0; // seconds
    public static final double SPLITSHOT_SHOT_COUNT = 3;

    // Generic upgrade scaling
    public static final double COOLDOWN_REDUCTION_PER_LEVEL = 0.95; // multiplicative

    // Wall upgrade defaults
    public static final double WALL_BASE_DURATION = 5.0; // seconds
    public static final double WALL_DURATION_PER_LEVEL = 1.0; // seconds per level
    public static final double WALL_BASE_COOLDOWN = 10.0; // seconds
    public static final double WALL_COOLDOWN_PER_LEVEL = -0.5; // reduction per level
    public static final double WALL_BASE_DISTANCE = 200.0; // px from player
    public static final double WALL_DISTANCE_PER_LEVEL = 5.0; // px per level
}
