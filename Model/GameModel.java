package Model;

import Entity.Enemy.Enemy;
import Entity.Player.Player;
import Entity.Projectile.Projectile;
import Manager.EnemyManager;
import Manager.TypingManager;
import Manager.WaveManager;
import Manager.LeaderboardManager;
import Manager.UpgradeManager;
import Data.GameState;
import Data.WaveState;
import Data.TypingResult;
import Data.GameStats;
import Data.Upgrades.Upgrade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import Audio.AudioManager;

public class GameModel {

    private int score = 0;
    private int lives = 5;
    private int playerLevel = 1; // player's level for XP progression
    private GameState gameState = GameState.MAIN_MENU;
    private String playerName = "";

    private final WaveManager waveManager;
    private final EnemyManager enemyManager;
    private final TypingManager typingManager;
    private final GameStats gameStats;
    private final LeaderboardManager leaderboardManager;
    private final UpgradeManager upgradeManager;
    
    private final Player player;
    private final ArrayList<Projectile> projectiles;
    private final ArrayList<GameObject.PoisonWall> poisonWalls = new ArrayList<>();
    private final ArrayList<GameObject.FireBallEffect> fireBallEffects = new ArrayList<>();
    private final Random rand = new Random();
    
    public static final int GAME_SPEED_MS = 16;
    public static final int MAX_NAME_LENGTH = 10;
    
    // Upgrade state fields
    private double fireBallCooldown = 0;
    private double insectSprayCooldown = 0;
    private double splitShotCooldown = 0;
    private double wallCooldown = 0;
    private double wallDuration = 0;
    private int wallYPosition = -1;

    public GameModel(int gameWidth, int gameHeight) {
        this.waveManager = new WaveManager();
        this.enemyManager = new EnemyManager(gameWidth, gameHeight);
        this.typingManager = new TypingManager();
        this.gameStats = new GameStats();
        this.leaderboardManager = new LeaderboardManager();
        this.upgradeManager = new UpgradeManager();
            
        this.player = new Player(gameWidth / 2 - 32, gameHeight - 120);
        this.projectiles = new ArrayList<>();
    }

    public void startNewGame() {
        score = 0;
        lives = 5;
        playerLevel = 1;
        playerName = "";
        gameState = GameState.PLAYING;
        waveManager.reset();
        enemyManager.getEnemies().clear();
        projectiles.clear();
        typingManager.reset();
        gameStats.reset();
        
        // Create a new UpgradeManager to reset all progress
        // Or add a reset() method to UpgradeManager
        // this.upgradeManager.reset(); 
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
        AudioManager.playMainMenuMusic();
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            AudioManager.stopAllMusic();
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
    // If level-up choice is open, skip updates
    if (gameState == GameState.LEVEL_UP_CHOICE) return;
        if (gameState != GameState.PLAYING) return;

        player.update();

        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update();
            if (!p.isActive()) {
                projIter.remove();
            }
        }
        
        for (Enemy enemy : enemyManager.getEnemies()) {
            enemy.updateAnimation();
        }

        if (waveManager.getWaveState() != WaveState.INTERMISSION) {
            gameStats.incrementGameTicks();
        }

    // Update upgrade cooldowns
        double delta = GAME_SPEED_MS / 1000.0; // Time in seconds
        if (fireBallCooldown > 0) fireBallCooldown -= delta;
        if (insectSprayCooldown > 0) insectSprayCooldown -= delta;
        if (splitShotCooldown > 0) splitShotCooldown -= delta;
        
        // Handle Wall Logic
        if (upgradeManager.hasUpgrade("Wall")) {
            if (wallCooldown > 0) {
                wallCooldown -= delta;
            } else if (wallDuration <= 0) { // Cooldown is done, wall not active
                // Activate Wall
                wallDuration = upgradeManager.getUpgrade("Wall").getParam1Value();
                wallCooldown = upgradeManager.getUpgrade("Wall").getParam2Value();
                // Position wall based on player and distance upgrade
                wallYPosition = player.y - (int)upgradeManager.getUpgrade("Wall").getParam3Value();
            }
            
            if (wallDuration > 0) {
                wallDuration -= delta;
                if (wallDuration <= 0) {
                    wallYPosition = -1; // Deactivate
                }
            }
        }
        // --- End Upgrade Update ---

    // Update poison walls
        for (GameObject.PoisonWall pw : new ArrayList<>(poisonWalls)) {
            pw.update(delta, enemyManager.getEnemies(), this);
            if (pw.isExpired()) {
                poisonWalls.remove(pw);
            }
        }
        // --- End Poison Walls ---
    // Update fireball effects
        for (GameObject.FireBallEffect f : new ArrayList<>(fireBallEffects)) {
            f.update(delta);
            if (f.isExpired()) fireBallEffects.remove(f);
        }
        // --- End FireBall Effects ---


        if (waveManager.update()) {
            score += 100;
            if (upgradeManager.hasUpgrade("Health Regen")) {
                if (lives < 5) { // Assuming 5 is max lives
                    lives++;
                }
            }
        }

        switch (waveManager.getWaveState()) {
            case SPAWNING:
                if (waveManager.canSpawnEnemy()) {
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
                projectiles.clear();
                break;
        }
    }

    private void updateAndCheckLostEnemies() {
        // Pass wall position to enemy manager
        ArrayList<Enemy> lostEnemies = enemyManager.updateEnemies(isWallActive() ? wallYPosition : -1);
        for (Enemy lostEnemy : lostEnemies) {
            loseLife();
            typingManager.checkTargetLost(lostEnemy);
        }
    }

    private void spawnProjectile(Enemy target) {
        if (target == null) return;

        int startX = player.x + (player.getSpriteWidth() / 2);
        int startY = player.y;

        int targetX = target.x;
        int targetY = target.y - (target.getScaledHeight() / 2);

        projectiles.add(new Projectile(startX, startY, targetX, targetY, 1));
        AudioManager.playProjectileSfx();
    }

    public void appendTypedCharacter(char c) {
        if (gameState != GameState.PLAYING) return;
        
        Enemy preHitTarget = typingManager.getTargetEnemy();
        
        TypingResult result = typingManager.handleKeyTyped(c, enemyManager.getEnemies());

        Enemy postHitTarget = typingManager.getTargetEnemy();
        
        Enemy targetToShootAt = (postHitTarget != null) ? postHitTarget : preHitTarget;

        if (result == TypingResult.HIT) {
            gameStats.incrementCharsTyped(1);
            
            // Split shot logic
            if (upgradeManager.hasUpgrade("Split Shot") && splitShotCooldown <= 0) {
                Upgrade splitShot = upgradeManager.getUpgrade("Split Shot");
                int numProjectiles = (int) splitShot.getParam1Value();
                for (int i = 0; i < numProjectiles; i++) {
                    // Spawn projectiles with a slight random offset
                    int randomTargetX = targetToShootAt.x + rand.nextInt(41) - 20; // -20 to +20 offset
                    int randomTargetY = targetToShootAt.y - (targetToShootAt.getScaledHeight() / 2) + rand.nextInt(41) - 20;
                    
                    projectiles.add(new Projectile(player.x, player.y, randomTargetX, randomTargetY, 1));
                }
                AudioManager.playProjectileSfx();
                splitShotCooldown = splitShot.getParam3Value();
            } else {
                spawnProjectile(targetToShootAt); // Normal shot
            }
            
        } else if (result == TypingResult.DESTROYED) {
            gameStats.incrementCharsTyped(1);
            spawnProjectile(targetToShootAt); // The killing shot
            
            if (targetToShootAt != null) {
                // Upgrade triggers on kill
                // 1. Add XP
                int xp = targetToShootAt.originalText.length();
                if (upgradeManager.hasUpgrade("Tome of Greed")) {
                    xp *= (1 + upgradeManager.getUpgrade("Tome of Greed").getParam1Value() / 100.0);
                }
                upgradeManager.addXP(xp); // Add XP
                
                // 2. Fire Ball Logic (moved to upgrade class)
                if (upgradeManager.hasUpgrade("Fire Ball") && fireBallCooldown <= 0) {
                    upgradeManager.getUpgrade("Fire Ball").apply(this, targetToShootAt);
                    fireBallCooldown = upgradeManager.getUpgrade("Fire Ball").getParam3Value();
                }

                // 3. Insect Spray Logic (moved to upgrade class)
                if (upgradeManager.hasUpgrade("Insect Spray") && insectSprayCooldown <= 0) {
                    upgradeManager.getUpgrade("Insect Spray").apply(this, targetToShootAt);
                    insectSprayCooldown = upgradeManager.getUpgrade("Insect Spray").getParam3Value();
                }

                // 4. Score
                double scoreIncrease = targetToShootAt.originalText.length();
                if (upgradeManager.hasUpgrade("Tome of Greed")) {
                     scoreIncrease *= (1 + upgradeManager.getUpgrade("Tome of Greed").getParam2Value() / 100.0);
                }
                if (upgradeManager.hasUpgrade("Health Regen")) {
                    scoreIncrease += upgradeManager.getUpgrade("Health Regen").getParam3Value();
                }
                score += scoreIncrease;
                
                enemyManager.removeEnemy(targetToShootAt);
            }
        }
        
    // Check if we leveled up and need to show the screen
        if (upgradeManager.getPlayerLevel() > this.playerLevel) { 
             this.playerLevel = upgradeManager.getPlayerLevel();
             gameState = GameState.LEVEL_UP_CHOICE;
        }
    }
    
    // Note: upgrade-specific effects were moved into the upgrade classes themselves.
    
    // --- End methods ---


    public void backspaceTypedWord() {
        if (gameState != GameState.PLAYING) return;
        typingManager.handleBackspace();
    }
    
    // --- GETTERS ---
    
    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public GameState getGameState() {
        return gameState;
    }
    
    // Setter for game state
    public void setGameState(GameState state) {
        this.gameState = state;
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
    
    // --- NEW GETTERS for Upgrades ---
    
    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    /** Returns the active poison walls. */
    public ArrayList<GameObject.PoisonWall> getPoisonWalls() {
        return poisonWalls;
    }
    
    // --- Helpers used by upgrade.apply(...) implementations ---
    /** Remove an enemy from the game (forward to the enemy manager). */
    public void removeEnemy(Enemy e) {
        enemyManager.removeEnemy(e);
    }

    /** Add to the player's score. */
    public void addScore(int amount) {
        this.score += amount;
    }

    /** If the typing target is the given enemy, reset the typing manager. */
    public void resetTypingIfTarget(Enemy e) {
        if (typingManager.getTargetEnemy() == e) {
            typingManager.reset();
        }
    }

    /** Create & add a poison wall at the specified location. */
    public void createPoisonWall(int x, int y, double height, double width, double durationSeconds, double lettersPerSecond, double slowFactor) {
        poisonWalls.add(new GameObject.PoisonWall(x, y, height, width, durationSeconds, lettersPerSecond, slowFactor));
    }

    /** Create & add a FireBall visual effect. */
    public void createFireBallEffect(int x, int y, double radius, double durationSeconds) {
        fireBallEffects.add(new GameObject.FireBallEffect(x, y, radius, durationSeconds));
    }

    public java.util.List<GameObject.FireBallEffect> getFireBallEffects() {
        return fireBallEffects;
    }

    // Cooldown getters for UI
    public double getFireBallCooldown() { return fireBallCooldown; }
    public double getInsectSprayCooldown() { return insectSprayCooldown; }
    public double getSplitShotCooldown() { return splitShotCooldown; }

    public double getFireBallMaxCooldown() {
        if (upgradeManager.hasUpgrade("Fire Ball")) return upgradeManager.getUpgrade("Fire Ball").getParam3Value();
        return 10.0;
    }
    public double getInsectSprayMaxCooldown() {
        if (upgradeManager.hasUpgrade("Insect Spray")) return upgradeManager.getUpgrade("Insect Spray").getParam3Value();
        return 15.0;
    }
    public double getSplitShotMaxCooldown() {
        if (upgradeManager.hasUpgrade("Split Shot")) return upgradeManager.getUpgrade("Split Shot").getParam3Value();
        return 5.0;
    }
    
    public boolean isWallActive() {
        return wallDuration > 0;
    }
    
    public int getWallYPosition() {
        return wallYPosition;
    }
}