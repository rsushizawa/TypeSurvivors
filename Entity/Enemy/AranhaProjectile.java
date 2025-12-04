package Entity.Enemy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class AranhaProjectile extends EnemyProjectile {

    private static BufferedImage[] aranhaSprites = null;

    static {
        try {
            BufferedImage img = ImageIO.read(new File("Assets/Enemy/teia.png"));
            aranhaSprites = new BufferedImage[] { img };
        } catch (IOException e) {
            aranhaSprites = null;
        }
    }

    public AranhaProjectile(char letter, int startX, int startY, int targetX, int targetY, int speed) {
        super(letter, startX, startY, targetX, targetY, speed, aranhaSprites);
    }
}
