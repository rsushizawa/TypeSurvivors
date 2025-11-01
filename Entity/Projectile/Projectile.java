package Entity.Projectile;

import java.awt.image.BufferedImage;

import Animation.SpriteSheetLoader;
import GameObject.AnimatedGameObject;


public class Projectile extends AnimatedGameObject {
    private static BufferedImage[] fireballSprites = null;
    private int damage;
    private int maxDistance;
    private int traveledDistance;
    
    static {
        loadProjectileSprites();
    }
    
    private static void loadProjectileSprites() {
        fireballSprites = SpriteSheetLoader.loadSpriteRow(
            "fireball_spritesheet.png",
            0,
            4,
            16,
            16
        );
    }

    public Projectile(int x, int y, int velocityX, int velocityY, int damage) {
        super(x, y, fireballSprites, 4, true); // Fast animation, looping
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.damage = damage;
        this.maxDistance = 500;
        this.traveledDistance = 0;
    }

    @Override
    public void update() {
        super.update();
        
        traveledDistance += Math.abs(velocityX) + Math.abs(velocityY);
        
        // Deactivate if traveled too far
        if (traveledDistance >= maxDistance) {
            deactivate();
        }
    }
    
    public int getDamage() {
        return damage;
    }
    
    public static boolean spritesLoaded() {
        return fireballSprites != null;
    }
}