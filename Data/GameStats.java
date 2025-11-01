package Data;

public class GameStats {

    public static final int GAME_SPEED_MS = 16;
    private int totalCharsTyped = 0;
    private long totalGameTicks = 0;
    private int maxWPM = 0;

    public void reset() {
        totalCharsTyped = 0;
        totalGameTicks = 0;
        maxWPM = 0;
    }

    public void incrementCharsTyped(int count) {
        totalCharsTyped += count;
    }

    public void incrementGameTicks() {
        totalGameTicks++;
        int currentWPM = getWPM();
        if (currentWPM > maxWPM) {
            maxWPM = currentWPM;
        }
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

    public int getMaxWPM() {
        return maxWPM;
    }
}