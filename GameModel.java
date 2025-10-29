import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameModel {

    public static final int GAME_SPEED_MS = 16;
    public static final int WORD_SPEED_PIXELS = 1;
    private static final int WORD_SPAWN_CHANCE = 80;
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

    public static class Word {
        public String text;
        public int x, y;

        Word(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }

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

    public void spawnWord() {
        if (random.nextInt(WORD_SPAWN_CHANCE) == 0) {
            String text = WORD_LIST[random.nextInt(WORD_LIST.length)];
            int wordWidth = text.length() * 10; 
            int x = random.nextInt(Math.max(10, gameWidth - wordWidth - 20)) + 10;
            
            words.add(new Word(text, x, 0));
        }
    }

    
    public void updateWords() {
        if (isGameOver) return;
        
        Iterator<Word> iter = words.iterator();
        while (iter.hasNext()) {
            Word word = iter.next();
            word.y += WORD_SPEED_PIXELS;
            
            if (word.y > gameHeight) {
                iter.remove();
                lives--;
                if (lives <= 0) {
                    isGameOver = true;
                }
            }
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
