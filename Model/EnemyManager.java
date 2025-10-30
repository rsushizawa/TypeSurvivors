package Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class EnemyManager {

    private final String[] WORD_LIST = {
        "java", "swing", "model", "view", "controller", "event",
        "pixel", "array", "string", "class", "object", "method", "logic"
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

    public boolean trySpawnEnemy(int spawnChance) {
        if (random.nextInt(spawnChance) == 0) {
            String text = WORD_LIST[random.nextInt(WORD_LIST.length)];
            int wordWidth = text.length() * 10; 
            int x = random.nextInt(Math.max(10, gameWidth - wordWidth - 20)) + 10;
            
            enemies.add(new Enemy(text, x, 0));
            return true;
        }
        return false;
    }

    public ArrayList<Enemy> updateEnemies(int speed) {
        ArrayList<Enemy> lostEnemies = new ArrayList<>();
        Iterator<Enemy> iter = enemies.iterator();
        
        while (iter.hasNext()) {
            Enemy enemy = iter.next();
            enemy.y += speed;
            
            if (enemy.y > gameHeight) {
                iter.remove();
                lostEnemies.add(enemy);
            }
        }
        return lostEnemies;
    }
}
