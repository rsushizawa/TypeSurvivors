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
    private final MainMenuPanel mainMenuPanel = new MainMenuPanel();
    private final OptionsPanel optionsPanel = new OptionsPanel();
    private final PausePanel pausePanel = new PausePanel();
    private final LevelUpRenderer levelUpRenderer = new LevelUpRenderer();
    private int targetWidth;
    private int targetHeight;

    public GamePanel(GameView parent, GameModel model) {
        this.parent = parent;
        this.model = model;
        this.targetWidth = parent.getGameWidth();
        this.targetHeight = parent.getGameHeight();
        setToolTipText("");

        setFocusTraversalKeysEnabled(false);
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (model.getGameState() != Data.GameState.MAIN_MENU) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                int idx = mainMenuPanel.indexAt(gp);
                if (idx >= 0) {
                    mainMenuPanel.setSelected(idx);
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
                    int idx = mainMenuPanel.indexAt(gp);
                    if (idx >= 0) {
                        activateMainMenuSelection(idx);
                        return;
                    }
                }
                // if paused, allow clicking pause menu entries
                if (model.getGameState() == Data.GameState.PAUSED) {
                    Point gp = panelToGame(e.getPoint());
                    if (gp == null) return;
                    int pidx = pausePanel.indexAt(gp);
                    if (pidx >= 0) {
                        activatePauseSelection(pidx);
                        return;
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (model.getGameState() != Data.GameState.OPTIONS) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                // ensure layout
                optionsPanel.ensureLayout(parent.getGameWidth(), parent.getGameHeight());

                Rectangle mKnob = optionsPanel.getMusicKnobBounds();
                Rectangle mTrack = optionsPanel.getMusicSliderBounds();
                Rectangle sKnob = optionsPanel.getSfxKnobBounds();
                Rectangle sTrack = optionsPanel.getSfxSliderBounds();
                Rectangle fsBtn = optionsPanel.getFullscreenToggleBounds();

                int idx = optionsPanel.indexAt(gp);

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
                    optionsPanel.setSelected(idx);
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
                optionsPanel.ensureLayout(parent.getGameWidth(), parent.getGameHeight());
                if (draggingMusic) {
                    updateMusicFromX(gp.x, optionsPanel.getMusicSliderBounds(), optionsPanel.getMusicKnobBounds());
                    repaint();
                } else if (draggingSfx) {
                    updateSfxFromX(gp.x, optionsPanel.getSfxSliderBounds(), optionsPanel.getSfxKnobBounds());
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (model.getGameState() != Data.GameState.OPTIONS) return;
                Point gp = panelToGame(e.getPoint());
                if (gp == null) return;
                optionsPanel.ensureLayout(parent.getGameWidth(), parent.getGameHeight());
                int idx = optionsPanel.indexAt(gp);
                if (idx >= 0) {
                    optionsPanel.setSelected(idx);
                    repaint();
                }
            }
        });
    }

    public void setTargetResolution(int w, int h) {
        this.targetWidth = Math.max(1, w);
        this.targetHeight = Math.max(1, h);
        revalidate();
        repaint();
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

    private Point panelToGame(Point p) {
        int panelW = getWidth();
        int panelH = getHeight();
        int gw = targetWidth;
        int gh = targetHeight;
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

    public void activateMainMenuSelection(int idx) {
        int selected = idx >= 0 ? idx : mainMenuPanel.getSelected();
        switch (selected) {
            case 0: // Start Game
                model.startNewGame();
                Audio.AudioManager.playGameMusic();
                break;
            case 1: // Options
                model.setGameState(Data.GameState.OPTIONS);
                break;
            case 2: // Leaderboard
                model.setGameState(Data.GameState.LEADERBOARD);
                break;
            case 3: // Exit
                System.exit(0);
                break;
        }
        repaint();
    }

    public int getMainMenuSelection() { return mainMenuPanel.getSelected(); }
    public void setMainMenuSelection(int idx) { mainMenuPanel.setSelected(idx); repaint(); }
    public void activateMainMenuSelection() { activateMainMenuSelection(-1); }

    public int getOptionsSelection() { return optionsPanel.getSelected(); }
    public void setOptionsSelection(int idx) { optionsPanel.setSelected(idx); repaint(); }

    // Pause menu activation
    public void activatePauseSelection(int idx) {
        int sel = idx >= 0 ? idx : pausePanel.getSelected();
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

    public int getPauseSelection() { return pausePanel.getSelected(); }
    public void setPauseSelection(int idx) { pausePanel.setSelected(idx); repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int panelW = getWidth();
        int panelH = getHeight();
        int gw = targetWidth;
        int gh = targetHeight;

        BufferedImage buffer = new BufferedImage(gw, gh, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gb = buffer.createGraphics();
        gb.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gb.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Image backgroundImage = parent.getBackgroundImage();
        if (backgroundImage != null) {
            // If fullscreen and we're on the main menu or leaderboard, draw full image to panel later
            boolean fullscreenMenu = parent.isFullscreen() && (model.getGameState() == Data.GameState.MAIN_MENU || model.getGameState() == Data.GameState.LEADERBOARD);
            if (!fullscreenMenu) {
                // In windowed/menu overlay mode: crop horizontally if source is wider than the game width
                if (backgroundImage instanceof BufferedImage) {
                    BufferedImage bi = (BufferedImage) backgroundImage;
                    int srcW = bi.getWidth();
                    int srcH = bi.getHeight();
                    if (srcW > gw) {
                        int sx = (srcW - gw) / 2;
                        gb.drawImage(bi, 0, 0, gw, gh, sx, 0, sx + gw, srcH, this);
                    } else {
                        gb.drawImage(backgroundImage, 0, 0, gw, gh, this);
                    }
                } else {
                    gb.drawImage(backgroundImage, 0, 0, gw, gh, this);
                }
            }
            // else: leave buffer transparent so we can paint the full image directly to the panel
        } else {
            gb.setColor(Color.BLACK);
            gb.fillRect(0, 0, gw, gh);
        }

        switch (model.getGameState()) {
            case MAIN_MENU:
                mainMenuPanel.render(gb, model, gw, gh);
                break;
            case LEADERBOARD:
                // Render leaderboard as its own screen
                mainMenuPanel.renderLeaderboard(gb, model, gw, gh);
                break;
            case PLAYING:
                drawGame(gb, gw, gh);
                drawWaveStatus(gb, gw, gh);
                break;
            case PAUSED:
                drawGame(gb, gw, gh);
                // render game behind then overlay pause panel
                pausePanel.render(gb, model, gw, gh);
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
                // Panel is responsible for drawing its own faded overlay now.
                optionsPanel.render(gb, model, gw, gh);
                break;
        }

        gb.dispose();

        // paint black background (will be underlay for buffer)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, panelW, panelH);

        // If fullscreen and we're on the main menu, draw the full background image centered
        // Scale proportionally so the entire image is visible (no non-uniform stretching)
        if (backgroundImage != null && parent.isFullscreen() && (model.getGameState() == Data.GameState.MAIN_MENU || model.getGameState() == Data.GameState.LEADERBOARD)) {
            int srcW = backgroundImage.getWidth(null);
            int srcH = backgroundImage.getHeight(null);
            if (srcW > 0 && srcH > 0) {
                double scale = Math.min((double) panelW / (double) srcW, (double) panelH / (double) srcH);
                int drawW = (int) Math.round(srcW * scale);
                int drawH = (int) Math.round(srcH * scale);
                int drawX = (panelW - drawW) / 2;
                int drawY = (panelH - drawH) / 2;
                g2d.drawImage(backgroundImage, drawX, drawY, drawW, drawH, null);
            } else {
                // fallback: draw stretched if source size unavailable
                g2d.drawImage(backgroundImage, 0, 0, panelW, panelH, null);
            }
        }

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

        for (Projectile p : model.getProjectiles()) {
            p.render(g, model);
        }

        // Draw fireball effects
        for (GameObject.FireBallEffect f : model.getFireBallEffects()) {
            f.render(g);
        }

        List<Enemy> enemies = model.getEnemies();
        enemies.sort((e1, e2) -> Double.compare(e1.z, e2.z));

        for (Enemy enemy : enemies) {
            enemy.render(g, model);
        }

        Player player = model.getPlayer();
        int shakeX = model.getShakeOffsetX();
        if (player != null) {
            g.translate(shakeX, 0);
            player.render(g, model);
            g.translate(-shakeX, 0);
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

        hudRenderer.render(g, model, cooldownIconBounds, cooldownIconTooltips, gw, gh);

        // Debug: draw road center and left/right edges following the road path
        if (Config.GameConfig.DEBUG_ROAD_RENDER) {
            g.setStroke(new BasicStroke(2f));
            g.setColor(Color.CYAN);
            java.util.List<java.awt.Point> centers = new java.util.ArrayList<>();
            java.util.List<java.awt.Point> lefts = new java.util.ArrayList<>();
            java.util.List<java.awt.Point> rights = new java.util.ArrayList<>();
            int steps = 50;
            for (int i = 0; i <= steps; i++) {
                double t = i / (double) steps;
                java.awt.geom.Point2D.Double c = Config.PerspectiveConfig.ROAD_PATH.getPointForZ(t);
                java.awt.geom.Point2D.Double n = Config.PerspectiveConfig.ROAD_PATH.getNormalForZ(t);
                // Apply camera vertical offset so debug overlay matches rendered world
                c.y += Config.PerspectiveConfig.CAMERA_Y_OFFSET;
                double half = Config.PerspectiveConfig.getRoadHalfWidthForZ(t);
                int cx = (int) Math.round(c.x);
                int cy = (int) Math.round(c.y);
                int lx = (int) Math.round(c.x - n.x * half);
                int ly = (int) Math.round(c.y - n.y * half);
                int rx = (int) Math.round(c.x + n.x * half);
                int ry = (int) Math.round(c.y + n.y * half);
                centers.add(new java.awt.Point(cx, cy));
                lefts.add(new java.awt.Point(lx, ly));
                rights.add(new java.awt.Point(rx, ry));
            }

            // draw center polyline
            for (int i = 1; i < centers.size(); i++) {
                java.awt.Point a = centers.get(i-1);
                java.awt.Point b = centers.get(i);
                g.drawLine(a.x, a.y, b.x, b.y);
            }

            // draw edges
            g.setColor(Color.YELLOW);
            for (int i = 1; i < lefts.size(); i++) {
                java.awt.Point a = lefts.get(i-1);
                java.awt.Point b = lefts.get(i);
                g.drawLine(a.x, a.y, b.x, b.y);
            }
            for (int i = 1; i < rights.size(); i++) {
                java.awt.Point a = rights.get(i-1);
                java.awt.Point b = rights.get(i);
                g.drawLine(a.x, a.y, b.x, b.y);
            }

            // small markers at sample points
            g.setColor(new Color(255, 255, 255, 180));
            for (java.awt.Point p : centers) g.fillOval(p.x-2, p.y-2, 4, 4);
        }
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
