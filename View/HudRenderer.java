package View;

import java.awt.*;
import java.util.List;

import Model.GameModel;
import Manager.UpgradeManager;

public class HudRenderer {

    public void render(Graphics2D g, GameModel model, List<Rectangle> cooldownIconBounds, List<String> cooldownIconTooltips, int panelWidth, int panelHeight) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, panelHeight - 50, panelWidth, 50);

    g.setColor(Color.GREEN);
    g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.TYPING_FONT));
    int shake = model.getShakeOffsetX();
    g.drawString("> " + model.getDisplayTypedWord(), 20 + shake, panelHeight - 20);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.STATS_FONT));
        g.drawString("Score: " + model.getScore(), 20, 30);
        String livesText = String.format("Lives: %d/%d", model.getLives(), model.getMaxLives());
        g.drawString(livesText, panelWidth - 160, 30);
        g.drawString("Wave: " + model.getWaveNumber(), panelWidth / 2 - 40, 30);
        g.drawString("WPM: " + model.getWPM(), 20, 55);

        double regenRate = model.getActiveRegenRate();
        double regenRemaining = model.getActiveRegenRemaining();
        if (regenRate > 0.0 && regenRemaining > 0.0) {
            String regenText = String.format("Regen: +%.1f hp/s (%.1fs)", regenRate, regenRemaining);
            g.setColor(new Color(120, 255, 120));
            g.setFont(new Font("Monospaced", Font.PLAIN, Config.GameConfig.STATS_FONT - 2));
            int rx = panelWidth - 360;
            g.drawString(regenText, rx, 30);

            double regenCD = model.getHealthRegenCooldown();
            double regenCDMax = model.getHealthRegenMaxCooldown();
            if (regenCD > 0 && regenCDMax > 0) {
                int barW = 120;
                int barH = 8;
                int barX = rx;
                int barY = 34;
                g.setColor(Color.DARK_GRAY);
                g.fillRect(barX, barY, barW, barH);
                double frac = Math.max(0.0, Math.min(1.0, 1.0 - (regenCD / regenCDMax)));
                g.setColor(new Color(120, 255, 120));
                g.fillRect(barX, barY, (int) Math.round(barW * frac), barH);
                g.setColor(Color.WHITE);
                g.drawRect(barX, barY, barW, barH);
                String cdText = String.format("CD: %.1fs", regenCD);
                g.setFont(new Font("Monospaced", Font.PLAIN, Config.GameConfig.STATS_FONT - 4));
                g.drawString(cdText, barX + barW + 8, barY + barH);
            }
        }

        // Stun / Poison indicators
        double stunRem = model.getStunRemaining();
        double stunMax = model.getStunMax();
        double poisonRem = model.getPoisonRemaining();
        double poisonMax = model.getPoisonMax();

        int statusX = panelWidth - 360;
        int statusY = 50;
        int statusBarW = 140;
        int statusBarH = 8;

        if (stunRem > 0.0 && stunMax > 0.0) {
            g.setColor(new Color(80, 160, 255));
            g.setFont(new Font("Monospaced", Font.PLAIN, Config.GameConfig.STATS_FONT - 4));
            g.drawString(String.format("Stunned: %.1fs", stunRem), statusX, statusY);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(statusX, statusY + 4, statusBarW, statusBarH);
            double frac = Math.max(0.0, Math.min(1.0, (stunMax - stunRem) / stunMax));
            g.setColor(new Color(120, 200, 255));
            g.fillRect(statusX, statusY + 4, (int) Math.round(statusBarW * frac), statusBarH);
            g.setColor(Color.WHITE);
            g.drawRect(statusX, statusY + 4, statusBarW, statusBarH);
            statusY += 18;
        }

        if (poisonRem > 0.0 && poisonMax > 0.0) {
            g.setColor(new Color(200, 255, 120));
            g.setFont(new Font("Monospaced", Font.PLAIN, Config.GameConfig.STATS_FONT - 4));
            g.drawString(String.format("Poisoned: %.1fs", poisonRem), statusX, statusY);
            g.setColor(Color.DARK_GRAY);
            g.fillRect(statusX, statusY + 4, statusBarW, statusBarH);
            double frac = Math.max(0.0, Math.min(1.0, (poisonMax - poisonRem) / poisonMax));
            g.setColor(new Color(120, 200, 120));
            g.fillRect(statusX, statusY + 4, (int) Math.round(statusBarW * frac), statusBarH);
            g.setColor(Color.WHITE);
            g.drawRect(statusX, statusY + 4, statusBarW, statusBarH);
            statusY += 18;
        }

        int iconSize = Config.GameConfig.COOLDOWN_ICON_SIZE;
        int padding = Config.GameConfig.COOLDOWN_ICON_PADDING;
        int ox = panelWidth - padding - iconSize;
        int startY = Config.GameConfig.COOLDOWN_TOP_OFFSET;
        int spacing = Config.GameConfig.COOLDOWN_VERTICAL_SPACING;

        cooldownIconBounds.clear();
        cooldownIconTooltips.clear();

        int iconIndex = 0;
        UpgradeManager umgr = model.getUpgradeManager();

        for (Data.Upgrades.Upgrade u : umgr.getPlayerUpgrades()) {
            int y = startY + iconIndex * (iconSize + spacing);

            double maxCd;
            if ("Wall".equals(u.getName())) {
                maxCd = u.getParam2Value();
            } else {
                maxCd = u.getParam3Value();
            }
            UpgradeManager.UpgradeId uid = umgr.idFor(u);
            double curCd = uid != null ? model.getUpgradeCooldown(uid) : model.getUpgradeCooldown(u.getName());

            boolean ready = curCd <= 0.0;
            if (ready) {
                g.setColor(new Color(200, 200, 255, 60));
                int pad = 6;
                g.fillOval(ox - pad, y - pad, iconSize + pad * 2, iconSize + pad * 2);
            }

            g.setColor(Color.DARK_GRAY);
            g.fillRoundRect(ox, y, iconSize, iconSize, 8, 8);

            if (maxCd > 0) {
                double frac = Math.max(0.0, Math.min(1.0, (maxCd - curCd) / maxCd));
                g.setColor(new Color(120, 200, 255));
                g.fillRoundRect(ox, y + (int) (iconSize * (1.0 - frac)), iconSize, (int) (iconSize * frac), 8, 8);
            }

            java.awt.image.BufferedImage img = u.getIconImage();
            if (img != null) {
                g.drawImage(img, ox, y, iconSize, iconSize, null);
            } else {
                g.setColor(Color.WHITE);
                int fontSize = Math.max(10, iconSize / 2);
                g.setFont(new Font("Monospaced", Font.BOLD, fontSize));
                FontMetrics fm = g.getFontMetrics();
                String iconText = u.getIcon();
                int tx = ox + (iconSize - fm.stringWidth(iconText)) / 2;
                int ty = y + (iconSize + fm.getAscent()) / 2 - 2;
                g.drawString(iconText, tx, ty);
            }

            cooldownIconBounds.add(new Rectangle(ox, y, iconSize, iconSize));
            String tip = ready ? String.format("%s — Ready", u.getName()) : String.format("%s — %.1fs", u.getName(), curCd);
            cooldownIconTooltips.add(tip);

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
