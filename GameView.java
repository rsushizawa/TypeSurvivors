import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

import Model.*;

public class GameView {

    private final JFrame frame;
    private final GamePanel gamePanel;
    private final GameModel model;

    public GameView(GameModel model, int width, int height) {
        this.model = model;
        
        frame = new JFrame("TypoJam MVC");
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
            
            setBackground(Color.BLACK);

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
                case GAME_OVER:
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
            drawCenteredString(g, "TYPE SURVIVORS", getHeight() / 3, new Color(30, 144, 255), Font.BOLD, 60);
            drawCenteredString(g, "Type words to destroy enemies!", getHeight() / 3 + 60, Color.WHITE, Font.PLAIN, 24);
            drawCenteredString(g, "Press ENTER to Start", getHeight() / 2 + 40, Color.GREEN, Font.BOLD, 28);

            String[] instructions = {
                "How to Play:",
                "- Type the letters of falling words",
                "- Complete words before they reach the bottom",
                "- Press BACKSPACE to cancel current word",
                "- Press ESC to pause the game"
            };

            int startY = getHeight() / 2 + 120;
            for (int i = 0; i < instructions.length; i++) {
                if (i == 0) {
                    drawCenteredString(g, instructions[i], startY + (i * 30), Color.CYAN, Font.BOLD, 20);
                } else {
                    drawCenteredString(g, instructions[i], startY + (i * 30), Color.LIGHT_GRAY, Font.PLAIN, 16);
                }
            }
        }

        private void drawPauseOverlay(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            drawCenteredString(g, "PAUSED", getHeight() / 2 - 20, Color.YELLOW, Font.BOLD, 48);
            drawCenteredString(g, "Press ESC to Resume", getHeight() / 2 + 30, Color.WHITE, Font.PLAIN, 22);
        }

        private void drawGame(Graphics2D g) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 20));
            
            Enemy target = model.getTargetEnemy();

            for (Enemy enemy : model.getEnemies()) {
                if (enemy == target) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(enemy.text, enemy.x, enemy.y);
            }

            g.setColor(Color.RED);
            g.drawLine(0, getHeight() - 50, getWidth(), getHeight() - 50);

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
            drawCenteredString(g, "GAME OVER", getHeight() / 2 - 60, Color.RED, Font.BOLD, 48);
            drawCenteredString(g, "Final Score: " + model.getScore(), getHeight() / 2, Color.WHITE, Font.BOLD, 24);
            drawCenteredString(g, "WPM: " + model.getWPM(), getHeight() / 2 + 40, Color.YELLOW, Font.PLAIN, 20);
            drawCenteredString(g, "Press ENTER to Return to Menu", getHeight() / 2 + 100, Color.GREEN, Font.BOLD, 22);
        }
    }
}