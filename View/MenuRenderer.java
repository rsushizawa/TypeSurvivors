package View;

import java.awt.*;
import java.awt.image.BufferedImage;

import Model.GameModel;
import Data.HighScoreEntry;
import Config.GameConfig;

import java.util.Arrays;
import java.util.ArrayList;

public class MenuRenderer {

    private final java.util.List<String> mainMenuItems = new ArrayList<>(Arrays.asList("Start Game", "Options", "Exit"));
    private final java.util.List<Rectangle> mainMenuBounds = new ArrayList<>();
    private int mainMenuSelected = 0;

    public int getMainMenuSelected() { return mainMenuSelected; }
    public void setMainMenuSelected(int idx) { if (idx < 0) idx = 0; if (idx >= mainMenuItems.size()) idx = mainMenuItems.size()-1; this.mainMenuSelected = idx; }
    public int getMainMenuIndexAtPoint(Point p) {
        for (int i = 0; i < mainMenuBounds.size(); i++) {
            Rectangle r = mainMenuBounds.get(i);
            if (r.contains(p)) return i;
        }
        return -1;
    }
    private Rectangle musicSliderBounds = new Rectangle();
    private Rectangle musicKnobBounds = new Rectangle();
    private Rectangle sfxSliderBounds = new Rectangle();
    private Rectangle sfxKnobBounds = new Rectangle();
    private Rectangle fullscreenToggleBounds = new Rectangle();
    
    private final java.util.List<String> optionsItems = new ArrayList<>(Arrays.asList("Music Volume", "SFX Volume", "Fullscreen", "Back"));
    private final java.util.List<Rectangle> optionsBounds = new ArrayList<>();
    private int optionsSelected = 0;

    public int getOptionsSelected() { return optionsSelected; }
    public void setOptionsSelected(int idx) { if (idx < 0) idx = 0; if (idx >= optionsItems.size()) idx = optionsItems.size()-1; this.optionsSelected = idx; }
    public int getOptionsIndexAtPoint(Point p) {
        for (int i = 0; i < optionsBounds.size(); i++) {
            Rectangle r = optionsBounds.get(i);
            if (r != null && r.contains(p)) return i;
        }
        return -1;
    }

    public void renderMainMenu(Graphics2D g, GameModel model, int width, int height) {
        drawCenteredString(g, "TYPE SURVIVORS", height / 6, new Color(30, 144, 255), Font.BOLD, GameConfig.MAINMENU_TITLE_FONT, width);
        drawCenteredString(g, "Type words to destroy enemies!", height / 6 + 60, Color.WHITE, Font.PLAIN, GameConfig.MAINMENU_SUB_FONT, width);
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

            // pointer
            if (i == mainMenuSelected) {
                g.setColor(Color.GREEN);
                g.drawString("->", x - 40, y);
                g.setColor(Color.WHITE);
                g.drawString(label, x, y);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawString(label, x, y);
            }

            // store bounds
            mainMenuBounds.add(new Rectangle(x, y - fm.getAscent(), textW, fm.getHeight()));
        }

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
        drawCenteredString(g, "PAUSED", height / 2 - 120, Color.YELLOW, Font.BOLD, 48, width);

        // Pause menu entries
        if (pauseBounds == null) pauseBounds = new ArrayList<>();
        pauseBounds.clear();
        int startY = height / 2 - 40;
        int spacing = 40;
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        for (int i = 0; i < pauseItems.size(); i++) {
            String label = pauseItems.get(i);
            int y = startY + i * spacing;
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(label);
            int x = (width - textW) / 2;
            g.setColor(Color.WHITE);
            g.drawString(label, x, y);
            pauseBounds.add(new Rectangle(x, y - fm.getAscent(), textW, fm.getHeight()));
        }

        // highlight selected pause option
        if (pauseSelected >= 0 && pauseSelected < pauseBounds.size()) {
            Rectangle sel = pauseBounds.get(pauseSelected);
            g.setColor(new Color(255,255,255,30));
            g.fillRect(sel.x, sel.y, sel.width, sel.height);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("->", sel.x - 30, sel.y + sel.height/2 + 6);
        }

        // draw helper text
        drawCenteredString(g, "Use arrows or mouse to select, ENTER to confirm", height - 80, Color.GREEN, Font.BOLD, 16, width);
    }

    // Pause menu support
    private final java.util.List<String> pauseItems = new ArrayList<>(Arrays.asList("Resume", "Options", "Main Menu"));
    private java.util.List<Rectangle> pauseBounds = new ArrayList<>();
    private int pauseSelected = 0;

    public int getPauseSelected() { return pauseSelected; }
    public void setPauseSelected(int idx) { if (idx < 0) idx = 0; if (idx >= pauseItems.size()) idx = pauseItems.size()-1; this.pauseSelected = idx; }
    public int getPauseIndexAtPoint(Point p) {
        for (int i = 0; i < pauseBounds.size(); i++) {
            Rectangle r = pauseBounds.get(i);
            if (r.contains(p)) return i;
        }
        return -1;
    }

    public void renderOptionsMenu(Graphics2D g, GameModel model, int width, int height) {
        drawCenteredString(g, "OPTIONS", height / 6, new Color(30, 144, 255), Font.BOLD, 48, width);

        // Layout sliders
        int centerX = width / 2;
        int baseY = height / 6 + 80;

        // Music slider
        int trackW = Math.min(600, width - 200);
        int trackH = 10;
        int trackX = centerX - trackW / 2;
        int musicY = baseY;
        musicSliderBounds.setBounds(trackX, musicY - trackH/2, trackW, trackH);

        float musicVol = Audio.AudioManager.getMusicVolume();
        int knobW = 18; int knobH = 28;
        int knobX = trackX + Math.round(musicVol * (trackW - knobW));
        int knobY = musicY - knobH/2;
        musicKnobBounds.setBounds(knobX, knobY, knobW, knobH);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(trackX, musicY - trackH/2, trackW, trackH, trackH, trackH);
        g.setColor(Color.CYAN);
        g.fillRoundRect(trackX, musicY - trackH/2, Math.max(2, (int)(trackW * musicVol)), trackH, trackH, trackH);
        g.setColor(Color.DARK_GRAY);
        g.fillOval(knobX, knobY, knobW, knobH);
        g.setColor(Color.WHITE);
        drawCenteredString(g, "Music Volume", musicY - 30, Color.WHITE, Font.BOLD, 18, width);
        // store bounds for music option (label + track)
        optionsBounds.clear();
        int musicLabelX = trackX - 120;
        int musicLabelW = trackW + 240;
        optionsBounds.add(new Rectangle(musicLabelX, musicY - 40, musicLabelW, knobH + 40));

        // SFX slider
        int sfxY = baseY + 60;
        sfxSliderBounds.setBounds(trackX, sfxY - trackH/2, trackW, trackH);
        float sfxVol = Audio.AudioManager.getSfxVolume();
        int sfxKnobX = trackX + Math.round(sfxVol * (trackW - knobW));
        int sfxKnobY = sfxY - knobH/2;
        sfxKnobBounds.setBounds(sfxKnobX, sfxKnobY, knobW, knobH);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRoundRect(trackX, sfxY - trackH/2, trackW, trackH, trackH, trackH);
        g.setColor(new Color(180, 200, 255));
        g.fillRoundRect(trackX, sfxY - trackH/2, Math.max(2, (int)(trackW * sfxVol)), trackH, trackH, trackH);
        g.setColor(Color.DARK_GRAY);
        g.fillOval(sfxKnobX, sfxKnobY, knobW, knobH);
        g.setColor(Color.WHITE);
        drawCenteredString(g, "SFX Volume", sfxY - 30, Color.WHITE, Font.BOLD, 18, width);
        // store bounds for sfx option
        optionsBounds.add(new Rectangle(musicLabelX, sfxY - 40, musicLabelW, knobH + 40));

        // Fullscreen toggle (simple button)
        int fsY = sfxY + 80;
        int btnW = 160; int btnH = 36;
        int btnX = centerX - btnW/2;
        fullscreenToggleBounds.setBounds(btnX, fsY - btnH/2, btnW, btnH);
        boolean fsOn = Config.GameConfig.FULLSCREEN;
        g.setColor(fsOn ? new Color(80,200,120) : new Color(120,120,120));
        g.fillRoundRect(btnX, fsY - btnH/2, btnW, btnH, 8, 8);
        g.setColor(Color.WHITE);
        drawCenteredString(g, fsOn ? "Fullscreen: ON" : "Fullscreen: OFF", fsY + 6, Color.WHITE, Font.BOLD, 16, width);
        // store bounds for fullscreen option (use button area)
        optionsBounds.add(new Rectangle(btnX - 10, fsY - btnH/2 - 6, btnW + 20, btnH + 12));

        // Back option bounds
        int backY = height - 80 - 20;
        int backW = 160; int backH = 28;
        int backX = width/2 - backW/2;
        optionsBounds.add(new Rectangle(backX, backY - backH/2, backW, backH));

        // highlight the selected option
        if (optionsSelected >= 0 && optionsSelected < optionsBounds.size()) {
            Rectangle sel = optionsBounds.get(optionsSelected);
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(sel.x, sel.y, sel.width, sel.height);
            // draw pointer
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("->", sel.x - 30, sel.y + sel.height/2 + 6);
        }

        drawCenteredString(g, "Click and drag sliders or click the button. ESC to return.", height - 40, Color.GREEN, Font.PLAIN, 16, width);
    }

    // Ensure layout is computed (useful before first render)
    public void ensureOptionsLayout(int width, int height) {
        // call render offscreen to compute bounds without drawing - reuse renderOptionsMenu logic
        BufferedImage tmp = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmp.createGraphics();
        renderOptionsMenu(g, null, width, height);
        g.dispose();
    }

    public Rectangle getMusicSliderBounds() { return musicSliderBounds; }
    public Rectangle getMusicKnobBounds() { return musicKnobBounds; }
    public Rectangle getSfxSliderBounds() { return sfxSliderBounds; }
    public Rectangle getSfxKnobBounds() { return sfxKnobBounds; }
    public Rectangle getFullscreenToggleBounds() { return fullscreenToggleBounds; }

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
