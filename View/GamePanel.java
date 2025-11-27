package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.List;

import Model.GameModel;
import Entity.Enemy.Enemy;
import GameObject.PoisonWall;
import Config.Theme;
import Entity.Player.Player;
import Entity.Projectile.Projectile;
import Data.WaveState;

public class GamePanel extends JPanel {

    private final java.util.List<Rectangle> cooldownIconBounds = new ArrayList<>();
    private final java.util.List<String> cooldownIconTooltips = new ArrayList<>();

    private final GameView parent;
    private final GameModel model;
    private final HudRenderer hudRenderer = new HudRenderer();
    private final MenuRenderer menuRenderer = new MenuRenderer();
    private final LevelUpRenderer levelUpRenderer = new LevelUpRenderer();

    public GamePanel(GameView parent, GameModel model) {
        this.parent = parent;
        this.model = model;
        setToolTipText("");

        setFocusTraversalKeysEnabled(false);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });
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

        Image backgroundImage = parent.getBackgroundImage();
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        // Updated switch to handle new states
        switch (model.getGameState()) {
            case MAIN_MENU:
                menuRenderer.renderMainMenu(g2d, model, getWidth(), getHeight());
                break;
            case PLAYING:
                drawGame(g2d);
                drawWaveStatus(g2d);
                break;
            case PAUSED:
                drawGame(g2d);
                menuRenderer.renderPauseOverlay(g2d, getWidth(), getHeight());
                break;
            case LEVEL_UP_CHOICE:
                drawGame(g2d);
                levelUpRenderer.renderLevelUpScreen(g2d, model, getWidth(), getHeight()-300);
                break;
            case ENTERING_NAME:
                drawGame(g2d);
                menuRenderer.renderNameEntry(g2d, model, getWidth(), getHeight());
                break;
            case GAME_OVER:
                drawGame(g2d);
                menuRenderer.renderGameOver(g2d, model, getWidth(), getHeight());
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
                    int fontSize = Config.GameConfig.ENEMY_FONT_SIZE;
                    g.setFont(new Font("Monospaced", Font.BOLD, fontSize));
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
            } else {
                int fontSize = Config.GameConfig.ENEMY_FONT_SIZE;
                g.setFont(new Font("Monospaced", Font.BOLD, fontSize));
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(enemy.text);
                int centeredX = enemy.x - (textWidth / 2);
                int textY = enemy.y;
                if (enemy == target) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(enemy.text, centeredX, textY);
            }
        }

        // Draw player (apply wrong-char shake offset)
        Player player = model.getPlayer();
        int shakeX = model.getShakeOffsetX();
        if (player != null && player.hasSprites()) {
            g.drawImage(player.getCurrentSprite(), player.x + shakeX, player.y, null);
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

        // HUD (typing, stats, cooldowns, XP bar)
        hudRenderer.render(g, model, cooldownIconBounds, cooldownIconTooltips, getWidth(), getHeight());
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


}
