package Model;

import java.util.ArrayList;
import Model.Enums.*;

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

        if (targetEnemy.text.isEmpty()) {
            resetTarget();
            return TypingResult.DESTROYED;
        } else {
            return TypingResult.HIT;
        }
    }

    public TypingResult handleKeyTyped(char c, ArrayList<Enemy> allEnemies) {
        if (targetEnemy == null) {
            Enemy bestMatch = null;
            for (Enemy enemy : allEnemies) {
                if (enemy.text.startsWith(String.valueOf(c))) {
                    if (bestMatch == null || enemy.y > bestMatch.y) {
                        bestMatch = enemy;
                    }
                }
            }

            if (bestMatch != null) {
                targetEnemy = bestMatch;
                displayTypedWord = String.valueOf(c);
                return damageEnemy();
            } else {
                return TypingResult.MISS;
            }
        } else {
            if (targetEnemy.text.startsWith(String.valueOf(c))) {
                displayTypedWord += c;
                return damageEnemy();
            } else {
                resetTarget();
                return TypingResult.MISS;
            }
        }
    }
}