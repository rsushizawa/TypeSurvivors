package View;

import java.awt.*;
// keep imports minimal for this helper-only renderer

import Model.GameModel;
// no collections required here

public class MenuRenderer {

    // leaderboard moved to MainMenuPanel

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
