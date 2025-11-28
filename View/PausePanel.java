package View;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import Model.GameModel;

public class PausePanel implements Renderable {

    private final java.util.List<String> pauseItems = new ArrayList<>(Arrays.asList("Resume", "Options", "Main Menu"));
    private java.util.List<Rectangle> pauseBounds = new ArrayList<>();
    private int pauseSelected = 0;

    public int getSelected() { return pauseSelected; }
    public void setSelected(int idx) { if (idx < 0) idx = 0; if (idx >= pauseItems.size()) idx = pauseItems.size()-1; this.pauseSelected = idx; }
    public int indexAt(Point p) { for (int i=0;i<pauseBounds.size();i++) { if (pauseBounds.get(i).contains(p)) return i; } return -1; }

    @Override
    public void render(Graphics2D g, GameModel model, int width, int height) {
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, width, height);
        drawCenteredString(g, "PAUSED", height / 2 - 120, Color.YELLOW, Font.BOLD, 48, width);

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

        if (pauseSelected >= 0 && pauseSelected < pauseBounds.size()) {
            Rectangle sel = pauseBounds.get(pauseSelected);
            g.setColor(new Color(255,255,255,30));
            g.fillRect(sel.x, sel.y, sel.width, sel.height);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("->", sel.x - 30, sel.y + sel.height/2 + 6);
        }

        drawCenteredString(g, "Use arrows or mouse to select, ENTER to confirm", height - 80, Color.GREEN, Font.BOLD, 16, width);
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
