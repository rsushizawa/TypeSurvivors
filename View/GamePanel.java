package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;

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

        // Mouse handling for main menu selection & clicks
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (model.getGameState() != Data.GameState.MAIN_MENU) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                int idx = menuRenderer.getMainMenuIndexAtPoint(gp);
                if (idx >= 0) {
                    menuRenderer.setMainMenuSelected(idx);
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (model.getGameState() == Data.GameState.MAIN_MENU) {
                    Point gp = panelToGame(e.getPoint());
                    if (gp == null) return;
                    int idx = menuRenderer.getMainMenuIndexAtPoint(gp);
                    if (idx >= 0) {
                        activateMainMenuSelection(idx);
                        return;
                    }
                }
                // if paused, allow clicking pause menu entries
                if (model.getGameState() == Data.GameState.PAUSED) {
                    Point gp = panelToGame(e.getPoint());
                    if (gp == null) return;
                    int pidx = menuRenderer.getPauseIndexAtPoint(gp);
                    if (pidx >= 0) {
                        activatePauseSelection(pidx);
                        return;
                    }
                }
            }
        });

        // Options UI mouse handling (map panel coords to game coords)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (model.getGameState() != Data.GameState.OPTIONS) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                // ensure layout
                menuRenderer.ensureOptionsLayout(parent.getGameWidth(), parent.getGameHeight());

                Rectangle mKnob = menuRenderer.getMusicKnobBounds();
                Rectangle mTrack = menuRenderer.getMusicSliderBounds();
                Rectangle sKnob = menuRenderer.getSfxKnobBounds();
                Rectangle sTrack = menuRenderer.getSfxSliderBounds();
                Rectangle fsBtn = menuRenderer.getFullscreenToggleBounds();

                int idx = menuRenderer.getOptionsIndexAtPoint(gp);

                if (mKnob.contains(gp) || mTrack.contains(gp)) {
                    draggingMusic = true;
                    updateMusicFromX(gp.x, mTrack, mKnob);
                } else if (sKnob.contains(gp) || sTrack.contains(gp)) {
                    draggingSfx = true;
                    updateSfxFromX(gp.x, sTrack, sKnob);
                } else if (fsBtn.contains(gp)) {
                    boolean newFs = !Config.GameConfig.FULLSCREEN;
                    parent.setFullscreen(newFs);
                } else if (idx >= 0) {
                    // click on option label area
                    menuRenderer.setOptionsSelected(idx);
                    if (idx == 0) { // music
                        draggingMusic = true;
                        updateMusicFromX(gp.x, mTrack, mKnob);
                    } else if (idx == 1) { // sfx
                        draggingSfx = true;
                        updateSfxFromX(gp.x, sTrack, sKnob);
                    } else if (idx == 2) { // fullscreen
                        parent.setFullscreen(!Config.GameConfig.FULLSCREEN);
                    } else if (idx == 3) { // back
                        model.setGameState(Data.GameState.MAIN_MENU);
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggingMusic = false;
                draggingSfx = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (model.getGameState() != Data.GameState.OPTIONS) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                menuRenderer.ensureOptionsLayout(parent.getGameWidth(), parent.getGameHeight());
                if (draggingMusic) {
                    updateMusicFromX(gp.x, menuRenderer.getMusicSliderBounds(), menuRenderer.getMusicKnobBounds());
                    repaint();
                } else if (draggingSfx) {
                    updateSfxFromX(gp.x, menuRenderer.getSfxSliderBounds(), menuRenderer.getSfxKnobBounds());
                    repaint();
                }
            }
        });

        // mouse move in options: highlight hovered entry
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (model.getGameState() != Data.GameState.OPTIONS) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                menuRenderer.ensureOptionsLayout(parent.getGameWidth(), parent.getGameHeight());
                int idx = menuRenderer.getOptionsIndexAtPoint(gp);
                if (idx >= 0) {
                    menuRenderer.setOptionsSelected(idx);
                    repaint();
                }
            }
        });
    }

    private boolean draggingMusic = false;
    private boolean draggingSfx = false;

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

    // Map a point in panel coordinates to the game's base resolution coordinates
    private Point panelToGame(Point p) {
        int panelW = getWidth();
        int panelH = getHeight();
        int gw = parent.getGameWidth();
        int gh = parent.getGameHeight();
        double scale = Math.min(panelW / (double) gw, panelH / (double) gh);
        int drawW = (int) Math.round(gw * scale);
        int drawH = (int) Math.round(gh * scale);
        int drawX = (panelW - drawW) / 2;
        int drawY = (panelH - drawH) / 2;
        if (p.x < drawX || p.x > drawX + drawW || p.y < drawY || p.y > drawY + drawH) return null;
        int gx = (int) Math.round((p.x - drawX) / scale);
        int gy = (int) Math.round((p.y - drawY) / scale);
        return new Point(gx, gy);
    }

    private void updateMusicFromX(int gx, Rectangle track, Rectangle knob) {
        int trackW = track.width - knob.width;
        int rel = Math.max(0, Math.min(track.width - knob.width, gx - track.x));
        float vol = trackW <= 0 ? 0f : (float) rel / (float) trackW;
        Audio.AudioManager.setMusicVolume(vol);
    }

    private void updateSfxFromX(int gx, Rectangle track, Rectangle knob) {
        int trackW = track.width - knob.width;
        int rel = Math.max(0, Math.min(track.width - knob.width, gx - track.x));
        float vol = trackW <= 0 ? 0f : (float) rel / (float) trackW;
        Audio.AudioManager.setSfxVolume(vol);
    }

    // Activate main menu entry at index (or currently selected if -1)
    public void activateMainMenuSelection(int idx) {
        int selected = idx >= 0 ? idx : menuRenderer.getMainMenuSelected();
        switch (selected) {
            case 0: // Start Game
                model.startNewGame();
                Audio.AudioManager.playGameMusic();
                break;
            case 1: // Options
                model.setGameState(Data.GameState.OPTIONS);
                break;
            case 2: // Exit
                System.exit(0);
                break;
        }
        repaint();
    }

    public int getMainMenuSelection() { return menuRenderer.getMainMenuSelected(); }
    public void setMainMenuSelection(int idx) { menuRenderer.setMainMenuSelected(idx); repaint(); }
    public void activateMainMenuSelection() { activateMainMenuSelection(-1); }

    public int getOptionsSelection() { return menuRenderer.getOptionsSelected(); }
    public void setOptionsSelection(int idx) { menuRenderer.setOptionsSelected(idx); repaint(); }

    // Pause menu activation
    public void activatePauseSelection(int idx) {
        int sel = idx >= 0 ? idx : menuRenderer.getPauseSelected();
        switch (sel) {
            case 0: // Resume
                model.togglePause();
                break;
            case 1: // Options
                model.setGameState(Data.GameState.OPTIONS);
                break;
            case 2: // Main Menu
                model.returnToMenu();
                break;
        }
        repaint();
    }

    public int getPauseSelection() { return menuRenderer.getPauseSelected(); }
    public void setPauseSelection(int idx) { menuRenderer.setPauseSelected(idx); repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Render everything into an offscreen buffer at the game's base resolution,
        // then scale that buffer to the panel while preserving aspect ratio and
        // painting black bars for remaining space.
        int panelW = getWidth();
        int panelH = getHeight();
        int gw = parent.getGameWidth();
        int gh = parent.getGameHeight();

        BufferedImage buffer = new BufferedImage(gw, gh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gb = buffer.createGraphics();
        gb.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Image backgroundImage = parent.getBackgroundImage();
        if (backgroundImage != null) {
            gb.drawImage(backgroundImage, 0, 0, gw, gh, this);
        } else {
            gb.setColor(Color.BLACK);
            gb.fillRect(0, 0, gw, gh);
        }

        // Updated switch to handle new states (render into gb using game resolution gw/gh)
        switch (model.getGameState()) {
            case MAIN_MENU:
                menuRenderer.renderMainMenu(gb, model, gw, gh);
                break;
            case PLAYING:
                drawGame(gb, gw, gh);
                drawWaveStatus(gb, gw, gh);
                break;
            case PAUSED:
                drawGame(gb, gw, gh);
                menuRenderer.renderPauseOverlay(gb, gw, gh);
                break;
            case LEVEL_UP_CHOICE:
                drawGame(gb, gw, gh);
                levelUpRenderer.renderLevelUpScreen(gb, model, gw, gh-800);
                break;
            case ENTERING_NAME:
                drawGame(gb, gw, gh);
                menuRenderer.renderNameEntry(gb, model, gw, gh-300);
                break;
            case GAME_OVER:
                drawGame(gb, gw, gh);
                menuRenderer.renderGameOver(gb, model, gw, gh-300);
                break;
            case OPTIONS:
                // draw whatever is behind (game or menu) depending on context - here we'll darken and draw options
                // If previous state was playing we might still want to draw the game behind; keep it simple: fill translucent background
                gb.setColor(new Color(0,0,0,200));
                gb.fillRect(0,0,gw,gh);
                menuRenderer.renderOptionsMenu(gb, model, gw, gh);
                break;
        }

        gb.dispose();

        // paint black background
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, panelW, panelH);

        double scale = Math.min(panelW / (double) gw, panelH / (double) gh);
        int drawW = (int) Math.round(gw * scale);
        int drawH = (int) Math.round(gh * scale);
        int drawX = (panelW - drawW) / 2;
        int drawY = (panelH - drawH) / 2;

        g2d.drawImage(buffer, drawX, drawY, drawW, drawH, null);
    }

    private void drawCenteredString(Graphics2D g, String text, int y, Color color, String fontName, int fontStyle, int fontSize, int width) {
        g.setColor(color);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (width - fm.stringWidth(text)) / 2, y);
    }

    private void drawCenteredString(Graphics2D g, String text, int y, Color color, int fontStyle, int fontSize, int width) {
        drawCenteredString(g, text, y, color, "Monospaced", fontStyle, fontSize, width);
    }

    private void drawGame(Graphics2D g, int gw, int gh) {
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
        g.drawLine(0, Enemy.PLAYER_Y_LINE, gw, Enemy.PLAYER_Y_LINE);

        // Draw wall
        if (model.isWallActive()) {
            g.setColor(Theme.wallLine);
            g.setStroke(new BasicStroke(Config.GameConfig.WALL_STROKE));
            int y = model.getWallYPosition();
            g.drawLine(0, y, gw, y);
        }

        g.setStroke(new BasicStroke(1));

        // HUD (typing, stats, cooldowns, XP bar)
        hudRenderer.render(g, model, cooldownIconBounds, cooldownIconTooltips, gw, gh);
    }

    private void drawWaveStatus(Graphics2D g, int gw, int gh) {
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
        g.fillRect(0, gh / 2 - 60, gw, 120);
        drawCenteredString(g, msg, gh / 2, Color.CYAN, Font.BOLD, 30, gw);
        if (model.getWaveNumber() > 0) {
            String nextMsg = String.format("Wave %d starting in %.1fs", model.getWaveNumber() + 1, secondsLeft);
            drawCenteredString(g, nextMsg, gh / 2 + 40, Color.CYAN, Font.BOLD, 30, gw);
        }
    }


}
