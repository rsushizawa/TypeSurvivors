package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;
import java.util.Random;

public class LouvaDeusEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/orc2_walk_full.png"; 
    private static final int SPRITE_WIDTH = 64; 
    private static final int SPRITE_HEIGHT = 64;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 4;
    private static final int ANIMATION_SPEED = 8;
    
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
    public LouvaDeusEnemy(String text, double worldX, double zSpeed, double worldSpeedX) {
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
        
        if(this.worldX > this.MAX_WIDTH || this.worldX < this.MIN_WIDTH){
            this.worldSpeedX = -this.worldSpeedX;
            if(this.worldX > this.MAX_WIDTH){
                this.MIN_WIDTH = random.nextInt(10, 600 / 2);
            }
            else{
                this.MAX_WIDTH = random.nextInt(600 / 2, 600 - 40);
            }
        }
        
        updatePerspective();
    }
}