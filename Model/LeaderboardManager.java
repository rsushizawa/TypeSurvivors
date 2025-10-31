package Model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderboardManager {
    private static final String LEADERBOARD_FILE = "leaderboard.txt";
    private static final int MAX_ENTRIES = 5;
    private List<HighScoreEntry> highScores;

    public LeaderboardManager() {
        highScores = new ArrayList<>();
        loadLeaderboard();
    }

    public void loadLeaderboard() {
        highScores.clear();
        File file = new File(LEADERBOARD_FILE);
        
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                HighScoreEntry entry = HighScoreEntry.fromFileString(line);
                if (entry != null) {
                    highScores.add(entry);
                }
            }
            Collections.sort(highScores);
        } catch (IOException e) {
            System.err.println("Error loading leaderboard: " + e.getMessage());
        }
    }

    public void saveLeaderboard() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LEADERBOARD_FILE))) {
            for (HighScoreEntry entry : highScores) {
                writer.write(entry.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving leaderboard: " + e.getMessage());
        }
    }

    public boolean isHighScore(int score) {
        if (highScores.size() < MAX_ENTRIES) {
            return true;
        }
        return score > highScores.get(highScores.size() - 1).getScore();
    }

    public void addHighScore(String name, int score, int wave, int maxWPM) {
        HighScoreEntry entry = new HighScoreEntry(name, score, wave, maxWPM);
        highScores.add(entry);
        Collections.sort(highScores);

        while (highScores.size() > MAX_ENTRIES) {
            highScores.remove(highScores.size() - 1);
        }
        
        saveLeaderboard();
    }

    public List<HighScoreEntry> getHighScores() {
        return new ArrayList<>(highScores);
    }

    public int getLowestHighScore() {
        if (highScores.isEmpty()) {
            return 0;
        }
        return highScores.get(highScores.size() - 1).getScore();
    }
}