package Model;

public class Enemy {
    public String text;
    public final String originalText;
    public int x, y;

    Enemy(String text, int x, int y) {
        this.text = text;
        this.originalText = text;
        this.x = x;
        this.y = y;
    }
}
