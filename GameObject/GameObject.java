package GameObject;


public abstract class GameObject {
    public int x, y;
    public int velocityX, velocityY;
    public boolean active;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.active = true;
    }

  
    public abstract void update();


    public void updatePosition() {
        x += velocityX;
        y += velocityY;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}