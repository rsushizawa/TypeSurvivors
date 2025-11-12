package Manager;

import Entity.Enemy.Enemy;
import Entity.Enemy.AranhaEnemy;
import Entity.Enemy.LouvaDeusEnemy;
import Entity.Enemy.OrcEnemy;
import Entity.Enemy.VespaEnemy;

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

        String csvFile = "Assets/dict-correto.csv";
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

    /**
     * Gets a random word based on the current wave number.
     * @param waveNumber The current wave number.
     * @return A word.
     */
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
            
            double worldX = random.nextInt(gameWidth - 100) + 50.0;
            
            int minSpeed = Math.max(1, (int)(baseSpeedPixels * 0.7));
            int maxSpeed = (int)(baseSpeedPixels * 1.3) + 1;
            int pixelSpeed = random.nextInt(maxSpeed - minSpeed) + minSpeed;
            
            int effectiveGameHeight = Enemy.PLAYER_Y_LINE - Enemy.HORIZON_Y;
            double zSpeed = (double)pixelSpeed / effectiveGameHeight;
            
            double worldSpeedX = (random.nextDouble() * 4.0 + 2.0) * (random.nextBoolean() ? 1 : -1);

            Enemy newEnemy;
            int enemyType = random.nextInt(3);
            
            switch (enemyType) {
                case 0: 
                    newEnemy = new OrcEnemy(text, worldX, zSpeed, 0);
                    break;
                case 1: 
                    newEnemy = new AranhaEnemy(text, worldX, zSpeed, pixelSpeed * 2.0);
                    break;
                case 2: 
                    newEnemy = new LouvaDeusEnemy(text, worldX, zSpeed, worldSpeedX);
                default:
                    newEnemy = new VespaEnemy(text, worldX, zSpeed, worldSpeedX); 
                    break;
            }
            enemies.add(newEnemy);
            return true;
        }
        return false;
    }

    public ArrayList<Enemy> updateEnemies() {
        ArrayList<Enemy> lostEnemies = new ArrayList<>();
        Iterator<Enemy> iter = enemies.iterator();
        
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.update();
                     
            if (enemy.z >= 1.0) {
                iter.remove();
                lostEnemies.add(enemy);
            }
        }
        return lostEnemies;
    }
}