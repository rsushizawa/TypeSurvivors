package Entity.Enemy;

import java.awt.image.BufferedImage;
import java.awt.FontMetrics;

public class EnemyProjectile extends Enemy {
    private int vx, vy;
    private boolean expired = false;

    public EnemyProjectile(char letter, int startX, int startY, int targetX, int targetY, int speed) {
        super(String.valueOf(letter), startX, 0.0, (BufferedImage[])null, 1);
        this.x = startX;
        this.y = startY;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double mag = Math.sqrt(dx*dx + dy*dy);
        if (mag > 0) {
            this.vx = (int)((dx / mag) * speed);
            this.vy = (int)((dy / mag) * speed);
        } else {
            this.vx = 0;
            this.vy = -speed;
        }

        this.z = 1.0;
    }

    @Override
    public void update() {
        this.x += vx;
        this.y += vy;

        if (this.x < -50 || this.x > TypeSurvivors.TypeSurvivors.gameWidth + 50 || this.y < -50 || this.y > TypeSurvivors.TypeSurvivors.gameHeight + 50) {
            this.expired = true;
        }
    }

    public boolean isExpired() {
        return expired;
    }

    public void destroy() {
        this.expired = true;
    }

    @Override
    public void render(java.awt.Graphics2D g, Model.GameModel model) {
        g.setColor(java.awt.Color.MAGENTA);
        int r = 6;
        g.fillOval(x - r, y - r, r*2, r*2);
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        String t = this.text != null ? this.text : "";
        if (!t.isEmpty()) {
            int tw = fm.stringWidth(t);
            g.drawString(t, x - tw/2, y + 4);
        }
    }
}
