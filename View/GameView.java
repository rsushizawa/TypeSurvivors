package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.List;

import Model.GameModel;
import Entity.Enemy.Enemy;
import GameObject.PoisonWall;
import Config.GameConfig;
import Config.Theme;
import Entity.Player.Player;
import Entity.Projectile.Projectile;
import Manager.UpgradeManager;
import Data.WaveState;
import Data.HighScoreEntry;
import Data.GameState;
import Data.Upgrades.Upgrade;
import Data.Parameter;

public class GameView {

    private final JFrame frame;
    private final GamePanel gamePanel;
    private final GameModel model;

    private Image backgroundImage;

    public GameView(GameModel model, int width, int height) {
        this.model = model;
        
        frame = new JFrame("Type Survivors");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setResizable(false);


        gamePanel = new GamePanel();
        frame.add(gamePanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();
    }

 
    public void addGameKeyListener(KeyListener listener) {
        gamePanel.addKeyListener(listener);
    }


    public void repaint() {
        gamePanel.repaint();
    }


    private class GamePanel extends JPanel {

        // Bounds and tooltips for cooldown icons (updated each paint)
        private final java.util.List<Rectangle> cooldownIconBounds = new ArrayList<>();
        private final java.util.List<String> cooldownIconTooltips = new ArrayList<>();

        public GamePanel() {
            // enable Swing tooltips for this custom panel
            setToolTipText("");
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            Point p = event.getPoint();
            for (int i = 0; i < cooldownIconBounds.size(); i++) {
                Rectangle r = cooldownIconBounds.get(i);
                if (r.contains(p)) {
                    return cooldownIconTooltips.get(i);
                }
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // Updated switch to handle new states
            switch (model.getGameState()) {
                case MAIN_MENU:
                    drawMainMenu(g2d);
                    break;
                case PLAYING:
                    drawGame(g2d);
                    drawWaveStatus(g2d);
                    break;
                case PAUSED:
                    drawGame(g2d); 
                    drawPauseOverlay(g2d);
                    break;
            case LEVEL_UP_CHOICE:
                drawGame(g2d);
                    drawLevelUpScreen(g2d);
                    break;
                case ENTERING_NAME:
                    drawGame(g2d);
                    drawNameEntry(g2d);
                    break;
                case GAME_OVER:
                    drawGame(g2d);
                    drawGameOver(g2d);
                    break;
            }
        }

        private void drawCenteredString(Graphics2D g, String text, int y, Color color, String fontName, int fontStyle, int fontSize) {
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(text, (getWidth() - fm.stringWidth(text)) / 2, y);
        }

        private void drawCenteredString(Graphics2D g, String text, int y, Color color, int fontStyle, int fontSize) {
            drawCenteredString(g, text, y, color, "Monospaced", fontStyle, fontSize);
        }

        private void drawMainMenu(Graphics2D g) {
            drawCenteredString(g, "TYPE SURVIVORS", getHeight() / 6, new Color(30, 144, 255), Font.BOLD, Config.GameConfig.MAINMENU_TITLE_FONT);
            drawCenteredString(g, "Type words to destroy enemies!", getHeight() / 6 + 60, Color.WHITE, Font.PLAIN, Config.GameConfig.MAINMENU_SUB_FONT);
            
            drawLeaderboard(g, getHeight() / 6 + 120);

            drawCenteredString(g, "Press ENTER to Start", getHeight() - 180, Color.GREEN, Font.BOLD, 28);

            String[] instructions = {
                "How to Play:",
                "- Type the letters of approaching enemies",
                "- Each letter fires a projectile",
                "- Complete words before they reach you",
                "- Press BACKSPACE to cancel current word",
                "- Press ESC to pause the game"
            };

            int startY = getHeight() - 140;
            for (int i = 0; i < instructions.length; i++) {
                if (i == 0) {
                    drawCenteredString(g, instructions[i], startY + (i * 25), Color.CYAN, Font.BOLD, 18);
                } else {
                    drawCenteredString(g, instructions[i], startY + (i * 25), Color.LIGHT_GRAY, Font.PLAIN, 14);
                }
            }
        }

        private void drawLeaderboard(Graphics2D g, int startY) {
            drawCenteredString(g, "=== HIGH SCORES ===", startY, Color.YELLOW, Font.BOLD, 24);

            List<HighScoreEntry> scores = model.getLeaderboardManager().getHighScores();
            
            if (scores.isEmpty()) {
                drawCenteredString(g, "No scores yet!", startY + 40, Color.GRAY, Font.PLAIN, 18);
                return;
            }

            g.setColor(Color.CYAN);
            g.setFont(new Font("Monospaced", Font.BOLD, 14));
            FontMetrics fm = g.getFontMetrics();
            
            String header = String.format("%-3s %-10s %8s %5s %5s", "#", "NAME", "SCORE", "WAVE", "WPM");
            g.drawString(header, (getWidth() - fm.stringWidth(header)) / 2, startY + 40);

            g.setFont(new Font("Monospaced", Font.PLAIN, 14));
            fm = g.getFontMetrics();

            for (int i = 0; i < scores.size(); i++) {
                HighScoreEntry entry = scores.get(i);
                
                if (i % 2 == 0) {
                    g.setColor(Color.WHITE);
                } else {
                    g.setColor(new Color(200, 200, 200));
                }
                
                String line = String.format("%-3d %-10s %8d %5d %5d", 
                    i + 1, 
                    entry.getName(), 
                    entry.getScore(),
                    entry.getWave(),
                    entry.getMaxWPM());
                
                g.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, startY + 65 + (i * 25));
            }
        }

        private void drawNameEntry(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 230));
            g.fillRect(0, 0, getWidth(), getHeight());

            drawCenteredString(g, "NEW HIGH SCORE!", getHeight() / 3, Color.YELLOW, Font.BOLD, 48);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();
            String scoreText = "Score: " + model.getScore() + "  Wave: " + model.getWaveNumber() + "  WPM: " + model.getMaxWPM();
            g.drawString(scoreText, (getWidth() - fm.stringWidth(scoreText)) / 2, getHeight() / 3 + 50);

            drawCenteredString(g, "Enter Your Name:", getHeight() / 2 - 40, Color.CYAN, Font.BOLD, 28);

            int boxWidth = 300;
            int boxHeight = 50;
            int boxX = (getWidth() - boxWidth) / 2;
            int boxY = getHeight() / 2;

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
            g.drawString(hint, (getWidth() - fm.stringWidth(hint)) / 2, boxY + boxHeight + 30);

            drawCenteredString(g, "Press ENTER to Submit", getHeight() - 80, Color.GREEN, Font.BOLD, 22);
        }

        private void drawPauseOverlay(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            drawCenteredString(g, "PAUSED", getHeight() / 2 - 20, Color.YELLOW, Font.BOLD, 48);
            drawCenteredString(g, "Press ESC to Resume", getHeight() / 2 + 30, Color.WHITE, Font.PLAIN, 22);
        }

        private void drawGame(Graphics2D g) {
            Enemy target = model.getTargetEnemy();

            // Draw poison walls
            for (PoisonWall pw : model.getPoisonWalls()) {
                int w = (int) pw.width;
                int h = (int) pw.height;
                int left = pw.x - w/2;
                int top = pw.y - h/2;
                g.setColor(Theme.poisonFill);
                g.fillRect(left, top, w, h);
                g.setColor(Theme.poisonBorder);
                g.drawRect(left, top, w, h);
            }

            g.setColor(Color.ORANGE);
            for (Projectile p : model.getProjectiles()) {
                int r = Config.GameConfig.PROJECTILE_RADIUS;
                g.fillOval(p.x - r, p.y - r, r * 2, r * 2);
            }

            // Draw fireball effects
            for (GameObject.FireBallEffect f : model.getFireBallEffects()) {
                f.render(g);
            }

            List<Enemy> enemies = model.getEnemies();
            enemies.sort((e1, e2) -> Double.compare(e1.z, e2.z));

            for (Enemy enemy : enemies) {
                if (enemy.hasSprites()) {
                    BufferedImage sprite = enemy.getCurrentSprite();
                    if (sprite != null) {
                        int scaledWidth = enemy.getScaledWidth();
                        int scaledHeight = enemy.getScaledHeight();
                        
                        int drawX = enemy.x - (scaledWidth / 2);
                        int drawY = enemy.y - scaledHeight;
                        
                        g.drawImage(sprite, drawX, drawY, scaledWidth, scaledHeight, null);
                        
                        float fontSize = (float)(16.0 * enemy.getScale());
                        if (fontSize < 8) fontSize = 8;
                        
                        g.setFont(new Font("Monospaced", Font.BOLD, (int)fontSize));
                        FontMetrics fm = g.getFontMetrics();
                        int textWidth = fm.stringWidth(enemy.text);
                        int centeredX = enemy.x - (textWidth / 2);
                        int textY = enemy.y + 15;
                        
                        if (enemy == target) {
                            g.setColor(Color.RED);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                        g.drawString(enemy.text, centeredX, textY);
                    }
                }
            }
            
            // Draw player
            Player player = model.getPlayer();
            if (player != null && player.hasSprites()) {
                g.drawImage(player.getCurrentSprite(), player.x, player.y, null);
            }

            // Draw danger line
            g.setColor(Theme.dangerLine);
            g.setStroke(new BasicStroke(Config.GameConfig.DANGER_LINE_STROKE));
            g.drawLine(0, Enemy.PLAYER_Y_LINE, getWidth(), Enemy.PLAYER_Y_LINE);
            
            // Draw wall
            if (model.isWallActive()) {
                g.setColor(Theme.wallLine);
                g.setStroke(new BasicStroke(Config.GameConfig.WALL_STROKE));
                int y = model.getWallYPosition();
                g.drawLine(0, y, getWidth(), y);
            }
            
            g.setStroke(new BasicStroke(1));


            // UI (typing & stats)
            g.setColor(new Color(0, 0, 0, 100));
            g.fillRect(0, getHeight() - 50, getWidth(), 50);
            
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.TYPING_FONT));
            g.drawString("> " + model.getDisplayTypedWord(), 20, getHeight() - 20);
            
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.BOLD, Config.GameConfig.STATS_FONT));
            g.drawString("Score: " + model.getScore(), 20, 30);
            g.drawString("Lives: " + model.getLives(), getWidth() - 100, 30);
            g.drawString("Wave: " + model.getWaveNumber(), getWidth() / 2 - 40, 30);
            g.drawString("WPM: " + model.getWPM(), 20, 55);

            // Cooldown icons (stacked vertically)
            int iconSize = Config.GameConfig.COOLDOWN_ICON_SIZE;
            int padding = Config.GameConfig.COOLDOWN_ICON_PADDING;
            int ox = getWidth() - padding - iconSize;
            int startY = Config.GameConfig.COOLDOWN_TOP_OFFSET;
            int spacing = Config.GameConfig.COOLDOWN_VERTICAL_SPACING;

            // small font was previously used for letter labels; icons replace that

            // Draw cooldown icons only for purchased upgrades, stacked vertically
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
            
            int barWidth = getWidth() - 40;
            int barHeight = 10;
            g.setColor(Color.DARK_GRAY);
            g.fillRect(20, 85, barWidth, barHeight);
            
            double xpPercent = (double)xp / next;
            g.setColor(Color.MAGENTA);
            g.fillRect(20, 85, (int)(barWidth * xpPercent), barHeight);
            
            g.setColor(Color.WHITE);
            g.drawRect(20, 85, barWidth, barHeight);
        }

        private void drawWaveStatus(Graphics2D g) {
            if (model.getWaveState() != WaveState.INTERMISSION) {
                return;
            }

            double secondsLeft = (model.getIntermissionTickCounter() * GameModel.GAME_SPEED_MS) / 1000.0;
            String msg;

            if (model.getWaveNumber() == 0) {
                msg = String.format("Game Starting in %.1fs", secondsLeft);
            } else {
                msg = String.format("Wave %d Complete!", model.getWaveNumber());
            }
            
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, getHeight() / 2 - 60, getWidth(), 120);

            drawCenteredString(g, msg, getHeight() / 2, Color.CYAN, Font.BOLD, 30);

            if (model.getWaveNumber() > 0) {
                String nextMsg = String.format("Wave %d starting in %.1fs", model.getWaveNumber() + 1, secondsLeft);
                drawCenteredString(g, nextMsg, getHeight() / 2 + 40, Color.CYAN, Font.BOLD, 30);
            }
        }

        private void drawGameOver(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            
            drawCenteredString(g, "GAME OVER", getHeight() / 2 - 100, Color.RED, Font.BOLD, 48);
            drawCenteredString(g, "Final Score: " + model.getScore(), getHeight() / 2 - 30, Color.WHITE, Font.BOLD, 24);
            drawCenteredString(g, "Wave Reached: " + model.getWaveNumber(), getHeight() / 2 + 10, Color.WHITE, Font.PLAIN, 20);
            drawCenteredString(g, "Max WPM: " + model.getMaxWPM(), getHeight() / 2 + 40, Color.YELLOW, Font.PLAIN, 20);
            drawCenteredString(g, "Press ENTER to Return to Menu", getHeight() / 2 + 100, Color.GREEN, Font.BOLD, 22);
        }
        
        private void drawLevelUpScreen(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 220)); // Dark overlay
            g.fillRect(0, 0, getWidth(), getHeight());

            drawCenteredString(g, "LEVEL UP!", getHeight() / 6, Color.YELLOW, Font.BOLD, Config.GameConfig.LEVELUP_TITLE_FONT);
            drawCenteredString(g, "Choose an Upgrade (Press 1, 2, or 3):", getHeight() / 6 + 50, Color.WHITE, Font.PLAIN, Config.GameConfig.LEVELUP_SUBTITLE_FONT);

            List<Upgrade> offers = model.getUpgradeManager().getCurrentLevelUpOffer();
            if (offers.isEmpty()) {
                // Should not happen, but good to check
                drawCenteredString(g, "No upgrades available...?", getHeight() / 2, Color.RED, Font.BOLD, 20);
                return;
            }

            int startY = getHeight() / 6 + 120;
            int cardHeight = Config.GameConfig.LEVELUP_CARD_HEIGHT;
            int cardWidth = getWidth() - Config.GameConfig.LEVELUP_CARD_PADDING * 2;
            int spacing = Config.GameConfig.LEVELUP_CARD_SPACING;

            for (int i = 0; i < offers.size(); i++) {
                if (i >= 3) break;
                
                Upgrade upg = offers.get(i);
                int cardY = startY + i * (cardHeight + spacing);

                // Card background
                g.setColor(new Color(30, 30, 80));
                g.fillRoundRect(Config.GameConfig.LEVELUP_CARD_PADDING, cardY, cardWidth, cardHeight, 15, 15);
                g.setColor(Color.CYAN);
                g.drawRoundRect(Config.GameConfig.LEVELUP_CARD_PADDING, cardY, cardWidth, cardHeight, 15, 15);

                // Content
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 24));
                String levelText = (upg.level == 0) ? "(New)" : "(Lvl " + upg.level + " -> " + (upg.level + 1) + ")";
                g.drawString("[" + (i + 1) + "] " + upg.getName() + " " + levelText, 70, cardY + 35);
                
                g.setColor(Color.LIGHT_GRAY);
                g.setFont(new Font("Monospaced", Font.PLAIN, 16));
                g.drawString(upg.description, 70, cardY + 60);

                // Draw Parameters
                int paramY = cardY + 90;
                for (Parameter param : upg.getParameters()) {
                    
                    if (upg.level == 0) {
                        // Show base stats
                        g.setColor(Color.WHITE);
                        g.drawString(String.format("%-18s: %s", param.name, param.getDisplayValue()), 80, paramY);
                    } else {
                        // Show upgrade arrow
                        String oldValue = param.getDisplayValue();
                        
                        if (param.justSkipped) {
                            g.setColor(Color.GRAY); // Skipped
                            g.drawString(String.format("%-18s: %s (Skipped)", param.name, oldValue), 80, paramY);
                        } else {
                            g.setColor(Color.GREEN); // Upgraded
                            // Get the new value for display
                            double newValue = param.currentValue; // Already upgraded in manager
                            String newValueStr;
                            if (newValue == (long) newValue) {
                                newValueStr = String.format("%d%s", (long) newValue, param.unit);
                            } else {
                                newValueStr = String.format("%.1f%s", newValue, param.unit);
                            }
                            // Calculate old value for display
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
    }
}