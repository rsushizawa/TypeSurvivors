package Manager;

import Entity.Enemy.Enemy;
import Entity.Enemy.OrcEnemy;
import Entity.Enemy.ZigZagEnemy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class EnemyManager {

    private final String[] ORC_WORDS = {
        "bash", "smash", "crush", "grind", "maul", "fight",
        "rage", "blood", "storm", "thunder", "iron", "bone",
        "grunt", "wreck", "slash", "pound", "brawl", "might"
    };

    private final ArrayList<Enemy> enemies = new ArrayList<>();
    private final Random random = new Random();
    private final int gameWidth;
    private final int gameHeight;

    public EnemyManager(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
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
            if (!OrcEnemy.spritesLoaded()) {
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
                case 0: newEnemy = new OrcEnemy(text, x, 40, speedVariation); break;
                case 1: newEnemy = new ZigZagEnemy(text, x, 40, speedVariation); break;
                default: newEnemy = new OrcEnemy(text, x, 40, speedVariation);
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
            enemy.y += enemy.speedy;
            enemy.x += enemy.speedx;
            if(enemy.x>enemy.MAX_WIDTH|| enemy.x<enemy.MIN_WIDTH){
                enemy.speedx = -enemy.speedx;
                enemy.MAX_WIDTH = random.nextInt(gameWidth/2,gameWidth-40);
                enemy.MIN_WIDTH = random.nextInt(10,gameWidth/2);
            }
                     
            if (enemy.y > gameHeight) {
                iter.remove();
                lostEnemies.add(enemy);
            }
        }
        return lostEnemies;
    }
}