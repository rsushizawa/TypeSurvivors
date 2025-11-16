package View;

import java.awt.*;

import Model.GameModel;
import Data.HighScoreEntry;
import Config.GameConfig;


public class MenuRenderer {

    public void renderMainMenu(Graphics2D g, GameModel model, int width, int height) {
        drawCenteredString(g, "TYPE SURVIVORS", height / 6, new Color(30, 144, 255), Font.BOLD, GameConfig.MAINMENU_TITLE_FONT, width);
        drawCenteredString(g, "Type words to destroy enemies!", height / 6 + 60, Color.WHITE, Font.PLAIN, GameConfig.MAINMENU_SUB_FONT, width);
        drawLeaderboard(g, model, height / 6 + 120, width);
        drawCenteredString(g, "Press ENTER to Start", height - 180, Color.GREEN, Font.BOLD, 28, width);

        String[] instructions = {
            "How to Play:",
            "- Type the letters of approaching enemies",
            "- Each letter fires a projectile",
            "- Complete words before they reach you",
            "- Press BACKSPACE to cancel current word",
            "- Press ESC to pause the game"
        };

        int startY = height - 140;
        for (int i = 0; i < instructions.length; i++) {
            if (i == 0) {
                drawCenteredString(g, instructions[i], startY + (i * 25), Color.CYAN, Font.BOLD, 18, width);
            } else {
                drawCenteredString(g, instructions[i], startY + (i * 25), Color.LIGHT_GRAY, Font.PLAIN, 14, width);
            }
        }
    }

    private void drawLeaderboard(Graphics2D g, GameModel model, int startY, int width) {
        drawCenteredString(g, "=== HIGH SCORES ===", startY, Color.YELLOW, Font.BOLD, 24, width);

        java.util.List<HighScoreEntry> scores = model.getLeaderboardManager().getHighScores();
        if (scores.isEmpty()) {
            drawCenteredString(g, "No scores yet!", startY + 40, Color.GRAY, Font.PLAIN, 18, width);
            return;
        }

        g.setColor(Color.CYAN);
        g.setFont(new Font("Monospaced", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();

        String header = String.format("%-3s %-10s %8s %5s %5s", "#", "NAME", "SCORE", "WAVE", "WPM");
        g.drawString(header, (width - fm.stringWidth(header)) / 2, startY + 40);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g.getFontMetrics();

        for (int i = 0; i < scores.size(); i++) {
            HighScoreEntry entry = scores.get(i);
            if (i % 2 == 0) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(new Color(200, 200, 200));
            }
            String line = String.format("%-3d %-10s %8d %5d %5d", i + 1, entry.getName(), entry.getScore(), entry.getWave(), entry.getMaxWPM());
            g.drawString(line, (width - fm.stringWidth(line)) / 2, startY + 65 + (i * 25));
        }
    }

    public void renderNameEntry(Graphics2D g, GameModel model, int width, int height) {
        g.setColor(new Color(0, 0, 0, 230));
        g.fillRect(0, 0, width, height);
        drawCenteredString(g, "NEW HIGH SCORE!", height / 3, Color.YELLOW, Font.BOLD, 48, width);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String scoreText = "Score: " + model.getScore() + "  Wave: " + model.getWaveNumber() + "  WPM: " + model.getMaxWPM();
        g.drawString(scoreText, (width - fm.stringWidth(scoreText)) / 2, height / 3 + 50);

        drawCenteredString(g, "Enter Your Name:", height / 2 - 40, Color.CYAN, Font.BOLD, 28, width);

        int boxWidth = 300;
        int boxHeight = 50;
        int boxX = (width - boxWidth) / 2;
        int boxY = height / 2;

        g.setColor(new Color(50, 50, 50));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);
        g.setColor(Color.GREEN);
        g.drawRect(boxX, boxY, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 32));
        String displayName = model.getPlayerName();
        if (System.currentTimeMillis() % 1000 < 500) {
            displayName += "_";
        } else if (displayName.isEmpty()) {
            displayName = " ";
        }
        fm = g.getFontMetrics();
        g.drawString(displayName, boxX + 10, boxY + 37);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Monospaced", Font.PLAIN, 16));
        String hint = "Max " + GameModel.MAX_NAME_LENGTH + " characters";
        fm = g.getFontMetrics();
        g.drawString(hint, (width - fm.stringWidth(hint)) / 2, boxY + boxHeight + 30);

        drawCenteredString(g, "Press ENTER to Submit", height - 80, Color.GREEN, Font.BOLD, 22, width);
    }

    public void renderPauseOverlay(Graphics2D g, int width, int height) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);
        drawCenteredString(g, "PAUSED", height / 2 - 20, Color.YELLOW, Font.BOLD, 48, width);
        drawCenteredString(g, "Press ESC to Resume", height / 2 + 30, Color.WHITE, Font.PLAIN, 22, width);
    }

    public void renderGameOver(Graphics2D g, GameModel model, int width, int height) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);
        drawCenteredString(g, "GAME OVER", height / 2 - 100, Color.RED, Font.BOLD, 48, width);
        drawCenteredString(g, "Final Score: " + model.getScore(), height / 2 - 30, Color.WHITE, Font.BOLD, 24, width);
        drawCenteredString(g, "Wave Reached: " + model.getWaveNumber(), height / 2 + 10, Color.WHITE, Font.PLAIN, 20, width);
        drawCenteredString(g, "Max WPM: " + model.getMaxWPM(), height / 2 + 40, Color.YELLOW, Font.PLAIN, 20, width);
        drawCenteredString(g, "Press ENTER to Return to Menu", height / 2 + 100, Color.GREEN, Font.BOLD, 22, width);
    }

    private void drawCenteredString(Graphics2D g, String text, int y, Color color, int fontStyle, int fontSize, int width) {
        drawCenteredString(g, text, y, color, "Monospaced", fontStyle, fontSize, width);
    }

    private void drawCenteredString(Graphics2D g, String text, int y, Color color, String fontName, int fontStyle, int fontSize, int width) {
        g.setColor(color);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (width - fm.stringWidth(text)) / 2, y);
    }
}
