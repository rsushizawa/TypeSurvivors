package View;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import Data.HighScoreEntry;
import Model.GameModel;

public class MainMenuPanel implements Renderable {

    private final java.util.List<String> mainMenuItems = new ArrayList<>(Arrays.asList("Start Game", "Options", "Exit"));
    private final java.util.List<Rectangle> mainMenuBounds = new ArrayList<>();
    private int mainMenuSelected = 0;

    public int getSelected() { return mainMenuSelected; }
    public void setSelected(int idx) { if (idx < 0) idx = 0; if (idx >= mainMenuItems.size()) idx = mainMenuItems.size()-1; this.mainMenuSelected = idx; }
    public int indexAt(Point p) {
        for (int i = 0; i < mainMenuBounds.size(); i++) {
            Rectangle r = mainMenuBounds.get(i);
            if (r.contains(p)) return i;
        }
        return -1;
    }

    @Override
    public void render(Graphics2D g, GameModel model, int width, int height) {
        drawCenteredString(g, "TYPE SURVIVORS", height / 6, new Color(30, 144, 255), Font.BOLD, 60, width);
        drawCenteredString(g, "Type words to destroy enemies!", height / 6 + 60, Color.WHITE, Font.PLAIN, 24, width);
        // render leaderboard under the subtitle
        drawLeaderboard(g, model, height / 6 + 120, width);

        drawCenteredString(g, "Press ENTER to select or use mouse", height - 80, Color.GREEN, Font.BOLD, 18, width);
        int menuStartY = height - 220;
        int spacing = 36;
        mainMenuBounds.clear();
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        for (int i = 0; i < mainMenuItems.size(); i++) {
            String label = mainMenuItems.get(i);
            int y = menuStartY + i * spacing;
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(label);
            int x = (width - textW) / 2;

            if (i == mainMenuSelected) {
                g.setColor(Color.GREEN);
                g.drawString("->", x - 40, y);
                g.setColor(Color.WHITE);
                g.drawString(label, x, y);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(label, x, y);
            }

            mainMenuBounds.add(new Rectangle(x, y - fm.getAscent(), textW, fm.getHeight()));
        }
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
}
