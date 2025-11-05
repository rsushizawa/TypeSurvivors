import javax.swing.SwingUtilities;

import Controller.GameController;
import Model.GameModel;
import View.GameView;

public class TypeSurvivors {

    public static int gameWidth = 600;
    public static int gameHeight = 900;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            GameController controller = new GameController(model, view);

            controller.startGame();
        });
    }
}
