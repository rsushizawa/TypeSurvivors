package Entity.Enemy;

import Animation.AnimatedSprite;
import java.awt.image.BufferedImage;
import Config.EnemyConfig;
import Model.GameModel;

public class Enemy {
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

    protected AnimatedSprite animatedSprite;
    protected int spriteWidth;
    protected int spriteHeight;
    protected double scale;

    public static final int HORIZON_Y = 50;
    public static final int VANISHING_POINT_X = 300; 
    public static final double MIN_SCALE = 0.1; 
    public static final double MAX_SCALE = 1.5;
    public static final int PLAYER_Y_LINE = 1000; 

    protected Enemy(String text, double worldX, double zSpeed, BufferedImage[] sprites, int animationSpeed) {
        this.text = text;
        this.originalText = text;
        this.worldX = worldX;
        this.z = 0.0;
        this.zSpeed = zSpeed;
        
        this.animatedSprite = new AnimatedSprite(sprites, animationSpeed, true);
        
        this.spriteWidth = getSpriteWidth();
        this.spriteHeight = getSpriteHeight();
        
        updatePerspective();
    }


    public void updatePerspective() {
        this.scale = MIN_SCALE + this.z * (MAX_SCALE - MIN_SCALE);
        
        this.y = (int)(HORIZON_Y + this.z * (PLAYER_Y_LINE - HORIZON_Y));
        
        this.x = (int)(VANISHING_POINT_X + (this.worldX - VANISHING_POINT_X) * this.scale);
    }

    public void update(){
        this.z += this.zSpeed;
        updatePerspective();
    }


    public void updateAnimation() {
        if (animatedSprite != null) {
            animatedSprite.updateAnimation();
        }
    }

    /** Optional per-frame hook for enemies to react to the game model (e.g. special attacks).
     * Default implementation does nothing. Subclasses may override. */
    public void onModelUpdate(GameModel model) {
        // no-op by default
    }

    public BufferedImage getCurrentSprite() {
        return animatedSprite != null ? animatedSprite.getCurrentSprite() : null;
    }

    public boolean hasSprites() {
        return animatedSprite != null && animatedSprite.hasSprites();
    }

    public int getSpriteWidth() {
        return animatedSprite != null ? animatedSprite.getSpriteWidth() : 0;
    }

    public int getSpriteHeight() {
        return animatedSprite != null ? animatedSprite.getSpriteHeight() : 0;
    }

    public double getScale() {
        return this.scale;
    }

    public int getScaledWidth() {
        // Normalize sprite sizes so that very large source sprites scale similarly
    double normalized = (double)EnemyConfig.REFERENCE_SPRITE_WIDTH;
        if (spriteWidth <= 0) return 0;
        double scaleFactor = normalized / (double)spriteWidth;
        return (int)(this.spriteWidth * this.scale * scaleFactor);
    }

    public int getScaledHeight() {
        // Keep height proportional to width normalization
    double normalized = (double)EnemyConfig.REFERENCE_SPRITE_WIDTH;
        if (spriteWidth <= 0) return 0;
        double scaleFactor = normalized / (double)spriteWidth;
        return (int)(this.spriteHeight * this.scale * scaleFactor);
    }
}