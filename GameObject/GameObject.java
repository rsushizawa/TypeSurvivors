package GameObject;


public abstract class GameObject {
    public int x, y;
    // Support fractional velocities/positions for smooth movement
    public double posX, posY;
    public double velocityX, velocityY;
    public boolean active;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.posX = x;
        this.posY = y;
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.active = true;
    }

  
    public abstract void update();


    public void updatePosition() {
        posX += velocityX;
        posY += velocityY;
        x = (int) Math.round(posX);
        y = (int) Math.round(posY);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public abstract void render(java.awt.Graphics2D g, Model.GameModel model);
}