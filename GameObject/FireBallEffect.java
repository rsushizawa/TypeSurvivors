package GameObject;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FireBallEffect {
    public final int x;
    public final int y;
    public final double radius;
    private double remaining;
    private final double duration;

    private static BufferedImage sprite = null;
    static {
        try {
            sprite = ImageIO.read(new File("Assets/explosion.png"));
        } catch (IOException e) {
            sprite = null;
        }
    }

    public FireBallEffect(int x, int y, double radius, double durationSeconds) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.duration = Math.max(0.01, durationSeconds);
        this.remaining = this.duration;
    }

    public void update(double deltaSeconds) {
        remaining -= deltaSeconds;
    }

    public boolean isExpired() {
        return remaining <= 0.0;
    }

    public void render(Graphics2D g) {
        double t = 1.0 - Math.max(0.0, remaining / duration);
        float alpha = (float)(0.8 * (1.0 - t*0.3));

        int r = Math.max(8, (int)(radius * (0.6 + 0.4 * t)));
        int left = x - r;
        int top = y - r;

        Composite old = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0.05f, alpha)));

        if (sprite != null) {
            g.drawImage(sprite, left, top, r * 2, r * 2, null);
        } else {
            g.setColor(new Color(255, 140, 0, 200));
            g.fill(new Ellipse2D.Double(left, top, r * 2, r * 2));
        }

        g.setComposite(old);
    }
}
