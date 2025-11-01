package Animation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class for loading sprites from sprite sheets
 * Supports loading individual rows or entire sheets
 */
public class SpriteSheetLoader {
    
    /**
     * Load a single row of sprites from a sprite sheet
     * @param filepath Path to the sprite sheet image
     * @param row Which row to extract (0-indexed)
     * @param cols Number of columns (sprites) in the row
     * @param spriteWidth Width of each sprite in pixels
     * @param spriteHeight Height of each sprite in pixels
     * @return Array of sprites, or null if loading fails
     */
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
    
    /**
     * Load an entire sprite sheet into a single array
     * @param filepath Path to the sprite sheet image
     * @param rows Number of rows in the sheet
     * @param cols Number of columns in the sheet
     * @param spriteWidth Width of each sprite in pixels
     * @param spriteHeight Height of each sprite in pixels
     * @return Array of all sprites (row-major order), or null if loading fails
     */
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