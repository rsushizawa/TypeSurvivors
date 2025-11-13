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

import Audio.AudioManager;

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
        
        this.player = new Player(gameWidth / 2 - 32, gameHeight - 120);
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

        if (waveManager.update()) {
            score += 100;
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
        ArrayList<Enemy> lostEnemies = enemyManager.updateEnemies();
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
            spawnProjectile(targetToShootAt);
        } else if (result == TypingResult.DESTROYED) {
            gameStats.incrementCharsTyped(1);
            spawnProjectile(targetToShootAt);
            
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