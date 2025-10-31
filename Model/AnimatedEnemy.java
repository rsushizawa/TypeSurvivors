package Model;

import java.awt.image.BufferedImage;

public abstract class AnimatedEnemy extends Enemy {
    protected BufferedImage[] sprites;
    protected int currentFrame;
    protected int animationSpeed;
    protected int animationCounter;

    public AnimatedEnemy(String text, int x, int y, BufferedImage[] sprites, int animationSpeed) {
        super(text, x, y);
        this.sprites = sprites;
        this.animationSpeed = animationSpeed;
        this.currentFrame = 0;
        this.animationCounter = 0;
    }

    public void updateAnimation() {
        if (sprites == null || sprites.length == 0) {
            return;
        }
        
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            currentFrame = (currentFrame + 1) % sprites.length;
        }
    }

    public BufferedImage getCurrentSprite() {
        if (sprites == null || sprites.length == 0) {
            return null;
        }
        return sprites[currentFrame];
    }

    public boolean hasSprites() {
        return sprites != null && sprites.length > 0;
    }
}