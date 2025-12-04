package View;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import Data.GameState;

public class BackgroundManager {

    private static Image menuBackground;
    private static Image playfieldBackground;

    public static void init(GameView view) {
        int w = view.getGameWidth();
        int h = view.getGameHeight();
        menuBackground = loadOrGenerate("Assets/Backgrounds/menu.png", w, h, "MENU");
        playfieldBackground = loadOrGenerate("Assets/Backgrounds/playfield.png", w, h, "PLAYFIELD");
    }

    private static Image loadOrGenerate(String resourcePath, int w, int h, String label) {
        try (InputStream is = BackgroundManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                BufferedImage src = ImageIO.read(is);
                if (src != null) {
                    return src;
                }
            }
        } catch (Exception e) {
        }
        BufferedImage img = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color top = label.equals("MENU") ? new Color(30, 30, 60) : new Color(10, 30, 10);
        Color bottom = label.equals("MENU") ? new Color(80, 80, 120) : new Color(20, 60, 20);
        GradientPaint gp = new GradientPaint(0, 0, top, 0, h, bottom);
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(255, 255, 255, 12));
        for (int y = 0; y < h; y += 40) {
            g.fillRect(0, y, w, 8);
        }

        g.setFont(new Font("SansSerif", Font.BOLD, Math.max(24, w / 12)));
        FontMetrics fm = g.getFontMetrics();
        String txt = label;
        int tx = (w - fm.stringWidth(txt)) / 2;
        int ty = h / 2 + fm.getAscent() / 2;
        g.setColor(new Color(255, 255, 255, 80));
        g.drawString(txt, tx, ty);

        g.dispose();
        return img;
    }

    public static Image getBackgroundForState(GameState state) {
        if (state == null) return menuBackground;
        switch (state) {
            case MAIN_MENU:
            case LEADERBOARD:
                return menuBackground;
            case PLAYING:
                return playfieldBackground;
            default:
                return null;
        }
    }
}
