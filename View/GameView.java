package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.List;

import Model.GameModel;
import Entity.Enemy.Enemy;
import Entity.Player.Player;
import Entity.Projectile.Projectile;
import Data.WaveState;
import Data.HighScoreEntry;

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
            drawCenteredString(g, "TYPE SURVIVORS", getHeight() / 6, new Color(30, 144, 255), Font.BOLD, 60);
            drawCenteredString(g, "Type words to destroy enemies!", getHeight() / 6 + 60, Color.WHITE, Font.PLAIN, 24);
            
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
            if (displayName.isEmpty()) {
                displayName = "_";
            } else {
                displayName += "_";
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

            g.setColor(Color.ORANGE);
            for (Projectile p : model.getProjectiles()) {
                g.fillOval(p.x - 3, p.y - 3, 6, 6);
            }

            List<Enemy> enemies = model.getEnemies();
            enemies.sort((e1, e2) -> Double.compare(e1.z, e2.z));

            for (Enemy enemy : enemies) {
                if (enemy.hasSprites()) {
                    BufferedImage sprite = enemy.getCurrentSprite();
                    if (sprite != null) {
                        int scaledWidth = enemy.getScaledWidth();
                        int scaledHeight = enemy.getScaledHeight();
                        
                        // Draw sprite centered on its X, with its base at Y
                        int drawX = enemy.x - (scaledWidth / 2);
                        int drawY = enemy.y - scaledHeight;
                        
                        g.drawImage(sprite, drawX, drawY, scaledWidth, scaledHeight, null);
                        
                        // Draw text below the sprite
                        float fontSize = (float)(16.0 * enemy.getScale());
                        if (fontSize < 8) fontSize = 8; // Don't let font get too small
                        
                        g.setFont(new Font("Monospaced", Font.BOLD, (int)fontSize));
                        FontMetrics fm = g.getFontMetrics();
                        int textWidth = fm.stringWidth(enemy.text);
                        int centeredX = enemy.x - (textWidth / 2);
                        int textY = enemy.y + 15; // Position text below the base
                        
                        if (enemy == target) {
                            g.setColor(Color.RED);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                        g.drawString(enemy.text, centeredX, textY);
                    }
                }
                // Fallback for no sprites (though all enemies have them now)
                else { 
                    g.setFont(new Font("Monospaced", Font.PLAIN, 20));
                    if (enemy == target) {
                        g.setColor(Color.RED);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.drawString(enemy.text, enemy.x, enemy.y);
                }
            }
            
            // Draw Player
            Player player = model.getPlayer();
            if (player != null && player.hasSprites()) {
                g.drawImage(player.getCurrentSprite(), player.x, player.y, null);
            }

            // Draw Red Line (Danger Zone)
            g.setColor(new Color(255, 0, 0, 150));
            g.setStroke(new BasicStroke(3));
            g.drawLine(0, Enemy.PLAYER_Y_LINE, getWidth(), Enemy.PLAYER_Y_LINE);
            g.setStroke(new BasicStroke(1));


            // Draw UI (Typing, Stats)
            g.setColor(new Color(0, 0, 0, 100)); // Semi-transparent bar for UI
            g.fillRect(0, getHeight() - 50, getWidth(), 50);
            
            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 22));
            g.drawString("> " + model.getDisplayTypedWord(), 20, getHeight() - 20);
            
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("Score: " + model.getScore(), 20, 30);
            g.drawString("Lives: " + model.getLives(), getWidth() - 100, 30);
            g.drawString("Wave: " + model.getWaveNumber(), getWidth() / 2 - 40, 30);
            g.drawString("WPM: " + model.getWPM(), 20, 55);
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
    }
}