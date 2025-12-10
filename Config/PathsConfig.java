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
    public static final String BARRIER_SFX = SFX_DIR + "/Barrier.wav";
    public static final String STUN_SFX = SFX_DIR + "/Stun.wav";
    public static final String LEVEL_UP_SFX = SFX_DIR + "/LevelUp.wav";
    public static final String DAMAGE_PER_SECOND_SFX = SFX_DIR + "/DamagePerSecond.wav";
    // Projectile SFX files live in the Projectile subfolder
    public static final String PROJECTILE_SFX = SFX_DIR + "/Projectile/Projectile SFX1.wav";
    public static final String LOUVA_ATTACK_SFX = SFX_DIR + "/Louva Attack.wav";
    public static final String MAIN_MENU_MUSIC = MUSIC_DIR + "/interludio.wav";
    public static final String BOSS_MUSIC = MUSIC_DIR + "/StarWarsPart.wav";
    public static final String WRONG_KEY_SFX = SFX_DIR + "/WrongKey.wav";
    public static final String DEATH1_SFX = SFX_DIR + "/Death/death1.wav";
    public static final String DEATH2_SFX = SFX_DIR + "/Death/death2.wav";
    public static final String DEATH3_SFX = SFX_DIR + "/Death/death3.wav";

    public static final String DICTIONARY_CSV = ASSETS_DIR + "/dict-correto.csv";
    public static final String LEADERBOARD_FILE = "leaderboard.txt";
}
