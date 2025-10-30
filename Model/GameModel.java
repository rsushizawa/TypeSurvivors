package Model;

import java.util.ArrayList;

public class GameModel {

    private int score = 0;
    private int lives = 5;
    private boolean isGameOver = false;

    private final WaveManager waveManager;
    private final EnemyManager enemyManager;
    private final TypingManager typingManager;
    private final GameStats gameStats;
    
    public static final int GAME_SPEED_MS = 16;

    public GameModel(int gameWidth, int gameHeight) {
        this.waveManager = new WaveManager();
        this.enemyManager = new EnemyManager(gameWidth, gameHeight);
        this.typingManager = new TypingManager();
        this.gameStats = new GameStats();
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            isGameOver = true;
        }
    }

    public void updateGameState() {
        if (isGameOver) return;

        if (waveManager.getWaveState() != WaveState.INTERMISSION) {
            gameStats.incrementGameTicks();
        }

        if (waveManager.update()) {
            score += 100;
        }

        switch (waveManager.getWaveState()) {
            case SPAWNING:
                if (waveManager.canSpawnEnemy()) {
                    if (enemyManager.trySpawnEnemy(waveManager.getWaveSpawnChance())) {
                        waveManager.notifyEnemySpawned();
                    }
                }
                updateAndCheckLostEnemies();
                break;
                
            case WAITING_FOR_CLEAR:
                updateAndCheckLostEnemies();
                if (enemyManager.isEmpty()) {
                    waveManager.startIntermission();
                }
                break;
                
            case INTERMISSION:
                break;
        }
    }

    private void updateAndCheckLostEnemies() {
        ArrayList<Enemy> lostEnemies = enemyManager.updateEnemies(waveManager.getWaveSpeedPixels());
        for (Enemy lostEnemy : lostEnemies) {
            loseLife();
            typingManager.checkTargetLost(lostEnemy);
        }
    }

    public void appendTypedCharacter(char c) {
        if (isGameOver) return;
        
        Enemy preHitTarget = typingManager.getTargetEnemy();
        TypingResult result = typingManager.handleKeyTyped(c, enemyManager.getEnemies());

        if (result == TypingResult.HIT) {
            gameStats.incrementCharsTyped(1);
        } else if (result == TypingResult.DESTROYED) {
            gameStats.incrementCharsTyped(1);
            Enemy destroyedEnemy = (preHitTarget != null) ? preHitTarget : typingManager.getTargetEnemy();
            if (destroyedEnemy != null) {
                score += destroyedEnemy.originalText.length();
                enemyManager.removeEnemy(destroyedEnemy);
            }
        }
    }

    public void backspaceTypedWord() {
        if (isGameOver) return;
        typingManager.handleBackspace();
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemyManager.getEnemies();
    }

    public Enemy getTargetEnemy() {
        return typingManager.getTargetEnemy();
    }

    public String getDisplayTypedWord() {
        return typingManager.getDisplayTypedWord();
    }

    public int getWaveNumber() {
        return waveManager.getWaveNumber();
    }

    public WaveState getWaveState() {
        return waveManager.getWaveState();
    }

    public int getIntermissionTickCounter() {
        return waveManager.getIntermissionTickCounter();
    }

    public int getWPM() {
        return gameStats.getWPM();
    }
}