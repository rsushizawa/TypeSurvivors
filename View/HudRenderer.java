package View;

import java.awt.*;
import java.util.List;

import Model.GameModel;
import Manager.UpgradeManager;

/**
 * Responsible for drawing HUD elements: typing box, stats, cooldown icons, and XP bar.
 */
public class HudRenderer {

    public void render(Graphics2D g, GameModel model, List<Rectangle> cooldownIconBounds, List<String> cooldownIconTooltips, int panelWidth, int panelHeight) {
        // UI (typing & stats)
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, panelHeight - 50, panelWidth, 50);

        g.setColor(Color.GREEN);
        g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.TYPING_FONT));
        g.drawString("> " + model.getDisplayTypedWord(), 20, panelHeight - 20);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.STATS_FONT));
        g.drawString("Score: " + model.getScore(), 20, 30);
        g.drawString("Lives: " + model.getLives(), panelWidth - 100, 30);
        g.drawString("Wave: " + model.getWaveNumber(), panelWidth / 2 - 40, 30);
        g.drawString("WPM: " + model.getWPM(), 20, 55);

        // Cooldown icons (stacked vertically)
        int iconSize = Config.GameConfig.COOLDOWN_ICON_SIZE;
        int padding = Config.GameConfig.COOLDOWN_ICON_PADDING;
        int ox = panelWidth - padding - iconSize;
        int startY = Config.GameConfig.COOLDOWN_TOP_OFFSET;
        int spacing = Config.GameConfig.COOLDOWN_VERTICAL_SPACING;

        // clear previous icon bounds/tooltips
        cooldownIconBounds.clear();
        cooldownIconTooltips.clear();

        int iconIndex = 0;
        UpgradeManager umgr = model.getUpgradeManager();

        // Fire Ball
        if (umgr.hasUpgrade("Fire Ball")) {
            double fbCur = model.getFireBallCooldown();
            double fbMax = model.getFireBallMaxCooldown();
            double fbFrac = fbMax > 0 ? Math.max(0.0, Math.min(1.0, (fbMax - fbCur) / fbMax)) : 1.0;
            int y = startY + iconIndex * (iconSize + spacing);

            boolean ready = fbCur <= 0.0;
            if (ready) {
                // faint glow when ready
                g.setColor(new Color(255, 160, 40, 90));
                int pad = 8;
                g.fillOval(ox - pad, y - pad, iconSize + pad * 2, iconSize + pad * 2);
            }

            // background box
            g.setColor(Color.DARK_GRAY);
            g.fillRoundRect(ox, y, iconSize, iconSize, 8, 8);

            // progress overlay
            g.setColor(new Color(255, 140, 0));
            g.fillRoundRect(ox, y + (int) (iconSize * (1.0 - fbFrac)), iconSize, (int) (iconSize * fbFrac), 8, 8);

            // fire glyph
            int cx = ox + iconSize / 2;
            int cy = y + iconSize / 2;
            g.setColor(Color.WHITE);
            g.fillOval(cx - 5, cy - 6, 10, 10);
            g.setColor(new Color(255, 120, 0));
            g.fillOval(cx - 4, cy - 5, 8, 8);

            cooldownIconBounds.add(new Rectangle(ox, y, iconSize, iconSize));
            String tipF = ready ? "Fire Ball — Ready" : String.format("Fire Ball — %.1fs", fbCur);
            cooldownIconTooltips.add(tipF);

            iconIndex++;
        }

        // Insect Spray
        if (umgr.hasUpgrade("Insect Spray")) {
            double isCur = model.getInsectSprayCooldown();
            double isMax = model.getInsectSprayMaxCooldown();
            double isFrac = isMax > 0 ? Math.max(0.0, Math.min(1.0, (isMax - isCur) / isMax)) : 1.0;
            int y = startY + iconIndex * (iconSize + spacing);

            boolean ready = isCur <= 0.0;
            if (ready) {
                g.setColor(new Color(80, 220, 80, 80));
                int pad = 8;
                g.fillOval(ox - pad, y - pad, iconSize + pad * 2, iconSize + pad * 2);
            }

            g.setColor(Color.DARK_GRAY);
            g.fillRoundRect(ox, y, iconSize, iconSize, 8, 8);

            g.setColor(new Color(50, 150, 30));
            g.fillRoundRect(ox, y + (int) (iconSize * (1.0 - isFrac)), iconSize, (int) (iconSize * isFrac), 8, 8);

            // droplet glyph
            int cx = ox + iconSize / 2;
            int cy = y + iconSize / 2 - 2;
            g.setColor(new Color(220, 255, 220));
            int[] xs = {cx, cx - 5, cx + 5};
            int[] ys = {cy - 8, cy + 6, cy + 6};
            g.fillPolygon(xs, ys, 3);

            cooldownIconBounds.add(new Rectangle(ox, y, iconSize, iconSize));
            String tipI = ready ? "Insect Spray — Ready" : String.format("Insect Spray — %.1fs", isCur);
            cooldownIconTooltips.add(tipI);

            iconIndex++;
        }

        // Split Shot
        if (umgr.hasUpgrade("Split Shot")) {
            double ssCur = model.getSplitShotCooldown();
            double ssMax = model.getSplitShotMaxCooldown();
            double ssFrac = ssMax > 0 ? Math.max(0.0, Math.min(1.0, (ssMax - ssCur) / ssMax)) : 1.0;
            int y = startY + iconIndex * (iconSize + spacing);

            boolean ready = ssCur <= 0.0;
            if (ready) {
                g.setColor(new Color(120, 140, 255, 80));
                int pad = 8;
                g.fillOval(ox - pad, y - pad, iconSize + pad * 2, iconSize + pad * 2);
            }

            g.setColor(Color.DARK_GRAY);
            g.fillRoundRect(ox, y, iconSize, iconSize, 8, 8);
            g.setColor(new Color(120, 120, 255));
            g.fillRoundRect(ox, y + (int) (iconSize * (1.0 - ssFrac)), iconSize, (int) (iconSize * ssFrac), 8, 8);

            // three small bullets glyph
            int bx = ox + iconSize / 2 - 6;
            int by = y + iconSize / 2;
            g.setColor(Color.WHITE);
            g.fillOval(bx, by - 3, 5, 5);
            g.fillOval(bx + 6, by - 6, 5, 5);
            g.fillOval(bx + 12, by - 3, 5, 5);

            cooldownIconBounds.add(new Rectangle(ox, y, iconSize, iconSize));
            String tipS = ready ? "Split Shot — Ready" : String.format("Split Shot — %.1fs", ssCur);
            cooldownIconTooltips.add(tipS);

            iconIndex++;
        }

        // XP Bar
        UpgradeManager um = model.getUpgradeManager();
        int xp = um.getPlayerXP();
        int next = um.getXPToNextLevel();
        int level = um.getPlayerLevel();
        g.drawString("Lvl: " + level, 20, 80);

        int barWidth = panelWidth - 40;
        int barHeight = 10;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(20, 85, barWidth, barHeight);

        double xpPercent = (double)xp / next;
        g.setColor(Color.MAGENTA);
        g.fillRect(20, 85, (int)(barWidth * xpPercent), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(20, 85, barWidth, barHeight);
    }
}
