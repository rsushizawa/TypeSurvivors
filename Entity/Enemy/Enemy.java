package Entity.Enemy;

import Animation.AnimatedSprite;
import java.awt.image.BufferedImage;

public class Enemy {
    public String text;
    public final String originalText;
    public int x, y;
    public int speedx;
    public int speedy;
    public int MAX_WIDTH = 540;
    public int MIN_WIDTH = 0;

    protected AnimatedSprite animatedSprite;

    public Enemy(String text, int x, int y) {
        this.text = text;
        this.originalText = text;
        this.x = x;
        this.y = y;
        this.speedy = 1; 
        this.speedx = 1;
        this.animatedSprite = null; 
    }
    

    protected Enemy(String text, int x, int y, BufferedImage[] sprites, int animationSpeed) {
        this.text = text;
        this.originalText = text;
        this.x = x;
        this.y = y;
        this.speedy = 1;
        this.speedx = 1;
        this.animatedSprite = new AnimatedSprite(sprites, animationSpeed, true);
    }

    public void updateAnimation() {
        if (animatedSprite != null) {
            animatedSprite.updateAnimation();
        }
    }

    public BufferedImage getCurrentSprite() {
        return animatedSprite != null ? animatedSprite.getCurrentSprite() : null;
    }

    public boolean hasSprites() {
        return animatedSprite != null && animatedSprite.hasSprites();
    }

    public int getSpriteWidth() {
        return animatedSprite != null ? animatedSprite.getSpriteWidth() : 0;
    }

    public int getSpriteHeight() {
        return animatedSprite != null ? animatedSprite.getSpriteHeight() : 0;
    }
}