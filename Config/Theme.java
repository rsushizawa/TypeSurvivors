package Config;

import java.awt.Color;

/**
 * Mutable theme configuration so visuals can be tweaked at runtime.
 */
public final class Theme {
    private Theme() {}

    public static Color poisonFill = GameConfig.POISON_FILL_COLOR;
    public static Color poisonBorder = GameConfig.POISON_BORDER_COLOR;
    public static Color dangerLine = new Color(255, 0, 0, 150);
    public static Color wallLine = new Color(0, 200, 255, 150);

    public static void setPoisonFill(Color c) { poisonFill = c; }
    public static void setPoisonBorder(Color c) { poisonBorder = c; }
    public static void setDangerLine(Color c) { dangerLine = c; }
    public static void setWallLine(Color c) { wallLine = c; }
}
