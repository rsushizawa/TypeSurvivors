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
    public void keyTyped(KeyEvent e) {
        if (model.isGameOver()) return;

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
        
        if (!model.isGameOver()) {
            model.updateGameState();
        }
        
        view.repaint();
    }
}
