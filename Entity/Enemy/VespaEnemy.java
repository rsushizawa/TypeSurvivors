package Entity.Enemy;

import Animation.SpriteSheetLoader;
import java.awt.image.BufferedImage;

public class VespaEnemy extends Enemy {
    
    private static final String SPRITE_PATH = "Assets/Enemy/vespa color resize.png"; 
    private static final int SPRITE_WIDTH = 184*2; 
    private static final int SPRITE_HEIGHT = 184*2;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 3;
    private static final int ANIMATION_SPEED = 12;

    private double worldSpeedX;
    
    private static BufferedImage[] orcSprites = null;
    
    static {
        loadSprites();
    }
    
    private static void loadSprites() {
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
        
        // Bounce against road edges computed for this z so bees follow the curved road
        double[] bounds = Config.PerspectiveConfig.getWorldXBoundsForZ(this.z);
        double minW = bounds[0];
        double maxW = bounds[1];
        if (this.worldX > maxW) {
            double overshoot = this.worldX - maxW;
            this.worldSpeedX = -Math.abs(this.worldSpeedX);
            this.worldX = maxW - (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        } else if (this.worldX < minW) {
            double overshoot = minW - this.worldX;
            this.worldSpeedX = Math.abs(this.worldSpeedX);
            this.worldX = minW + (overshoot * Config.EnemyConfig.BOUNCE_FACTOR_LARGE);
        }
        
        updatePerspective();
    }
}