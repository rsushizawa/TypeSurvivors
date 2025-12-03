package Entity.Enemy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VespaProjectile extends EnemyProjectile {

    private static BufferedImage[] vespaSprites = null;

    static {
        try {
            BufferedImage img = ImageIO.read(new File("Assets/Enemy/ferrao projetil.png"));
            vespaSprites = new BufferedImage[] { img };
        } catch (IOException e) {
            vespaSprites = null;
        }
    }

    public VespaProjectile(char letter, int startX, int startY, int targetX, int targetY, int speed) {
        super(letter, startX, startY, targetX, targetY, speed, vespaSprites);
    }
}
