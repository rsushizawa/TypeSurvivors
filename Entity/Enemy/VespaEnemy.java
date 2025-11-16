package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VespaEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/vespa color resize.png"; 
    private static final int SPRITE_WIDTH = 184*2; 
    private static final int SPRITE_HEIGHT = 184*2;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 3;
    private static final int ANIMATION_SPEED = 12;

    private double worldSpeedX;
    private static final Random random = new Random();
    
    private static BufferedImage[] orcSprites = null;
    
    static {
        loadOrcSprites();
    }
    
    private static void loadOrcSprites() {
        orcSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH, 
            WALK_DOWN_ROW, 
            ANIMATION_FRAMES, 
            SPRITE_WIDTH, 
            SPRITE_HEIGHT
        );
    }

    public VespaEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
        super(text, worldX, zSpeed, orcSprites, ANIMATION_SPEED);
        this.worldSpeedX = worldSpeedX;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }

    @Override
    public void update(){
        this.z += this.zSpeed;
        
        this.worldX += this.worldSpeedX;
        
        if (this.worldX > this.MAX_WIDTH) {
            // Eased bounce back inside bounds
            double overshoot = this.worldX - this.MAX_WIDTH;
            this.worldSpeedX = -Math.abs(this.worldSpeedX);
            this.worldX = this.MAX_WIDTH - (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        } else if (this.worldX < this.MIN_WIDTH) {
            double overshoot = this.MIN_WIDTH - this.worldX;
            this.worldSpeedX = Math.abs(this.worldSpeedX);
            this.worldX = this.MIN_WIDTH + (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        }
        
        updatePerspective();
    }
}