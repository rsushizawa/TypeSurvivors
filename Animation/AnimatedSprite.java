package Animation;

import java.awt.image.BufferedImage;

public class AnimatedSprite {
    protected BufferedImage[] sprites;
    protected int currentFrame;
    protected int animationSpeed;
    protected int animationCounter;
    protected boolean loopAnimation;
    protected boolean animationFinished;

    public AnimatedSprite(BufferedImage[] sprites, int animationSpeed, boolean loopAnimation) {
        this.sprites = sprites;
        this.animationSpeed = animationSpeed;
        this.loopAnimation = loopAnimation;
        this.currentFrame = 0;
        this.animationCounter = 0;
        this.animationFinished = false;
    }

    public void updateAnimation() {
        if (sprites == null || sprites.length == 0 || animationFinished) {
            return;
        }
        
        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            currentFrame++;
            
            if (currentFrame >= sprites.length) {
                if (loopAnimation) {
                    currentFrame = 0;
                } else {
                    currentFrame = sprites.length - 1;
                    animationFinished = true;
                }
            }
        }
    }

    public void setLoopAnimation(boolean loop) {
        this.loopAnimation = loop;
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

    public boolean isAnimationFinished() {
        return animationFinished;
    }

    public void resetAnimation() {
        currentFrame = 0;
        animationCounter = 0;
        animationFinished = false;
    }

    public void setSprites(BufferedImage[] newSprites) {
        this.sprites = newSprites;
        resetAnimation();
    }

    public int getSpriteWidth() {
        if (hasSprites()) {
            return sprites[0].getWidth();
        }
        return 0;
    }

    public int getSpriteHeight() {
        if (hasSprites()) {
            return sprites[0].getHeight();
        }
        return 0;
    }
}