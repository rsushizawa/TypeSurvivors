package Entity.Projectile;

import GameObject.GameObject;
import TypeSurvivors.TypeSurvivors;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import Config.GameConfig;
import Config.PerspectiveConfig;

import java.io.File;
import java.io.IOException;
import Model.GameModel;

public class Projectile extends GameObject {
    
    private int damage;
    private static final double PROJECTILE_SPEED = 100; 
    private boolean enemyOwned = false;
    private static BufferedImage projectileSprite = null;

    static {
        try {
            projectileSprite = ImageIO.read(new File("Assets/airball.png"));
        } catch (IOException e) {
            projectileSprite = null;
        }
    }

    public Projectile(int x, int y, int targetX, int targetY, int damage) {
        this(x, y, targetX, targetY, damage, PROJECTILE_SPEED, false);
    }

    public Projectile(int x, int y, int targetX, int targetY, int damage, double speed, boolean enemyOwned) {
        super(x, y);

        double dx = targetX - x;
        double dy = targetY - y;
        double magnitude = Math.sqrt(dx * dx + dy * dy);

        if (magnitude > 0) {
            this.velocityX = (dx / magnitude) * speed;
            this.velocityY = (dy / magnitude) * speed;
        } else {
            this.velocityX = 0.0;
            this.velocityY = -speed;
        }

        this.damage = damage;
        this.enemyOwned = enemyOwned;
    }

    @Override
    public void update() {
        super.updatePosition();
        
        if (y < 0 || y < PerspectiveConfig.HORIZON_Y || x < 0 || x > TypeSurvivors.gameWidth) {
            deactivate();
        }
    }
    
    public int getDamage() {
        return damage;
    }

    public boolean isEnemyOwned() {
        return enemyOwned;
    }

    @Override
    public void render(Graphics2D g, GameModel model) {
        int r = Config.GameConfig.PROJECTILE_RADIUS;
        if (projectileSprite != null) {
            int w = r * 2;
            int h = r * 2;

            double angle = 0.0;
            if (this.velocityX != 0 || this.velocityY != 0) {
                angle = Math.atan2(this.velocityY, this.velocityX) + Math.PI / 2.0;
            }

            AffineTransform old = g.getTransform();
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            at.rotate(angle);
            at.translate(-w / 2.0, -h / 2.0);
            g.setTransform(at);
            g.drawImage(projectileSprite, 0, 0, w, h, null);
            g.setTransform(old);
        } else {
            g.setColor(Color.ORANGE);
            g.fillOval(x - r, y - r, r * 2, r * 2);
        }
    }// sprite faces up
}