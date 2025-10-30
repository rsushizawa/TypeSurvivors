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

        private void drawMainMenu(Graphics2D g) {
            g.setColor(new Color(30, 144, 255));
            g.setFont(new Font("Monospaced", Font.BOLD, 60));
            String title = "TYPE SURVIVORS";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, getHeight() / 3);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.PLAIN, 24));
            String subtitle = "Type words to destroy enemies!";
            fm = g.getFontMetrics();
            g.drawString(subtitle, (getWidth() - fm.stringWidth(subtitle)) / 2, getHeight() / 3 + 60);

            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 28));
            String start = "Press ENTER to Start";
            fm = g.getFontMetrics();
            g.drawString(start, (getWidth() - fm.stringWidth(start)) / 2, getHeight() / 2 + 40);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.PLAIN, 18));
            String[] instructions = {
                "How to Play:",
                "- Type the letters of falling words",
                "- Complete words before they reach the bottom",
                "- Press BACKSPACE to cancel current word",
                "- Press ESC to pause the game"
            };

            int startY = getHeight() / 2 + 120;
            for (int i = 0; i < instructions.length; i++) {
                String line = instructions[i];
                if (i == 0) {
                    g.setColor(Color.CYAN);
                    g.setFont(new Font("Monospaced", Font.BOLD, 20));
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.setFont(new Font("Monospaced", Font.PLAIN, 16));
                }
                fm = g.getFontMetrics();
                g.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, startY + (i * 30));
            }
        }

        private void drawPauseOverlay(Graphics2D g) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
            String msg = "PAUSED";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.PLAIN, 22));
            String resume = "Press ESC to Resume";
            fm = g.getFontMetrics();
            g.drawString(resume, (getWidth() - fm.stringWidth(resume)) / 2, getHeight() / 2 + 30);
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

            g.setColor(Color.CYAN);
            g.setFont(new Font("Monospaced", Font.BOLD, 30));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);

            if (model.getWaveNumber() > 0) {
                String nextMsg = String.format("Wave %d starting in %.1fs", model.getWaveNumber() + 1, secondsLeft);
                fm = g.getFontMetrics();
                g.drawString(nextMsg, (getWidth() - fm.stringWidth(nextMsg)) / 2, getHeight() / 2 + 40);
            }
        }

        private void drawGameOver(Graphics2D g) {
            g.setColor(Color.RED);
            g.setFont(new Font("Monospaced", Font.BOLD, 48));
            String msg = "GAME OVER";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2 - 60);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            String finalScore = "Final Score: " + model.getScore();
            fm = g.getFontMetrics();
            g.drawString(finalScore, (getWidth() - fm.stringWidth(finalScore)) / 2, getHeight() / 2);

            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.PLAIN, 20));
            String wpm = "WPM: " + model.getWPM();
            fm = g.getFontMetrics();
            g.drawString(wpm, (getWidth() - fm.stringWidth(wpm)) / 2, getHeight() / 2 + 40);

            g.setColor(Color.GREEN);
            g.setFont(new Font("Monospaced", Font.BOLD, 22));
            String restart = "Press ENTER to Return to Menu";
            fm = g.getFontMetrics();
            g.drawString(restart, (getWidth() - fm.stringWidth(restart)) / 2, getHeight() / 2 + 100);
        }
    }
}