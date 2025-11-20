package Entity.Projectile;

import GameObject.GameObject;
import TypeSurvivors.TypeSurvivors;

public class Projectile extends GameObject {
    
    private int damage;
    private static final int PROJECTILE_SPEED = 100;
    private boolean enemyOwned = false;

    public Projectile(int x, int y, int targetX, int targetY, int damage) {
        this(x, y, targetX, targetY, damage, PROJECTILE_SPEED, false);
    }

    public Projectile(int x, int y, int targetX, int targetY, int damage, int speed, boolean enemyOwned) {
        super(x, y);

        double dx = targetX - x;
        double dy = targetY - y;
        double magnitude = Math.sqrt(dx * dx + dy * dy);

        if (magnitude > 0) {
            this.velocityX = (int)((dx / magnitude) * speed);
            this.velocityY = (int)((dy / magnitude) * speed);
        } else {
            this.velocityX = 0;
            this.velocityY = -speed;
        }

        this.damage = damage;
        this.enemyOwned = enemyOwned;
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

    public boolean isEnemyOwned() {
        return enemyOwned;
    }
}