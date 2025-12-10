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
    private int maxLives = 5;
    private int playerLevel = 1;
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
    private final ArrayList<RegenEffect> regenEffects = new ArrayList<>();
    private final Random rand = new Random();
    private final ArrayList<Enemy> pendingEnemies = new ArrayList<>();
    
    public static final int GAME_SPEED_MS = 16;
    public static final int MAX_NAME_LENGTH = 10;

    private double fireBallCooldown = 0;
    private double insectSprayCooldown = 0;
    private double splitShotCooldown = 0;
    private double wallCooldown = 0;
    private double wallDuration = 0;
    private int wallYPosition = -1;
    private double wrongCharShakeTime = 0.0;
    private final double WRONG_CHAR_SHAKE_DURATION = 0.35;
    private final int WRONG_CHAR_SHAKE_AMPLITUDE = 8;
    private double healthRegenCooldown = 0.0;
    private double healthRegenMaxCooldown = 0.0;

    // Player status effects from enemy projectiles
    private double stunRemaining = 0.0; // seconds
    private double stunMax = 0.0; // the original stun duration for HUD fraction

    // Poison window: when active, the next enemy reaching the line deals double damage.
    private double poisonRemaining = 0.0; // seconds
    private double poisonMax = 0.0;

    public GameModel(int gameWidth, int gameHeight) {
        this.waveManager = new WaveManager();
        this.enemyManager = new EnemyManager(gameWidth, gameHeight);
        this.typingManager = new TypingManager();
        this.gameStats = new GameStats();
        this.leaderboardManager = new LeaderboardManager();
        this.upgradeManager = new UpgradeManager();
            
        this.player = new Player(gameWidth / 2 - 92, gameHeight - 200);
        this.projectiles = new ArrayList<>();
    }

    public void startNewGame() {
        score = 0;
        lives = 5;
        maxLives = 5;
        playerLevel = 1;
        playerName = "";
        gameState = GameState.PLAYING;
        waveManager.reset();
        enemyManager.getEnemies().clear();
        projectiles.clear();
        typingManager.reset();
        gameStats.reset();
    
    }

    public int getMaxLives() {
        return maxLives;
    }

    public void increaseMaxLives(int amount) {
        if (amount <= 0) return;
        maxLives += amount;
        if (lives < maxLives) lives = Math.min(maxLives, lives + amount);
    }

    public void heal(int amount) {
        if (amount <= 0) return;
        lives = Math.min(maxLives, lives + amount);
    }

    public void startHealthRegen(double hpPerSecond, double durationSeconds) {
 
        startHealthRegen(hpPerSecond, durationSeconds, 0.0);
    }

    public void startHealthRegen(double hpPerSecond, double durationSeconds, double cooldownSeconds) {
        if (hpPerSecond <= 0 || durationSeconds <= 0) return;

        if (healthRegenCooldown > 0) return;

        regenEffects.add(new RegenEffect(hpPerSecond, durationSeconds));
        if (cooldownSeconds > 0) {
            healthRegenCooldown = cooldownSeconds;
            healthRegenMaxCooldown = cooldownSeconds;
        }
    }

    private static class RegenEffect {
        double ratePerSecond;
        double remainingSeconds;
        double accumulator = 0.0; 

        RegenEffect(double ratePerSecond, double durationSeconds) {
            this.ratePerSecond = ratePerSecond;
            this.remainingSeconds = durationSeconds;
        }

        int tick(double deltaSeconds) {
            if (remainingSeconds <= 0) return 0;
            double effective = Math.min(deltaSeconds, remainingSeconds);
            double hp = ratePerSecond * effective;
            remainingSeconds -= effective;
            accumulator += hp;
            int toHeal = (int) Math.floor(accumulator);
            accumulator -= toHeal;
            return toHeal;
        }

        boolean isExpired() {
            return remainingSeconds <= 0;
        }
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

    public void loseLife() {
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
    if (gameState == GameState.LEVEL_UP_CHOICE) return;
        if (gameState != GameState.PLAYING) return;

        player.update();

        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update();

            boolean removeProjectile = false;

            if (p.isEnemyOwned()) {
                int px = player.x + (player.getSpriteWidth() / 2);
                int py = player.y + (player.getSpriteHeight() / 2);
                int dx = p.x - px;
                int dy = p.y - py;
                int pr = Math.max(8, player.getSpriteWidth() / 3);
                if (dx * dx + dy * dy <= pr * pr) {
                    removeProjectile = true;
                    loseLife();
                }
            } else {
                for (Enemy e : enemyManager.getEnemies()) {
                    int dx = p.x - e.x;
                    int dy = p.y - e.y;
                    int radius = Math.max(8, e.getScaledWidth() / 3);
                    if (dx * dx + dy * dy <= radius * radius) {
                        removeProjectile = true;
                        break;
                    }
                }
            }

            if (!p.isActive() || removeProjectile) {
                projIter.remove();
            }
        }
        
        for (Enemy enemy : enemyManager.getEnemies()) {
            if (!(enemy instanceof Entity.Enemy.EnemyProjectile)) {
                enemy.updateAnimation();
            }
        }

        if (waveManager.getWaveState() != WaveState.INTERMISSION) {
            gameStats.incrementGameTicks();
        }

        double delta = GAME_SPEED_MS / 1000.0;
        if (fireBallCooldown > 0) fireBallCooldown -= delta;
        if (insectSprayCooldown > 0) insectSprayCooldown -= delta;
        if (splitShotCooldown > 0) splitShotCooldown -= delta;
        if (stunRemaining > 0) stunRemaining = Math.max(0.0, stunRemaining - delta);
        if (poisonRemaining > 0) poisonRemaining = Math.max(0.0, poisonRemaining - delta);

        if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.WALL)) {
            if (wallCooldown > 0) {
                wallCooldown -= delta;
            }

            if (wallDuration > 0) {
                wallDuration -= delta;
                if (wallDuration <= 0) {
                    wallYPosition = -1;
                }
            }
        }

        for (GameObject.PoisonWall pw : new ArrayList<>(poisonWalls)) {
            pw.update(delta, enemyManager.getEnemies(), this);
            if (pw.isExpired()) {
                poisonWalls.remove(pw);
            }
        }
        for (GameObject.FireBallEffect f : new ArrayList<>(fireBallEffects)) {
            f.update(delta);
            if (f.isExpired()) fireBallEffects.remove(f);
        }

        if (wrongCharShakeTime > 0) {
            wrongCharShakeTime = Math.max(0.0, wrongCharShakeTime - delta);
        }

        if (!regenEffects.isEmpty()) {
            ArrayList<RegenEffect> expired = new ArrayList<>();
            for (RegenEffect r : regenEffects) {
                int healed = r.tick(delta);
                if (healed > 0) {
                    heal(healed);
                }
                if (r.isExpired()) expired.add(r);
            }
            regenEffects.removeAll(expired);
        }

        if (healthRegenCooldown > 0) {
            healthRegenCooldown = Math.max(0.0, healthRegenCooldown - delta);
        }


        if (waveManager.update()) {
            score += 100;
            if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.HEALTH_REGEN)) {
                if (lives < 5) {
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

    public boolean tryActivateWall() {
    if (!upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.WALL)) return false;
        if (wallDuration > 0) return false; 
        if (wallCooldown > 0) return false; 

    Upgrade wall = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.WALL);
        if (wall == null) return false;

        this.wallDuration = wall.getParam1Value();
        this.wallCooldown = wall.getParam2Value();
        this.wallYPosition = player.y - (int) wall.getParam3Value();
        // Play barrier activation SFX
        Audio.AudioManager.playBarrierSfx();
        return true;
    }

    private void updateAndCheckLostEnemies() {
        ArrayList<Enemy> lostEnemies = enemyManager.updateEnemies(isWallActive() ? wallYPosition : -1, this);
        for (Enemy lostEnemy : lostEnemies) {
            // If poisoned, the next enemy to reach the line deals double damage (consume poison)
            if (poisonRemaining > 0.0) {
                // apply double damage
                loseLife();
                loseLife();
                poisonRemaining = 0.0;
            } else {
                loseLife();
            }
            typingManager.checkTargetLost(lostEnemy);
        }
        if (!pendingEnemies.isEmpty()) {
            enemyManager.getEnemies().addAll(pendingEnemies);
            pendingEnemies.clear();
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
        if (stunRemaining > 0.0) {
            AudioManager.playWrongKeySfx();
            return;
        }
        
        Enemy preHitTarget = typingManager.getTargetEnemy();
        
        TypingResult result = typingManager.handleKeyTyped(c, enemyManager.getEnemies());

        Enemy postHitTarget = typingManager.getTargetEnemy();
        
        Enemy targetToShootAt = (postHitTarget != null) ? postHitTarget : preHitTarget;

        if (result == TypingResult.HIT) {
            gameStats.incrementCharsTyped(1);
            
            if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.SPLIT_SHOT) && splitShotCooldown <= 0) {
                Upgrade splitShot = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.SPLIT_SHOT);
                int numProjectiles = (int) splitShot.getParam1Value();
                for (int i = 0; i < numProjectiles; i++) {
                    int randomTargetX = targetToShootAt.x + rand.nextInt(41) - 20; 
                    int randomTargetY = targetToShootAt.y - (targetToShootAt.getScaledHeight() / 2) + rand.nextInt(41) - 20;
                    
                    projectiles.add(new Projectile(player.x, player.y, randomTargetX, randomTargetY, 1));
                }
                AudioManager.playProjectileSfx();
                splitShotCooldown = splitShot.getParam3Value();
            } else {
                spawnProjectile(targetToShootAt);
            }
            
        } else if (result == TypingResult.DESTROYED) {
            gameStats.incrementCharsTyped(1);
            spawnProjectile(targetToShootAt);
            
            if (targetToShootAt != null) {
                int xp = targetToShootAt.originalText.length();
                if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.XP_TOME)) {
                    Upgrade tome = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.XP_TOME);
                    if (tome != null) xp *= (1 + tome.getParam1Value() / 100.0);
                }
                upgradeManager.addXP(xp);
                
                if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.FIRE_BALL) && fireBallCooldown <= 0) {
                    Upgrade fb = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.FIRE_BALL);
                    if (fb != null) {
                        fb.apply(this, targetToShootAt);
                        fireBallCooldown = fb.getParam3Value();
                    }
                }

                if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.INSECT_SPRAY) && insectSprayCooldown <= 0) {
                    Upgrade is = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.INSECT_SPRAY);
                    if (is != null) {
                        is.apply(this, targetToShootAt);
                        insectSprayCooldown = is.getParam3Value();
                    }
                }

                // 4. Score
                double scoreIncrease = targetToShootAt.originalText.length();
                if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.XP_TOME)) {
                     Upgrade t = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.XP_TOME);
                     if (t != null) scoreIncrease *= (1 + t.getParam2Value() / 100.0);
                }
                if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.HEALTH_REGEN)) {
                    Upgrade hr = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.HEALTH_REGEN);
                    if (hr != null) scoreIncrease += hr.getParam3Value();
                }
                score += scoreIncrease;
                
                resetTypingIfTarget(targetToShootAt);
                // Play enemy death SFX when player destroys an enemy
                AudioManager.playDeath1Sfx();
                enemyManager.removeEnemy(targetToShootAt);
            }
        }
            else if (result == TypingResult.MISS) {
                AudioManager.playWrongKeySfx();
                wrongCharShakeTime = WRONG_CHAR_SHAKE_DURATION;
            }
        
           if (upgradeManager.getPlayerLevel() > this.playerLevel) { 
               this.playerLevel = upgradeManager.getPlayerLevel();
               // Play level-up SFX when opening the level-up choice screen
               AudioManager.playLevelUpSfx();
               gameState = GameState.LEVEL_UP_CHOICE;
           }
    }


    public void backspaceTypedWord() {
        if (gameState != GameState.PLAYING) return;
        typingManager.handleBackspace();
    }
    
    
    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public double getActiveRegenRate() {
        double total = 0.0;
        for (RegenEffect r : regenEffects) total += r.ratePerSecond;
        return total;
    }

    public double getActiveRegenRemaining() {
        double max = 0.0;
        for (RegenEffect r : regenEffects) max = Math.max(max, r.remainingSeconds);
        return max;
    }

    public double getHealthRegenCooldown() {
        return healthRegenCooldown;
    }

    public double getHealthRegenMaxCooldown() {
        return healthRegenMaxCooldown;
    }

    public GameState getGameState() {
        return gameState;
    }
    
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

    public void addEnemy(Enemy e) {
        pendingEnemies.add(e);
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
    
    
    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public ArrayList<GameObject.PoisonWall> getPoisonWalls() {
        return poisonWalls;
    }
    
    public void removeEnemy(Enemy e) {
        enemyManager.removeEnemy(e);
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public void resetTypingIfTarget(Enemy e) {
        if (typingManager.getTargetEnemy() == e) {
            typingManager.reset();
        }
    }

    public void createPoisonWall(int x, int y, double height, double width, double durationSeconds, double lettersPerSecond, double slowFactor) {
        poisonWalls.add(new GameObject.PoisonWall(x, y, height, width, durationSeconds, lettersPerSecond, slowFactor));
    }

    public void createFireBallEffect(int x, int y, double radius, double durationSeconds) {
        fireBallEffects.add(new GameObject.FireBallEffect(x, y, radius, durationSeconds));
    }

    public java.util.List<GameObject.FireBallEffect> getFireBallEffects() {
        return fireBallEffects;
    }

    // --- Stun / Poison effects API ---
    // Apply a stun effect (seconds). Stun will block player typing for its duration.
    public void applyStun(double seconds) {
        if (seconds <= 0) return;
        this.stunRemaining = seconds;
        this.stunMax = seconds;
    }

    // Apply poison window (seconds): during this time, the next enemy that reaches
    // the line deals double damage. The window is consumed when that happens.
    public void applyPoisonWindow(double seconds) {
        if (seconds <= 0) return;
        this.poisonRemaining = seconds;
        this.poisonMax = seconds;
    }

    public double getStunRemaining() { return stunRemaining; }
    public double getStunMax() { return stunMax; }
    public double getPoisonRemaining() {return poisonRemaining; }
    public double getPoisonMax() { return poisonMax; }

    public double getFireBallCooldown() { return fireBallCooldown; }
        
    public double getInsectSprayCooldown() { return insectSprayCooldown; }
    public double getSplitShotCooldown() { return splitShotCooldown; }

    public double getUpgradeCooldown(String upgradeName) {
        switch (upgradeName) {
            case "Fire Ball": return fireBallCooldown;
            case "Insect Spray": return insectSprayCooldown;
            case "Split Shot": return splitShotCooldown;
            case "Wall": return wallCooldown;
            case "Health Regen": return healthRegenCooldown;
            default: return 0.0;
        }
    }

    public boolean isPoisonActive() {
        return getPoisonRemaining() > 0.0;
    }

    // Enum-based overload for safer access
    public double getUpgradeCooldown(Manager.UpgradeManager.UpgradeId id) {
        if (id == null) return 0.0;
        switch (id) {
            case FIRE_BALL: return fireBallCooldown;
            case INSECT_SPRAY: return insectSprayCooldown;
            case SPLIT_SHOT: return splitShotCooldown;
            case WALL: return wallCooldown;
            case HEALTH_REGEN: return healthRegenCooldown;
            default: return 0.0;
        }
    }

    public double getFireBallMaxCooldown() {
        if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.FIRE_BALL)) {
            Upgrade u = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.FIRE_BALL);
            if (u != null) return u.getParam3Value();
        }
        return 10.0;
    }
    public double getInsectSprayMaxCooldown() {
        if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.INSECT_SPRAY)) {
            Upgrade u = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.INSECT_SPRAY);
            if (u != null) return u.getParam3Value();
        }
        return 15.0;
    }
    public double getSplitShotMaxCooldown() {
        if (upgradeManager.hasUpgrade(UpgradeManager.UpgradeId.SPLIT_SHOT)) {
            Upgrade u = upgradeManager.getUpgrade(UpgradeManager.UpgradeId.SPLIT_SHOT);
            if (u != null) return u.getParam3Value();
        }
        return 5.0;
    }
    
    public boolean isWallActive() {
        return wallDuration > 0;
    }
    
    public int getWallYPosition() {
        return wallYPosition;
    }

    public int getShakeOffsetX() {
        if (wrongCharShakeTime <= 0) return 0;
        double progress = 1.0 - (wrongCharShakeTime / WRONG_CHAR_SHAKE_DURATION);
        double oscillations = 6.0;
        double angle = progress * Math.PI * 2.0 * oscillations;
        double decay = 1.0 - progress;
        return (int) Math.round(Math.sin(angle) * WRONG_CHAR_SHAKE_AMPLITUDE * decay);
    }
}