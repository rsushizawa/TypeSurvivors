import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import Model.*;

public class GameController extends KeyAdapter implements ActionListener {

    private final GameModel model;
    private final GameView view;
    private final Timer gameLoop;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;

        this.view.addGameKeyListener(this);

        this.gameLoop = new Timer(GameModel.GAME_SPEED_MS, this);
    }

    public void startGame() {
        gameLoop.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        // Handle ESC key for pausing
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (model.getGameState() == GameState.PLAYING || 
                model.getGameState() == GameState.PAUSED) {
                model.togglePause();
                view.repaint();
            }
            return;
        }

        // Handle ENTER key for menu
        if (keyCode == KeyEvent.VK_ENTER) {
            if (model.getGameState() == GameState.MAIN_MENU) {
                model.startNewGame();
                view.repaint();
            } else if (model.getGameState() == GameState.GAME_OVER) {
                model.returnToMenu();
                view.repaint();
            }
            return;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (model.getGameState() != GameState.PLAYING) return;

        char c = e.getKeyChar();

        if (c == KeyEvent.VK_BACK_SPACE) {
            model.backspaceTypedWord();
        } else if (Character.isLetter(c)) {
            model.appendTypedCharacter(c);
        }
        
        view.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (model.getGameState() == GameState.PLAYING) {
            model.updateGameState();
        }
        
        view.repaint();
    }
}