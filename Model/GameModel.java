package Model;

import Entity.Enemy.Enemy;
import Entity.Player.Player;
import Entity.Projectile.Projectile;
import Manager.EnemyManager;
import Manager.TypingManager;
import Manager.WaveManager;
import Manager.LeaderboardManager;
import Data.GameState;
import Data.WaveState;
import Data.TypingResult;
import Data.GameStats;

import java.util.ArrayList;
import java.util.Iterator;

public class GameModel {

    private int score = 0;
    private int lives = 5;
    private GameState gameState = GameState.MAIN_MENU;
    private String playerName = "";

    private final WaveManager waveManager;
    private final EnemyManager enemyManager;
    private final TypingManager typingManager;
    private final GameStats gameStats;
    private final LeaderboardManager leaderboardManager;
    
    // Player and Projectiles
    private final Player player;
    private final ArrayList<Projectile> projectiles;
    
    public static final int GAME_SPEED_MS = 16;
    public static final int MAX_NAME_LENGTH = 10;

    public GameModel(int gameWidth, int gameHeight) {
        this.waveManager = new WaveManager();
        this.enemyManager = new EnemyManager(gameWidth, gameHeight);
        this.typingManager = new TypingManager();
        this.gameStats = new GameStats();
        this.leaderboardManager = new LeaderboardManager();
        
        // Initialize Player and Projectiles
        this.player = new Player(gameWidth / 2 - 32, gameHeight - 120); // 32 is half sprite width
        this.projectiles = new ArrayList<>();
    }

    public void startNewGame() {
        score = 0;
        lives = 5;
        playerName = "";
        gameState = GameState.PLAYING;
        waveManager.reset();
        enemyManager.getEnemies().clear();
        projectiles.clear();
        typingManager.reset();
        gameStats.reset();
    }

    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }

    public void returnToMenu() {
        gameState = GameState.MAIN_MENU;
        enemyManager.getEnemies().clear();
        projectiles.clear();
        typingManager.reset();
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            if (leaderboardManager.isHighScore(score)) {
                gameState = GameState.ENTERING_NAME;
            } else {
                gameState = GameState.GAME_OVER;
            }
        }
    }

    public void appendToPlayerName(char c) {
        if (playerName.length() < MAX_NAME_LENGTH && Character.isLetterOrDigit(c)) {
            playerName += Character.toUpperCase(c);
        }
    }

    public void backspacePlayerName() {
        if (playerName.length() > 0) {
            playerName = playerName.substring(0, playerName.length() - 1);
        }
    }

    public void submitHighScore() {
        if (playerName.isEmpty()) {
            playerName = "PLAYER";
        }
        leaderboardManager.addHighScore(playerName, score, waveManager.getWaveNumber(), gameStats.getMaxWPM());
        gameState = GameState.GAME_OVER;
    }

    public void updateGameState() {
        if (gameState != GameState.PLAYING) return;

        // Update player
        player.update();

        // Update all projectiles
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update();
            if (!p.isActive()) {
                projIter.remove();
            }
        }
        
        // Update all enemies
        for (Enemy enemy : enemyManager.getEnemies()) {
            enemy.updateAnimation();
        }

        if (waveManager.getWaveState() != WaveState.INTERMISSION) {
            gameStats.incrementGameTicks();
        }

        if (waveManager.update()) {
            score += 100;
        }

        switch (waveManager.getWaveState()) {
            case SPAWNING:
                if (waveManager.canSpawnEnemy()) {
                    // Pass wave number for difficulty scaling
                    if (enemyManager.trySpawnEnemy(waveManager.getWaveSpawnChance(), waveManager.getWaveSpeedPixels(), waveManager.getWaveNumber())) {
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
                // Clear any remaining projectiles
                projectiles.clear();
                break;
        }
    }

    private void updateAndCheckLostEnemies() {
        ArrayList<Enemy> lostEnemies = enemyManager.updateEnemies();
        for (Enemy lostEnemy : lostEnemies) {
            loseLife();
            typingManager.checkTargetLost(lostEnemy);
        }
    }
    
    /**
     * Spawns a projectile from the player towards the target enemy.
     * @param target The enemy to shoot at.
     */
    private void spawnProjectile(Enemy target) {
        if (target == null) return;

        // Calculate start position (center of player sprite)
        int startX = player.x + (player.getSpriteWidth() / 2);
        int startY = player.y;

        // Calculate target position (center of enemy sprite)
        int targetX = target.x; // Already perspective-centered
        int targetY = target.y - (target.getScaledHeight() / 2); // Aim for vertical center

        projectiles.add(new Projectile(startX, startY, targetX, targetY, 1));
    }

    public void appendTypedCharacter(char c) {
        if (gameState != GameState.PLAYING) return;
        
        // Get target *before* handling the key
        Enemy preHitTarget = typingManager.getTargetEnemy();
        
        TypingResult result = typingManager.handleKeyTyped(c, enemyManager.getEnemies());

        // Get target *after* handling the key (it might have been set)
        Enemy postHitTarget = typingManager.getTargetEnemy();
        
        // Use the post-hit target, or if null (word destroyed), use the pre-hit target
        Enemy targetToShootAt = (postHitTarget != null) ? postHitTarget : preHitTarget;

        if (result == TypingResult.HIT) {
            gameStats.incrementCharsTyped(1);
            spawnProjectile(targetToShootAt); // Shoot on HIT
        } else if (result == TypingResult.DESTROYED) {
            gameStats.incrementCharsTyped(1);
            spawnProjectile(targetToShootAt); // Shoot on DESTROYED
            
            if (targetToShootAt != null) {
                score += targetToShootAt.originalText.length();
                enemyManager.removeEnemy(targetToShootAt);
            }
        }
    }

    public void backspaceTypedWord() {
        if (gameState != GameState.PLAYING) return;
        typingManager.handleBackspace();
    }

    // --- Getters ---
    
    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isGameOver() {
        return gameState == GameState.GAME_OVER;
    }

    public String getPlayerName() {
        return playerName;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemyManager.getEnemies();
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
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

    public int getMaxWPM() {
        return gameStats.getMaxWPM();
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }
}