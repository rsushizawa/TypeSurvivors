package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

import Model.GameModel;

public class GameView {

	private final JFrame frame;
	private final GamePanel gamePanel;
	private final GameModel model;

	private Image backgroundImage;
	private final int gameWidth;
	private final int gameHeight;
	private boolean fullscreen = Config.GameConfig.FULLSCREEN;

	public GameView(GameModel model, int width, int height) {
		this.model = model;
        this.gameWidth = width;
        this.gameHeight = height;
        
		frame = new JFrame("Type Survivors");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setResizable(true);

		gamePanel = new GamePanel(this, model);
		frame.add(gamePanel);

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		gamePanel.setFocusable(true);
		SwingUtilities.invokeLater(() -> gamePanel.requestFocusInWindow());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowActivated(java.awt.event.WindowEvent e) {
				gamePanel.requestFocusInWindow();
			}
		});
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fs) {
		if (this.fullscreen == fs) return;
		this.fullscreen = fs;
		Config.GameConfig.FULLSCREEN = fs;

		frame.dispose();
		frame.setUndecorated(fs);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if (fs) {
			gd.setFullScreenWindow(frame);
			DisplayMode dm = gd.getDisplayMode();
			int newW = dm.getWidth();
			int newH = dm.getHeight();
			gamePanel.setTargetResolution(newW, newH);
		} else {
			gd.setFullScreenWindow(null);
			frame.setSize(gameWidth, gameHeight);
			frame.setLocationRelativeTo(null);
			gamePanel.setTargetResolution(gameWidth, gameHeight);
		}
		frame.setVisible(true);
		gamePanel.requestFocusInWindow();
	}

	public void addGameKeyListener(KeyListener listener) {
		gamePanel.addKeyListener(listener);
	}

	public int getMainMenuSelection() { return gamePanel.getMainMenuSelection(); }
	public void setMainMenuSelection(int idx) { gamePanel.setMainMenuSelection(idx); }
	public void activateMainMenuSelection() { gamePanel.activateMainMenuSelection(); }

	public int getOptionsSelection() { return gamePanel.getOptionsSelection(); }
	public void setOptionsSelection(int idx) { gamePanel.setOptionsSelection(idx); }

	// Pause menu helpers
	public int getPauseSelection() { return gamePanel.getPauseSelection(); }
	public void setPauseSelection(int idx) { gamePanel.setPauseSelection(idx); }
	public void activatePauseSelection() { gamePanel.activatePauseSelection(-1); }

	public void repaint() {
		gamePanel.repaint();
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image img) {
		this.backgroundImage = img;
	}
}