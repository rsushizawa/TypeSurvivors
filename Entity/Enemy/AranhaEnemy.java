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
    public int MAX_WIDTH = 600;
    public int MIN_WIDTH = 0;

    private final int amplitude;
    private final int v_deslocamento;
    
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

    public AranhaEnemy(String text, int x, int y, int speed) {
        super(text, x, y, orcSprites, ANIMATION_SPEED);
        this.speedy = speed;
        this.speedx = speed;
        this.amplitude = 2;
        this.v_deslocamento = 1;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }

    @Override
    public void update(){
        double cos = Math.cos((y*(Math.PI/180))%(Math.PI));
        if(cos < 0){
            cos = cos*(-1);
        }
        this.y += this.speedy * amplitude * cos + v_deslocamento;
    }
}