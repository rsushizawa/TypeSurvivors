import javax.swing.SwingUtilities;

import Model.GameModel;

public class TypeSurvivors {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            int gameWidth = 600;
            int gameHeight = 800;

            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            GameController controller = new GameController(model, view);

            controller.startGame();
        });
    }
}
