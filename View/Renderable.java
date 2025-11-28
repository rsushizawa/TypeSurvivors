package View;

import java.awt.Graphics2D;
import Model.GameModel;

public interface Renderable {
    void render(Graphics2D g, GameModel model, int width, int height);
}
