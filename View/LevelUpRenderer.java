package View;

import java.awt.*;
import Data.Upgrades.Upgrade;
import Data.Parameter;
import Model.GameModel;
import Config.GameConfig;

import java.util.List;

public class LevelUpRenderer {

    public void renderLevelUpScreen(Graphics2D g, GameModel model, int width, int height) {
        g.setColor(new Color(0, 0, 0, 220)); // Dark overlay
        g.fillRect(0, 0, width, height);

        drawCenteredString(g, "LEVEL UP!", height / 6, Color.YELLOW, Font.BOLD, GameConfig.LEVELUP_TITLE_FONT, width);
        drawCenteredString(g, "Choose an Upgrade (Press 1, 2, or 3):", height / 6 + 50, Color.WHITE, Font.PLAIN, GameConfig.LEVELUP_SUBTITLE_FONT, width);

        List<Upgrade> offers = model.getUpgradeManager().getCurrentLevelUpOffer();
        if (offers.isEmpty()) {
            drawCenteredString(g, "No upgrades available...?", height / 2, Color.RED, Font.BOLD, 20, width);
            return;
        }

        int startY = height / 6 + 120;
        int cardHeight = GameConfig.LEVELUP_CARD_HEIGHT;
        int cardWidth = width - GameConfig.LEVELUP_CARD_PADDING * 2;
        int spacing = GameConfig.LEVELUP_CARD_SPACING;

        for (int i = 0; i < offers.size(); i++) {
            if (i >= 3) break;

            Upgrade upg = offers.get(i);
            int cardY = startY + i * (cardHeight + spacing);

            // Card background
            g.setColor(new Color(30, 30, 80));
            g.fillRoundRect(GameConfig.LEVELUP_CARD_PADDING, cardY, cardWidth, cardHeight, 15, 15);
            g.setColor(Color.CYAN);
            g.drawRoundRect(GameConfig.LEVELUP_CARD_PADDING, cardY, cardWidth, cardHeight, 15, 15);

            // Content
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            String levelText = (upg.level == 0) ? "(New)" : "(Lvl " + upg.level + " -> " + (upg.level + 1) + ")";
            g.drawString("[" + (i + 1) + "] " + upg.getName() + " " + levelText, 70, cardY + 35);

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Monospaced", Font.PLAIN, 16));
            g.drawString(upg.description, 70, cardY + 60);

            int paramY = cardY + 90;
            for (Parameter param : upg.getParameters()) {
                if (upg.level == 0) {
                    g.setColor(Color.WHITE);
                    g.drawString(String.format("%-18s: %s", param.name, param.getDisplayValue()), 80, paramY);
                } else {
                    String oldValue = param.getDisplayValue();
                    if (param.justSkipped) {
                        g.setColor(Color.GRAY);
                        g.drawString(String.format("%-18s: %s (Skipped)", param.name, oldValue), 80, paramY);
                    } else {
                        g.setColor(Color.GREEN);
                        double newValue = param.currentValue;
                        String newValueStr;
                        if (newValue == (long) newValue) {
                            newValueStr = String.format("%d%s", (long) newValue, param.unit);
                        } else {
                            newValueStr = String.format("%.1f%s", newValue, param.unit);
                        }
                        double oldValueNum = newValue - param.increasePerLevel;
                        String oldValueStr;
                        if (oldValueNum == (long) oldValueNum) {
                            oldValueStr = String.format("%d%s", (long) oldValueNum, param.unit);
                        } else {
                            oldValueStr = String.format("%.1f%s", oldValueNum, param.unit);
                        }
                        g.drawString(String.format("%-18s: %s -> %s", param.name, oldValueStr, newValueStr), 80, paramY);
                    }
                }
                paramY += 30;
            }
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
}
