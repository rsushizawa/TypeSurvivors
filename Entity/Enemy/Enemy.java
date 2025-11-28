package Entity.Enemy;

import java.awt.image.BufferedImage;
import Config.EnemyConfig;
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

    public static final int HORIZON_Y = 50;
    public static final int VANISHING_POINT_X = 300; 
    public static final double MIN_SCALE = 0.1; 
    public static final double MAX_SCALE = 1.5;
    public static final int PLAYER_Y_LINE = 1000; 

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
        this.scale = MIN_SCALE + this.z * (MAX_SCALE - MIN_SCALE);
        
        this.y = (int)(HORIZON_Y + this.z * (PLAYER_Y_LINE - HORIZON_Y));
        
        int projectedX = (int)(VANISHING_POINT_X + (this.worldX - VANISHING_POINT_X) * this.scale);
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
        // Ensure animated sprite frames advance for enemies
        super.update();
    }



    public void updateAnimation() {
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