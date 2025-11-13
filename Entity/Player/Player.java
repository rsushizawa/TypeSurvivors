package Entity.Player;

import java.awt.image.BufferedImage;

import Animation.SpriteSheetLoader;
import GameObject.AnimatedGameObject;

public class Player extends AnimatedGameObject {
    private static final String SPRITE_PATH = "Assets/Enemy/orc2_walk_full.png";
    private static final int SPRITE_WIDTH = 64;
    private static final int SPRITE_HEIGHT = 64;
    private static final int IDLE_ROW = 0;
    private static final int ANIMATION_SPEED = 8;
    
    private static BufferedImage[] idleSprites = null;
    
    static {
        loadPlayerSprites();
    }
    
    private static void loadPlayerSprites() {
        idleSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH,
            IDLE_ROW,
            4,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public Player(int x, int y) {
        super(x, y, idleSprites, ANIMATION_SPEED, true);
    }

    @Override
    public void update() {
        super.update();
        // Add player-specific logic here (input handling, etc.)
    }
    
    public static boolean spritesLoaded() {
        return idleSprites != null;
    }
}