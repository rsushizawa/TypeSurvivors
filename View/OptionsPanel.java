package View;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import Model.GameModel;

public class OptionsPanel implements Renderable {

    private Rectangle musicSliderBounds = new Rectangle();
    private Rectangle musicKnobBounds = new Rectangle();
    private Rectangle sfxSliderBounds = new Rectangle();
    private Rectangle sfxKnobBounds = new Rectangle();
    private Rectangle fullscreenToggleBounds = new Rectangle();

    private final java.util.List<String> optionsItems = new ArrayList<>(Arrays.asList("Music Volume", "SFX Volume", "Fullscreen", "Back"));
    private final java.util.List<Rectangle> optionsBounds = new ArrayList<>();
    private int optionsSelected = 0;

    public int getSelected() { return optionsSelected; }
    public void setSelected(int idx) { if (idx < 0) idx = 0; if (idx >= optionsItems.size()) idx = optionsItems.size()-1; this.optionsSelected = idx; }
    public int indexAt(Point p) { for (int i=0;i<optionsBounds.size();i++) { Rectangle r = optionsBounds.get(i); if (r!=null && r.contains(p)) return i;} return -1; }

    @Override
    public void render(Graphics2D g, GameModel model, int width, int height) {
        // Faded black overlay to dim the background
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);

        drawCenteredString(g, "OPTIONS", height / 6, new Color(30, 144, 255), Font.BOLD, 48, width);

        int centerX = width / 2;
        int baseY = height / 6 + 80;

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

        optionsBounds.clear();
        int musicLabelX = trackX - 120;
        int musicLabelW = trackW + 240;
        optionsBounds.add(new Rectangle(musicLabelX, musicY - 40, musicLabelW, knobH + 40));

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
        optionsBounds.add(new Rectangle(musicLabelX, sfxY - 40, musicLabelW, knobH + 40));

        int fsY = sfxY + 80;
        int btnW = 160; int btnH = 36;
        int btnX = centerX - btnW/2;
        fullscreenToggleBounds.setBounds(btnX, fsY - btnH/2, btnW, btnH);
        boolean fsOn = Config.GameConfig.FULLSCREEN;
        g.setColor(fsOn ? new Color(80,200,120) : new Color(120,120,120));
        g.fillRoundRect(btnX, fsY - btnH/2, btnW, btnH, 8, 8);
        g.setColor(Color.WHITE);
        drawCenteredString(g, fsOn ? "Fullscreen: ON" : "Fullscreen: OFF", fsY + 6, Color.WHITE, Font.BOLD, 16, width);
        optionsBounds.add(new Rectangle(btnX - 10, fsY - btnH/2 - 6, btnW + 20, btnH + 12));

        int backY = height - 80 - 20;
        int backW = 160; int backH = 28;
        int backX = width/2 - backW/2;
        optionsBounds.add(new Rectangle(backX, backY - backH/2, backW, backH));

        if (optionsSelected >= 0 && optionsSelected < optionsBounds.size()) {
            Rectangle sel = optionsBounds.get(optionsSelected);
            g.setColor(new Color(255, 255, 255, 30));
            g.fillRect(sel.x, sel.y, sel.width, sel.height);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("->", sel.x - 30, sel.y + sel.height/2 + 6);
        }

        drawCenteredString(g, "Click and drag sliders or click the button. ESC to return.", height - 40, Color.GREEN, Font.PLAIN, 16, width);
    }

    public void ensureLayout(int width, int height) {
        BufferedImage tmp = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmp.createGraphics();
        render(g, null, width, height);
        g.dispose();
    }

    public Rectangle getMusicSliderBounds() { return musicSliderBounds; }
    public Rectangle getMusicKnobBounds() { return musicKnobBounds; }
    public Rectangle getSfxSliderBounds() { return sfxSliderBounds; }
    public Rectangle getSfxKnobBounds() { return sfxKnobBounds; }
    public Rectangle getFullscreenToggleBounds() { return fullscreenToggleBounds; }

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
