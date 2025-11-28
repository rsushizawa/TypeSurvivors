package Manager;

import Entity.Enemy.Enemy;
import Data.TypingResult;
import java.util.ArrayList;

public class TypingManager {

    private Enemy targetEnemy = null;
    private String displayTypedWord = "";

    public Enemy getTargetEnemy() {
        return targetEnemy;
    }

    public String getDisplayTypedWord() {
        return displayTypedWord;
    }

    public void reset() {
        targetEnemy = null;
        displayTypedWord = "";
    }

    private void resetTarget() {
        targetEnemy = null;
        displayTypedWord = "";
    }

    public void handleBackspace() {
        resetTarget();
    }

    public void checkTargetLost(Enemy lostEnemy) {
        if (targetEnemy == lostEnemy) {
            resetTarget();
        }
    }

    private TypingResult damageEnemy() {
        targetEnemy.text = targetEnemy.text.substring(1);
    
        try {
            targetEnemy.z = Math.max(0.0, targetEnemy.z - Config.EnemyConfig.ENEMY_HIT_PUSHBACK);
        } catch (Exception ignored) {}

        if (targetEnemy.text.isEmpty()) {
            return TypingResult.DESTROYED;
        } else {
            return TypingResult.HIT;
        }
    }

    public TypingResult handleKeyTyped(char c, ArrayList<Enemy> allEnemies) {
        char lowerC = Character.toLowerCase(c);
        
        if (targetEnemy == null) {
            Enemy bestMatch = null;
            for (Enemy enemy : allEnemies) {
                if (!enemy.text.isEmpty() && enemy.text.charAt(0) == lowerC) {
                    if (bestMatch == null || enemy.z > bestMatch.z) {
                        bestMatch = enemy;
                    }
                }
            }

            if (bestMatch != null) {
                targetEnemy = bestMatch;
                displayTypedWord = String.valueOf(lowerC);
                return damageEnemy();
            } else {
                return TypingResult.MISS;
            }
        } else {
            if (!targetEnemy.text.isEmpty() && targetEnemy.text.charAt(0) == lowerC) {
                displayTypedWord += lowerC;
                TypingResult result = damageEnemy();
                if (result == TypingResult.DESTROYED) {
                    resetTarget();
                }
                return result;
            } else {
                return TypingResult.MISS;
            }
        }
    }
}