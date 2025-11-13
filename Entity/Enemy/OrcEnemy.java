package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;

public class OrcEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/orc2_walk_full.png"; 
    private static final int SPRITE_WIDTH = 64; 
    private static final int SPRITE_HEIGHT = 64;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 4;
    private static final int ANIMATION_SPEED = 8;
    
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

    public OrcEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
        super(text, worldX, zSpeed, orcSprites, ANIMATION_SPEED);
    }
    
    @Override
    public void update() {
        this.z += this.zSpeed;
        
        updatePerspective();
    }

    public static boolean spritesLoaded() {
        return orcSprites != null;
    }
}