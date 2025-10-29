import javax.swing.SwingUtilities;

public class TypeJam {

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
