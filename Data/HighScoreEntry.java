package Data;

public class HighScoreEntry implements Comparable<HighScoreEntry> {
    private final String name;
    private final int score;
    private final int wave;
    private final int maxWPM;

    public HighScoreEntry(String name, int score, int wave, int maxWPM) {
        this.name = name;
        this.score = score;
        this.wave = wave;
        this.maxWPM = maxWPM;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getWave() {
        return wave;
    }

    public int getMaxWPM() {
        return maxWPM;
    }

    @Override
    public int compareTo(HighScoreEntry other) {
        return Integer.compare(other.score, this.score); // Descending order
    }

    public String toFileString() {
        return name + "," + score + "," + wave + "," + maxWPM;
    }

    public static HighScoreEntry fromFileString(String line) {
        String[] parts = line.split(",");
        if (parts.length == 4) {
            return new HighScoreEntry(
                parts[0],
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3])
            );
        }
        return null;
    }
}