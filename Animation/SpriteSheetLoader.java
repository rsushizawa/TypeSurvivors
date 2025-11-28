package Animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SpriteSheetLoader {
    
    public static BufferedImage[] loadSpriteRow(String filepath, int row, int cols, int spriteWidth, int spriteHeight) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(filepath));
            BufferedImage[] sprites = new BufferedImage[cols];
            
            for (int i = 0; i < cols; i++) {
                sprites[i] = spriteSheet.getSubimage(
                    i * spriteWidth, 
                    row * spriteHeight, 
                    spriteWidth, 
                    spriteHeight
                );
            }
            
            return sprites;
        } catch (IOException e) {
            System.err.println("Error loading sprite sheet: " + filepath);
            e.printStackTrace();
            return null;
        }
    }
    
    public static BufferedImage[] loadFullSpriteSheet(String filepath, int rows, int cols, int spriteWidth, int spriteHeight) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(filepath));
            BufferedImage[] sprites = new BufferedImage[rows * cols];
            
            int index = 0;
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    sprites[index++] = spriteSheet.getSubimage(
                        col * spriteWidth, 
                        row * spriteHeight, 
                        spriteWidth, 
                        spriteHeight
                    );
                }
            }
            
            return sprites;
        } catch (IOException e) {
            System.err.println("Error loading sprite sheet: " + filepath);
            e.printStackTrace();
            return null;
        }
    }
}