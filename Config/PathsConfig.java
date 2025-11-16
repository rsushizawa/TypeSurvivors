package Config;

/**
 * Centralized asset and file path constants.
 */
public final class PathsConfig {
    private PathsConfig() {}

    public static final String ASSETS_DIR = "Assets";
    public static final String MUSIC_DIR = ASSETS_DIR + "/Music";
    public static final String SFX_DIR = ASSETS_DIR + "/SFX";
    public static final String SPRITES_DIR = ASSETS_DIR + "/Sprites";

    // Specific files
    public static final String FIREBALL_SFX = SFX_DIR + "/Fireball SFX.wav";
    public static final String PROJECTILE_SFX = SFX_DIR + "/Projectile SFX.wav";
    public static final String MAIN_MENU_MUSIC = MUSIC_DIR + "/Main Menu Music.wav";
    public static final String BOSS_MUSIC = MUSIC_DIR + "/Boss Music.wav";

    public static final String DICTIONARY_CSV = ASSETS_DIR + "/dict-correto.csv";
    public static final String LEADERBOARD_FILE = "leaderboard.txt";
}
