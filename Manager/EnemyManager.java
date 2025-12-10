package Manager;

import Entity.Enemy.Enemy;
import Config.EnemyConfig;
import Entity.Enemy.AranhaEnemy;
import Entity.Enemy.AranhaProjectile;
import Entity.Enemy.LouvaDeusEnemy;
import Entity.Enemy.OrcEnemy;
import Entity.Enemy.VespaEnemy;
import Entity.Enemy.VespaProjectile;
import Audio.AudioManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

public class EnemyManager {

    private static final ArrayList<ArrayList<String>> WORDS_BY_LENGTH = new ArrayList<>();
    private static final int MAX_WORD_LENGTH_SUPPORTED = 30;

    static{
        readCSV();
    }

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    private final int gameWidth;
    private final int gameHeight;

    public EnemyManager(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public static void readCSV(){
        for (int i = 0; i < MAX_WORD_LENGTH_SUPPORTED; i++) {
            WORDS_BY_LENGTH.add(new ArrayList<String>());
        }

    String csvFile = Config.PathsConfig.DICTIONARY_CSV;
    try (Scanner scanner = new Scanner(new File(csvFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim().toLowerCase();
                if (!line.isEmpty() && line.length() < MAX_WORD_LENGTH_SUPPORTED && line.matches("^[a-z]+$")) {
                    WORDS_BY_LENGTH.get(line.length()).add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getWordForWave(int waveNumber) {
        int minLength = 4 + (waveNumber / 3);
        int maxLength = 6 + (waveNumber / 2);
        
        maxLength = Math.min(maxLength, MAX_WORD_LENGTH_SUPPORTED - 1);
        minLength = Math.min(minLength, maxLength);

        int wordLength = random.nextInt((maxLength - minLength) + 1) + minLength;
        
        int originalLength = wordLength;
        int offset = 0;
        while (true) {
            if (wordLength < MAX_WORD_LENGTH_SUPPORTED && !WORDS_BY_LENGTH.get(wordLength).isEmpty()) {
                break;
            }
            
            offset++;
            wordLength = originalLength + offset;
            if (wordLength < MAX_WORD_LENGTH_SUPPORTED && !WORDS_BY_LENGTH.get(wordLength).isEmpty()) {
                break;
            }

            wordLength = originalLength - offset;
            if (wordLength > 1 && !WORDS_BY_LENGTH.get(wordLength).isEmpty()) {
                break;
            }

            if (offset > 5) {
                wordLength = 3;
                break;
            }
        }
        
        if (WORDS_BY_LENGTH.get(wordLength).isEmpty()) {
            return "error";
        }

        ArrayList<String> wordList = WORDS_BY_LENGTH.get(wordLength);
        return wordList.get(random.nextInt(wordList.size()));
    }


    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isEmpty() {
        return enemies.isEmpty();
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public boolean trySpawnEnemy(int spawnChance, int baseSpeedPixels, int waveNumber) {
        if (random.nextInt(spawnChance) == 0) {
            if (!OrcEnemy.spritesLoaded()) {
                System.err.println("Warning: Orc sprites not loaded. Cannot spawn enemies.");
                return false;
            }

            String text = getWordForWave(waveNumber);
            
            double worldX = random.nextInt(gameWidth - (2 * EnemyConfig.SPAWN_BORDER)) + EnemyConfig.SPAWN_BORDER;
            
            int minSpeed = Math.max(1, (int)(baseSpeedPixels * 0.7));
            int maxSpeed = (int)(baseSpeedPixels * 1.3) + 1;
            int pixelSpeed = random.nextInt(maxSpeed - minSpeed) + minSpeed;
            
            int effectiveGameHeight = Enemy.PLAYER_Y_LINE - Enemy.HORIZON_Y;
            double zSpeed = (double)pixelSpeed / effectiveGameHeight;
            zSpeed *= EnemyConfig.ENEMY_SPEED_MULTIPLIER;
            
            double worldSpeedX = (random.nextDouble() * 4.0 + 2.0) * (random.nextBoolean() ? 1 : -1);

            Enemy newEnemy;
            int enemyType = random.nextInt(3);

            switch (enemyType) {
                case 0: 
                    newEnemy = new AranhaEnemy(text, worldX, zSpeed, pixelSpeed * 2.0);
                    break;
                case 1:
                    newEnemy = new LouvaDeusEnemy(text, worldX, zSpeed, worldSpeedX);
                    break;
                case 2:
                    newEnemy = new VespaEnemy(text, worldX, zSpeed, worldSpeedX); 
                    break;
                default:
                    newEnemy = new VespaEnemy(text, worldX, zSpeed, worldSpeedX); 
                    break;
            }
            int margin;
            if (newEnemy instanceof VespaEnemy || newEnemy instanceof LouvaDeusEnemy) {
                margin = EnemyConfig.MARGIN_LARGE; // big sprites
            } else if (newEnemy instanceof AranhaEnemy) {
                margin = EnemyConfig.MARGIN_MEDIUM; // medium sprites
            } else {
                margin = EnemyConfig.MARGIN_SMALL; // default small margin
            }

            newEnemy.MIN_WIDTH = margin;
            newEnemy.MAX_WIDTH = Math.max(margin + 10, gameWidth - margin);

            enemies.add(newEnemy);
            return true;
        }
        return false;
    }

    public ArrayList<Enemy> updateEnemies(int wallYPosition, Model.GameModel model) {
        ArrayList<Enemy> lostEnemies = new ArrayList<>();
        Iterator<Enemy> iter = enemies.iterator();
        
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            
            // Allow enemy to react to the model before standard update (special attacks / AI)
            enemy.onModelUpdate(model);

            // Special handling for one-letter enemy-projectiles (they move in screen space)
            if (enemy instanceof Entity.Enemy.EnemyProjectile) {
                Entity.Enemy.EnemyProjectile ep = (Entity.Enemy.EnemyProjectile) enemy;
                ep.update();
                // Debug: if projectile gets reasonably close to player, log its position
                if (model.getPlayer() != null) {
                    int px = model.getPlayer().x + (model.getPlayer().getSpriteWidth() / 2);
                    int py = model.getPlayer().y + (model.getPlayer().getSpriteHeight() / 2);
                    int dxClose = ep.x - px;
                    int dyClose = ep.y - py;
                    int closeThresh = 200;
                    if (dxClose * dxClose + dyClose * dyClose <= closeThresh * closeThresh) {
                        System.out.println(String.format("[DEBUG] EnemyProjectile near player at (%d,%d) dx=%d dy=%d", ep.x, ep.y, dxClose, dyClose));
                    }
                }
                // Damage is triggered when the letter reaches the danger line (player line),
                // not necessarily by direct overlap with the player's bounding circle.
                if (ep.y >= Entity.Enemy.Enemy.PLAYER_Y_LINE) {
                    // If this mini-enemy was the current typing target, clear typing state
                    model.resetTypingIfTarget(ep);
                    // Special-case projectiles with status effects
                    if (ep instanceof AranhaProjectile) {
                        // Aranha: stun the player for 0.5 - 2.0 seconds
                        double stunSec = 0.5 + (random.nextDouble() * 1.5);
                        model.applyStun(stunSec);
                            // Play stun SFX
                            AudioManager.playStunSfx();
                        System.out.println(String.format("[DEBUG] AranhaProjectile stunned player for %.2fs", stunSec));
                        iter.remove();
                        continue;
                    } else if (ep instanceof VespaProjectile) {
                        // Vespa: apply poison window (10s)
                        model.applyPoisonWindow(10.0);
                        AudioManager.playDamagePerSecondSfx();
                        System.out.println("[DEBUG] VespaProjectile applied poison window (10s)");
                        iter.remove();
                        continue;
                    }

                    // Default behavior: damage player
                    model.loseLife();
                    System.out.println(String.format("[DEBUG] EnemyProjectile reached line at (%d,%d). Lives now: %d", ep.x, ep.y, model.getLives()));
                    iter.remove();
                    continue;
                }

                if (ep.isExpired()) {
                    iter.remove();
                }
                continue;
            }

            // Wall logic
            if (wallYPosition > 0 && enemy.y >= wallYPosition) {
                enemy.updateAnimation(); 
                continue; 
            }

            enemy.update();
            
            if (enemy.z >= 1.0) {
                iter.remove();
                lostEnemies.add(enemy);
            }
        }
        return lostEnemies;
    }
}