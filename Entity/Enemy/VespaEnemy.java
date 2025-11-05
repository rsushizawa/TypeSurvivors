package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VespaEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/orc2_walk_full.png"; 
    private static final int SPRITE_WIDTH = 64; 
    private static final int SPRITE_HEIGHT = 64;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 4;
    private static final int ANIMATION_SPEED = 8;
    public int MAX_WIDTH = 600;
    public int MIN_WIDTH = 0;
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

    public VespaEnemy(String text, int x, int y, int speed) {
        super(text, x, y, orcSprites, ANIMATION_SPEED);
        this.speedy = speed;
        this.speedx = speed;
    }
    
    public static boolean spritesLoaded() {
        return orcSprites != null;
    }

    @Override
    public void update(){
        this.y += this.speedy;
        this.x += this.speedx;
        if(this.x>this.MAX_WIDTH|| this.x<this.MIN_WIDTH){
            this.speedx = -this.speedx;
            if(this.x>this.MAX_WIDTH){
                this.MIN_WIDTH = random.nextInt(10,600/2);
            }
            else{
                this.MAX_WIDTH = random.nextInt(600/2,600-40);
            }
            
        }
    }
}