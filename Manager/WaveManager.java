package Manager;

import Audio.AudioManager;
import Data.WaveState;

public class WaveManager {

    private int waveNumber = 0;
    private int enemiesLeftToSpawn;
    private int waveSpeedPixels = 1;
    private WaveState waveState = WaveState.INTERMISSION;
    private int intermissionTickCounter = 90;

    private final WaveDifficultyManager difficultyManager = new WaveDifficultyManager();
    private int totalEnemiesThisWave = 0;
    private int spawnedSoFar = 0;

    public void reset() {
        waveNumber = 0;
        enemiesLeftToSpawn = 0;
        waveSpeedPixels = 1;
        waveState = WaveState.INTERMISSION;
        intermissionTickCounter = difficultyManager.getIntermissionTicks() / 2; // shorter first intermission
        totalEnemiesThisWave = 0;
        spawnedSoFar = 0;
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
        if (waveNumber <= 0) return difficultyManager.spawnChanceForWave(0);
        return difficultyManager.spawnChanceDuringWave(waveNumber, spawnedSoFar, totalEnemiesThisWave);
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
        totalEnemiesThisWave = difficultyManager.enemiesForWave(waveNumber);
        enemiesLeftToSpawn = totalEnemiesThisWave;
        spawnedSoFar = 0;

        waveSpeedPixels = difficultyManager.waveSpeedPixels(waveNumber);

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
        spawnedSoFar++;
        if (enemiesLeftToSpawn <= 0) {
            waveState = WaveState.WAITING_FOR_CLEAR;
        }
    }
}