package Entity.Projectile;

import GameObject.GameObject;
import TypeSurvivors.TypeSurvivors;

public class Projectile extends GameObject {
    
    private int damage;
    private static final int PROJECTILE_SPEED = 15;

    public Projectile(int x, int y, int targetX, int targetY, int damage) {
        super(x, y);
        
        double dx = targetX - x;
        double dy = targetY - y;
        double magnitude = Math.sqrt(dx * dx + dy * dy);

        if (magnitude > 0) {
            this.velocityX = (int)((dx / magnitude) * PROJECTILE_SPEED);
            this.velocityY = (int)((dy / magnitude) * PROJECTILE_SPEED);
        } else {
            this.velocityX = 0;
            this.velocityY = -PROJECTILE_SPEED;
        }

        this.damage = damage;
    }

    @Override
    public void update() {
        super.updatePosition();
        
        if (y < 0 || y > TypeSurvivors.gameHeight || x < 0 || x > TypeSurvivors.gameWidth) {
            deactivate();
        }
    }
    
    public int getDamage() {
        return damage;
    }
}