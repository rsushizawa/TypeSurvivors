package Model;

import java.awt.image.BufferedImage;

public class OrcEnemy extends AnimatedEnemy {
    
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

    public OrcEnemy(String text, int x, int y, int speed) {
        super(text, x, y, orcSprites, ANIMATION_SPEED);
        this.speed = speed;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }
}