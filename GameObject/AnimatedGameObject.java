package GameObject;

import Animation.AnimatedSprite;
import java.awt.image.BufferedImage;

public abstract class AnimatedGameObject extends GameObject {
    protected AnimatedSprite animatedSprite;

    public AnimatedGameObject(int x, int y, BufferedImage[] sprites, int animationSpeed, boolean loopAnimation) {
        super(x, y);
        this.animatedSprite = new AnimatedSprite(sprites, animationSpeed, loopAnimation);
    }

    @Override
    public void update() {
        updatePosition();
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

    public void resetAnimation() {
        if (animatedSprite != null) {
            animatedSprite.resetAnimation();
        }
    }

    public void setSprites(BufferedImage[] newSprites) {
        if (animatedSprite != null) {
            animatedSprite.setSprites(newSprites);
        }
    }

    public boolean isAnimationFinished() {
        return animatedSprite != null && animatedSprite.isAnimationFinished();
    }

    public int getSpriteWidth() {
        return animatedSprite != null ? animatedSprite.getSpriteWidth() : 0;
    }

    public int getSpriteHeight() {
        return animatedSprite != null ? animatedSprite.getSpriteHeight() : 0;
    }
}