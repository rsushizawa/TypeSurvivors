package Config;

import javax.swing.plaf.TreeUI;

/**
 * Global game configuration constants.
 */
public final class GameConfig {
    private GameConfig() {}

    // Poison wall defaults
    public static final double POISON_DEFAULT_WIDTH = 80.0; // px
    public static final double POISON_DEFAULT_DURATION = 6.0; // seconds
    public static final double POISON_DEFAULT_SLOW_FACTOR = 0.5; // 50% slow

    // Enemy tuning
    public static final double ENEMY_SPEED_MULTIPLIER = 0.8; // global speed reduction
    // Reference sprite width for normalized scaling (so big sprites appear similar)
    public static final int REFERENCE_SPRITE_WIDTH = 128;

    // Spawn / margins
    public static final int SPAWN_HORIZONTAL_BORDER = 50; // px margin from left/right when spawning
    public static final int MARGIN_SMALL = 20;
    public static final int MARGIN_MEDIUM = 30;
    public static final int MARGIN_LARGE = 30;

    // Bounce / easing
    public static final double BOUNCE_OVERSHOOT_REDUCTION = 0.5; // generic overshoot reduction factor
    // Per-enemy type bounce reduction factors (used for multi-frame easing)
    public static final double BOUNCE_FACTOR_SMALL = 0.4;
    public static final double BOUNCE_FACTOR_MEDIUM = 0.5;
    public static final double BOUNCE_FACTOR_LARGE = 0.6;

    // Projectile/rendering sizes
    public static final int PROJECTILE_RADIUS = 3; // px (oval radius)

    // Stroke widths
    public static final float DANGER_LINE_STROKE = 3.0f;
    public static final float WALL_STROKE = 8.0f;

    // Poison wall visuals
    public static final java.awt.Color POISON_FILL_COLOR = new java.awt.Color(100, 200, 50, 90);
    public static final java.awt.Color POISON_BORDER_COLOR = new java.awt.Color(50, 150, 30, 160);
    // Poison wall rendering parameters
    public static final int POISON_RENDER_BORDER = 2; // px border width when drawing

    // FireBall visual
    public static final double FIREBALL_VISUAL_DURATION = 0.6; // seconds
    public static final int FIREBALL_MAX_RADIUS = 80; // px visual cap

    // Cooldown visuals
    public static final int COOLDOWN_ICON_SIZE = 32; // px square
    public static final int COOLDOWN_ICON_PADDING = 8; // px from corner
    public static final int COOLDOWN_TOP_OFFSET = 150; // px from top to start stacking cooldown icons
    public static final int COOLDOWN_VERTICAL_SPACING = 8; // px between stacked cooldown icons
    
    // UI / layout constants
    public static final int LEVELUP_TITLE_FONT = 48;
    public static final int LEVELUP_SUBTITLE_FONT = 24;
    public static final int LEVELUP_CARD_HEIGHT = 220;
    public static final int LEVELUP_CARD_PADDING = 50;
    public static final int LEVELUP_CARD_SPACING = 20;

    public static final int MAINMENU_TITLE_FONT = 60;
    public static final int MAINMENU_SUB_FONT = 24;

    // Typing / UI font sizes
    public static final int TYPING_FONT = 22;
    public static final int STATS_FONT = 18;
    public static final int SMALL_FONT = 12;
    // Enemy text fixed font size for readability
    public static final int ENEMY_FONT_SIZE = 14;

    // Runtime-configurable settings (persisted at runtime only)
    // Range: 0.0 (mute) .. 1.0 (full)
    public static float MUSIC_VOLUME = 0.8f;
    public static float SFX_VOLUME = 0.8f;
    public static boolean FULLSCREEN = false;
    // Developer debugging visuals
    public static boolean DEBUG_ROAD_RENDER = true;
}
