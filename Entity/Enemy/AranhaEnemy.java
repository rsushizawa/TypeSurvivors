package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;

public class AranhaEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/orc2_walk_full.png"; 
    private static final int SPRITE_WIDTH = 64; 
    private static final int SPRITE_HEIGHT = 64;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 4;
    private static final int ANIMATION_SPEED = 8;

    private final double initialWorldX;
    private final double worldSpeedX;
    private static final double SINE_AMPLITUDE = 100.0;
    
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

    public AranhaEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
        super(text, worldX, zSpeed, orcSprites, ANIMATION_SPEED);
        this.initialWorldX = worldX;
        this.worldSpeedX = worldSpeedX;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }

    @Override
    public void update(){
        this.z += this.zSpeed;
        
        double angle = (this.z * this.worldSpeedX * 0.1 * Math.PI); 
        this.worldX = this.initialWorldX + (Math.sin(angle) * SINE_AMPLITUDE);
        
        updatePerspective();
    }
}