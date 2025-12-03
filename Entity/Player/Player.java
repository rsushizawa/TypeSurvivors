package Entity.Player;

import java.awt.image.BufferedImage;

import Animation.SpriteSheetLoader;
import GameObject.AnimatedGameObject;

public class Player extends AnimatedGameObject {
    private static final String SPRITE_PATH = "Assets/cicadasprite.png";
    private static final int SPRITE_WIDTH = 184;
    private static final int SPRITE_HEIGHT = 184;
    private static final int IDLE_ROW = 0;

    private static final int ANIMATION_FRAMES = 6;
    private static final int ANIMATION_SPEED = 8;
    
    private static BufferedImage[] idleSprites = null;
    
    static {
        loadPlayerSprites();
    }
    
    private static void loadPlayerSprites() {
        idleSprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH,
            IDLE_ROW,
            ANIMATION_FRAMES,
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
    }
    
    public static boolean spritesLoaded() {
        return idleSprites != null;
    }

    @Override
    public void render(java.awt.Graphics2D g, Model.GameModel model) {
        super.render(g, model);
    }
}