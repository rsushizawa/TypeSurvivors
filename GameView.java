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

            if (model.isGameOver()) {
                drawGameOver(g2d);
            } else {
                drawGame(g2d);
                drawWaveStatus(g2d);
            }
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
            g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2 - 20);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            String finalScore = "Final Score: " + model.getScore();
            fm = g.getFontMetrics();
            g.drawString(finalScore, (getWidth() - fm.stringWidth(finalScore)) / 2, getHeight() / 2 + 30);
        }
    }
}