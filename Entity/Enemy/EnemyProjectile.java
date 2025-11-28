package Entity.Enemy;

import java.awt.image.BufferedImage;

import Animation.SpriteSheetLoader;
import Config.EnemyConfig;

public class EnemyProjectile extends Enemy {
    private int vx, vy;
    private boolean expired = false;

    private static final String SPRITE_PATH = "Assets/Enemy/airslash.png"; 
    private static final int SPRITE_WIDTH = 184; 
    private static final int SPRITE_HEIGHT = 184;
    private static final int WALK_DOWN_ROW = 0;
    private static final int ANIMATION_FRAMES = 1;

    private static BufferedImage[] sprites = null;
    
    static {
        loadSprites();
    }
    
    private static void loadSprites() {
        sprites = SpriteSheetLoader.loadSpriteRow(
            SPRITE_PATH, 
            WALK_DOWN_ROW, 
            ANIMATION_FRAMES, 
            SPRITE_WIDTH, 
            SPRITE_HEIGHT
        );
    }

    public static boolean spritesLoaded() {
        return sprites != null;
    }

    public EnemyProjectile(char letter, int startX, int startY, int targetX, int targetY, int speed) {
        super(String.valueOf(letter), startX, 0.0, sprites, 1);
        this.x = startX;
        this.y = startY;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double mag = Math.sqrt(dx*dx + dy*dy);
        if (mag > 0) {
            this.vx = (int)((dx / mag) * speed);
            this.vy = (int)((dy / mag) * speed);
        } else {
            this.vx = 0;
            this.vy = -speed;
        }

        this.z = 1.0;
    }

    @Override
    public void update() {
        this.x += vx;
        this.y += vy;
        
        /* Targeting 

        if (this.x < -50 || this.x > TypeSurvivors.TypeSurvivors.gameWidth + 50 || this.y < -50 || this.y > TypeSurvivors.TypeSurvivors.gameHeight + 50) {
            this.expired = true;
        }
        */
    }

    public boolean isExpired() {
        return expired;
    }

    public void destroy() {
        this.expired = true;
    }

    @Override
    public int getScaledWidth() {
    double normalized = (double)EnemyConfig.REFERENCE_PROJECTILE_WIDTH;
        int sw = getSpriteWidth();
        if (sw <= 0) return 0;
        double scaleFactor = normalized / (double)sw;
        return (int)(sw * this.scale * scaleFactor);
    }

    @Override
    public int getScaledHeight() {
    double normalized = (double)EnemyConfig.REFERENCE_PROJECTILE_WIDTH;
        int sw = getSpriteWidth();
        if (sw <= 0) return 0;
        double scaleFactor = normalized / (double)sw;
        return (int)(getSpriteHeight() * this.scale * scaleFactor);
    }
}
