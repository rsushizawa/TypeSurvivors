package Manager;

import Audio.AudioManager;
import Data.WaveState;

public class WaveManager {

    private int waveNumber = 0;
    private int enemiesLeftToSpawn;
    private int waveSpeedPixels = 1;
    private int waveSpawnChance = 80;
    private WaveState waveState = WaveState.INTERMISSION;
    private int intermissionTickCounter = 90;

    private static final int INTERMISSION_TICKS = 180;
    private int baseWordSpeed = 1;
    private int baseSpawnChance = 80;
    private final WaveDifficultyManager difficultyManager = new WaveDifficultyManager();

    public void reset() {
        waveNumber = 0;
        enemiesLeftToSpawn = 0;
        waveSpeedPixels = 1;
        waveSpawnChance = 80;
        waveState = WaveState.INTERMISSION;
        intermissionTickCounter = difficultyManager.getIntermissionTicks() / 2; // shorter first intermission
    }

    public WaveState getWaveState() {
        return waveState;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public int getIntermissionTickCounter() {
        return intermissionTickCounter;
    }

    public int getWaveSpeedPixels() {
        return waveSpeedPixels;
    }

    public int getWaveSpawnChance() {
        return waveSpawnChance;
    }

    public boolean canSpawnEnemy() {
        return waveState == WaveState.SPAWNING && enemiesLeftToSpawn > 0;
    }

    public boolean update() {
        if (waveState == WaveState.INTERMISSION) {
            intermissionTickCounter--;
            if (intermissionTickCounter <= 0) {
                startNextWave();
                return true;
            }
        }
        return false;
    }

    public void startNextWave() {
        waveNumber++;
        enemiesLeftToSpawn = difficultyManager.enemiesForWave(waveNumber);

        waveSpeedPixels = difficultyManager.waveSpeedPixels(waveNumber);
        waveSpawnChance = difficultyManager.spawnChanceForWave(waveNumber);

        waveState = WaveState.SPAWNING;

        if (waveNumber % 5 == 0) {
            AudioManager.playBossMusic();
        } else {
            AudioManager.playGameMusic();
        }
    }

    public void startIntermission() {
        waveState = WaveState.INTERMISSION;
        intermissionTickCounter = difficultyManager.getIntermissionTicks();
    }

    public void notifyEnemySpawned() {
        enemiesLeftToSpawn--;
        if (enemiesLeftToSpawn <= 0) {
            waveState = WaveState.WAITING_FOR_CLEAR;
        }
    }
}