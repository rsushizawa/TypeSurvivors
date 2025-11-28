package TypeSurvivors;

import javax.swing.SwingUtilities;
import Controller.GameController;
import Model.GameModel;
import View.GameView;
import Audio.AudioManager;
import Config.GameConfig;
import Config.PerspectiveConfig;
import Config.RoadPath;
import java.awt.geom.Point2D;
import Entity.Enemy.Enemy;

public class TypeSurvivors {

    public static int gameWidth = 1000;
    public static int gameHeight = 1150;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            AudioManager.init();
            PerspectiveConfig.setRoadPath(new RoadPath(
                new Point2D.Double(500, PerspectiveConfig.HORIZON_Y),
                new Point2D.Double(500, PerspectiveConfig.HORIZON_Y + (PerspectiveConfig.PLAYER_Y_LINE - PerspectiveConfig.HORIZON_Y) * 0.35),
                new Point2D.Double(500, PerspectiveConfig.HORIZON_Y + (PerspectiveConfig.PLAYER_Y_LINE - PerspectiveConfig.HORIZON_Y) * 0.7),
                new Point2D.Double(PerspectiveConfig.PLAYER_CENTER_X, PerspectiveConfig.PLAYER_Y_LINE)
            ));

            Enemy.refreshPerspectiveConstants();

            GameModel model = new GameModel(gameWidth, gameHeight);

            GameView view = new GameView(model, gameWidth, gameHeight);

            if (GameConfig.FULLSCREEN) {
                view.setFullscreen(true);
            }

            GameController controller = new GameController(model, view);

            AudioManager.playMainMenuMusic();
            controller.startGame();
        });
    }
}