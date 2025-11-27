package TypeSurvivors;

import javax.swing.SwingUtilities;
import Controller.GameController;
import Model.GameModel;
import View.GameView;
import Audio.AudioManager; // Added import

public class TypeSurvivors {

    public static int gameWidth = 1000;
    public static int gameHeight = 1108;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            AudioManager.init();

            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            GameController controller = new GameController(model, view);

            AudioManager.playMainMenuMusic();
            controller.startGame();
        });
    }
}