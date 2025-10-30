import javax.swing.SwingUtilities;

import Model.GameModel;

public class TypeSurvivors {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            int gameWidth = 1900;
            int gameHeight = 1260;

            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            GameController controller = new GameController(model, view);

            controller.startGame();
        });
    }
}
