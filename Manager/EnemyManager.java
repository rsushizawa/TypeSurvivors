package Manager;

import Entity.Enemy.Enemy;
import Entity.Enemy.AranhaEnemy;
import Entity.Enemy.VespaEnemy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class EnemyManager {

    static{
        readCSV();
    }

    private static String[] ORC_WORDS;


    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    private final int gameWidth;
    private final int gameHeight;

    public EnemyManager(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public static void readCSV(){
        String csvFile = "Assets/dict-correto.csv";
        List<String> data = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(csvFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                data.add(line);
            }
            String[] newArray = data.toArray(new String[0]);
            ORC_WORDS = newArray;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

    public boolean trySpawnEnemy(int spawnChance, int baseSpeed) {
        if (random.nextInt(spawnChance) == 0) {
            if (!AranhaEnemy.spritesLoaded()) {
                System.err.println("Warning: Orc sprites not loaded. Cannot spawn enemies.");
                return false;
            }

            
            String text = ORC_WORDS[random.nextInt(ORC_WORDS.length)];
            int wordWidth = text.length() * 10; 
            int x = random.nextInt(Math.max(10, gameWidth - wordWidth - 20)) + 10;
            
            int minSpeed = Math.max(1, (int)(baseSpeed * 0.7));
            int maxSpeed = (int)(baseSpeed * 1.3) + 1;
            int speedVariation = random.nextInt(maxSpeed - minSpeed) + minSpeed;
            
            Enemy newEnemy;
            int enemyType = random.nextInt()%2;
            switch (enemyType) {
                case 0: newEnemy = new AranhaEnemy(text, x, 40, speedVariation); System.out.println("Aranha");break;
                case 1: newEnemy = new VespaEnemy(text, x, 40, speedVariation); System.out.println("Vespa"); break;
                default: newEnemy = new AranhaEnemy(text, x, 40, speedVariation);
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

                     
            if (enemy.y > gameHeight) {
                iter.remove();
                lostEnemies.add(enemy);
            }
        }
        return lostEnemies;
    }
}