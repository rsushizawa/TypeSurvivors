package Entity.Enemy;

import java.awt.image.BufferedImage;
import Config.EnemyConfig;
import Config.PerspectiveConfig;
import Model.GameModel;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.FontMetrics;
import GameObject.AnimatedGameObject;

public class Enemy extends AnimatedGameObject {
    public String text;
    public final String originalText;
    public int x, y; // Screen coordinates
    public double z; // Depth (0.0 = horizon, 1.0 = player)
    public double zSpeed; // Speed moving towards player (z per frame)
    public double worldX; // "Track" position on the horizontal axis
    
    public int speedx;
    public int speedy;
    
    public int MAX_WIDTH = 540;
    public int MIN_WIDTH = 0;

    protected double scale;

    public static int HORIZON_Y = (int)Math.round(PerspectiveConfig.HORIZON_Y);
    public static int PLAYER_Y_LINE = (int)Math.round(PerspectiveConfig.PLAYER_Y_LINE);

    public static void refreshPerspectiveConstants() {
        HORIZON_Y = (int)Math.round(PerspectiveConfig.HORIZON_Y);
        PLAYER_Y_LINE = (int)Math.round(PerspectiveConfig.PLAYER_Y_LINE);
    }

    protected Enemy(String text, double worldX, double zSpeed, BufferedImage[] sprites, int animationSpeed) {
        super(0, 0, sprites, animationSpeed, true);
        this.text = text;
        this.originalText = text;
        this.worldX = worldX;
        this.z = 0.0;
        this.zSpeed = zSpeed;
        updatePerspective();
    }


    public void updatePerspective() {
        this.scale = PerspectiveConfig.MIN_SCALE + this.z * (PerspectiveConfig.MAX_SCALE - PerspectiveConfig.MIN_SCALE);

        java.awt.geom.Point2D.Double pathPoint = PerspectiveConfig.ROAD_PATH.getPointForZ(this.z);
        java.awt.geom.Point2D.Double normal = PerspectiveConfig.ROAD_PATH.getNormalForZ(this.z);
        // Apply global camera vertical offset so the camera can be raised/lowered.
        pathPoint.y += PerspectiveConfig.CAMERA_Y_OFFSET;
        this.y = (int) Math.round(pathPoint.y);

        double centerXAtZ = pathPoint.x;

        // Lateral offset in screen pixels (positive = right side along normal)
        double lateralScreen = (this.worldX - PerspectiveConfig.PLAYER_CENTER_X) * this.scale;

        // Clamp worldX in world-space so movement respects the road edges.
        double[] bounds = PerspectiveConfig.getWorldXBoundsForZ(this.z);
        double minAllowedWorldX = bounds[0];
        double maxAllowedWorldX = bounds[1];
        if (this.worldX < minAllowedWorldX) this.worldX = minAllowedWorldX;
        if (this.worldX > maxAllowedWorldX) this.worldX = maxAllowedWorldX;

        // Recompute lateralScreen after potential clamp
        lateralScreen = (this.worldX - PerspectiveConfig.PLAYER_CENTER_X) * this.scale;

        // Project along the normal so the lateral offset follows the road slope.
        int projectedX = (int) Math.round(centerXAtZ + normal.x * lateralScreen);
        int projectedY = (int) Math.round(pathPoint.y + normal.y * lateralScreen);
        this.y = projectedY;
        int halfScaledWidth = 0;
        int scaledW = getScaledWidth();
        if (scaledW > 0) halfScaledWidth = scaledW / 2;

        int minXAllowed = this.MIN_WIDTH + halfScaledWidth;
        int maxXAllowed = this.MAX_WIDTH - halfScaledWidth;
        if (minXAllowed > maxXAllowed) {
            minXAllowed = this.MIN_WIDTH;
            maxXAllowed = this.MAX_WIDTH;
        }

        if (projectedX < minXAllowed) projectedX = minXAllowed;
        if (projectedX > maxXAllowed) projectedX = maxXAllowed;

        this.x = projectedX;
    }

    public void update(){
        this.z += this.zSpeed;
        updatePerspective();
        super.update();
    }



    public void updateAnimation() {
        if (this.animatedSprite != null) {
            this.animatedSprite.updateAnimation();
        }
    }
    public void onModelUpdate(GameModel model) {
    }

    public double getScale() {
        return this.scale;
    }

    public int getScaledWidth() {
    double normalized = (double)EnemyConfig.REFERENCE_SPRITE_WIDTH;
        int sw = getSpriteWidth();
        if (sw <= 0) return 0;
        double scaleFactor = normalized / (double)sw;
        return (int)(sw * this.scale * scaleFactor);
    }

    public int getScaledHeight() {
    double normalized = (double)EnemyConfig.REFERENCE_SPRITE_WIDTH;
        int sw = getSpriteWidth();
        if (sw <= 0) return 0;
        double scaleFactor = normalized / (double)sw;
        return (int)(getSpriteHeight() * this.scale * scaleFactor);
    }

    public void render(Graphics2D g, GameModel model) {
        BufferedImage sprite = getCurrentSprite();
        if (sprite != null) {
            int scaledWidth = getScaledWidth();
            int scaledHeight = getScaledHeight();
            int drawX = x - (scaledWidth / 2);
            int drawY = y - scaledHeight;
            g.drawImage(sprite, drawX, drawY, scaledWidth, scaledHeight, null);
            int fontSize = Config.GameConfig.ENEMY_FONT_SIZE;
            g.setFont(new Font("Monospaced", Font.BOLD, fontSize));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int centeredX = x - (textWidth / 2);
            int textY = y + 15;
            if (model != null && model.getTargetEnemy() == this) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(text, centeredX, textY);
        } else {
            g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.ENEMY_FONT_SIZE));
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int centeredX = x - (textWidth / 2);
            int textY = y;
            if (model != null && model.getTargetEnemy() == this) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(text, centeredX, textY);
        }
    }
}