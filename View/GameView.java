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

	public GameView(GameModel model, int width, int height) {
		this.model = model;
        
		frame = new JFrame("Type Survivors");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(width, height);
		frame.setResizable(false);

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

	public void addGameKeyListener(KeyListener listener) {
		gamePanel.addKeyListener(listener);
	}

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