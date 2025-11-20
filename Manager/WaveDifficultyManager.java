package Manager;


public class WaveDifficultyManager {

    private int baseEnemies = 3;
    private int enemiesPerWave = 1;

    private int baseWordSpeed = 1;
    private double speedIncreasePerWave = 0.08; 

    private int baseSpawnChance = 90;
    private int spawnChanceDecayPerWave = 2; 
    private int minSpawnChance = 20;

    private int intermissionTicks = 180;

    public WaveDifficultyManager() {}

    public int enemiesForWave(int waveNumber) {
        if (waveNumber <= 0) return baseEnemies;
        return baseEnemies + (waveNumber * enemiesPerWave);
    }

    public int waveSpeedPixels(int waveNumber) {
        double speed = baseWordSpeed + (waveNumber * speedIncreasePerWave);
        return Math.max(1, (int)Math.floor(speed));
    }

    public int spawnChanceForWave(int waveNumber) {
        int val = baseSpawnChance - (waveNumber * spawnChanceDecayPerWave);
        if (val < minSpawnChance) val = minSpawnChance;
        return val;
    }

    public int spawnChanceDuringWave(int waveNumber, int spawnedSoFar, int totalToSpawn) {
        int base = spawnChanceForWave(waveNumber);
        if (totalToSpawn <= 0) return base;

        double progress = (double)spawnedSoFar / (double)totalToSpawn;
        double reduction = progress * (base - minSpawnChance) * 0.7;
        int adjusted = (int)Math.max(minSpawnChance, Math.round(base - reduction));
        return adjusted;
    }

    public int getIntermissionTicks() {
        return intermissionTicks;
    }

    public void setBaseEnemies(int v) { baseEnemies = v; }
    public void setEnemiesPerWave(int v) { enemiesPerWave = v; }
    public void setBaseWordSpeed(int v) { baseWordSpeed = v; }
    public void setSpeedIncreasePerWave(double v) { speedIncreasePerWave = v; }
    public void setBaseSpawnChance(int v) { baseSpawnChance = v; }
    public void setSpawnChanceDecayPerWave(int v) { spawnChanceDecayPerWave = v; }
    public void setMinSpawnChance(int v) { minSpawnChance = v; }
    public void setIntermissionTicks(int v) { intermissionTicks = v; }
}
