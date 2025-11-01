import javax.swing.SwingUtilities;

import Controller.GameController;
import Model.GameModel;
import View.GameView;

public class TypeSurvivors {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            int gameWidth = 600;
            int gameHeight = 900;

            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            GameController controller = new GameController(model, view);

            controller.startGame();
        });
    }
}
