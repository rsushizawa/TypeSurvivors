package Model;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameModel {

    public static final int GAME_SPEED_MS = 16;
    public static final int INTERMISSION_TICKS = 180;

    private int baseWordSpeed = 1;
    private int baseSpawnChance = 80;

    private final String[] WORD_LIST = {
        "java", "swing", "model", "view", "controller", "event",
        "pixel", "array", "string", "class", "object", "method", "logic"
    };

    private final ArrayList<Word> words = new ArrayList<>();
    private final Random random = new Random();
    private String currentTypedWord = "";
    private int score = 0;
    private int lives = 5;
    private boolean isGameOver = false;
    private final int gameWidth;
    private final int gameHeight;

    private int waveNumber = 0;
    private int wordsLeftToSpawn;
    private int waveSpeedPixels = 1;
    private int waveSpawnChance = 80;
    private WaveState waveState = WaveState.INTERMISSION;
    private int intermissionTickCounter = 90;

    private int totalCharsTyped = 0;
    private long totalGameTicks = 0;

    public GameModel(int gameWidth, int gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public String getCurrentTypedWord() {
        return currentTypedWord;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getGameHeight() {
        return gameHeight;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public WaveState getWaveState() {
        return waveState;
    }

    public int getIntermissionTickCounter() {
        return intermissionTickCounter;
    }

    public int getWPM() {
        if (totalGameTicks == 0) {
            return 0;
        }
        
        double totalMinutes = (totalGameTicks * GAME_SPEED_MS) / 60000.0;

        if (totalMinutes == 0) {
            return 0;
        }

        double totalWords = totalCharsTyped / 5.0;
        
        return (int) (totalWords / totalMinutes);
    }

    private void startNextWave() {
        waveNumber++;
        score += 100;
        wordsLeftToSpawn = 5 + (waveNumber * 2);
        
        waveSpeedPixels = baseWordSpeed + (waveNumber / 6);
        waveSpawnChance = Math.max(20, baseSpawnChance - (waveNumber * 5));
        
        waveState = WaveState.SPAWNING;
    }

    public void trySpawnWord() {
        if (waveState != WaveState.SPAWNING || wordsLeftToSpawn <= 0) {
            return;
        }

        if (random.nextInt(waveSpawnChance) == 0) {
            String text = WORD_LIST[random.nextInt(WORD_LIST.length)];
            int wordWidth = text.length() * 10; 
            int x = random.nextInt(Math.max(10, gameWidth - wordWidth - 20)) + 10;
            
            words.add(new Word(text, x, 0));
            wordsLeftToSpawn--;

            if (wordsLeftToSpawn <= 0) {
                waveState = WaveState.WAITING_FOR_CLEAR;
            }
        }
    }

    
    public void updateWords() {
        if (isGameOver) return;
        
        Iterator<Word> iter = words.iterator();
        while (iter.hasNext()) {
            Word word = iter.next();
            word.y += waveSpeedPixels;
            
            if (word.y > gameHeight) {
                iter.remove();
                lives--;
                if (lives <= 0) {
                    isGameOver = true;
                }
            }
        }
    }

    public void updateGameState() {
        if (isGameOver) return;

        switch (waveState) {
            case SPAWNING:
                totalGameTicks++;
                trySpawnWord();
                updateWords();
                break;
            case WAITING_FOR_CLEAR:
                totalGameTicks++;
                updateWords();
                if (words.isEmpty()) {
                    waveState = WaveState.INTERMISSION;
                    intermissionTickCounter = INTERMISSION_TICKS;
                }
                break;
            case INTERMISSION:
                intermissionTickCounter--;
                if (intermissionTickCounter <= 0) {
                    startNextWave();
                }
                break;
        }
    }

    public void submitTypedWord() {
        if (currentTypedWord.isEmpty()) return;
        
        Iterator<Word> iter = words.iterator();
        while (iter.hasNext()) {
            Word word = iter.next();
            if (word.text.equals(currentTypedWord)) {
                iter.remove();
                score += word.text.length();
                totalCharsTyped += currentTypedWord.length();
                currentTypedWord = "";
                return;
            }
        }
    }

    public void appendTypedCharacter(char c) {
        currentTypedWord += c;
        submitTypedWord();
    }

    public void backspaceTypedWord() {
        if (currentTypedWord.length() > 0) {
            currentTypedWord = currentTypedWord.substring(0, currentTypedWord.length() - 1);
        }
    }
}


