package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;
import java.util.ArrayList;

public class FireBallUpgrade extends Upgrade {

    public FireBallUpgrade() {
        super(
            "Fire Ball", 
            UpgradeType.WEAPON, 
            "Destroy target & remove letters from nearby enemies.",
            new Parameter("Radius", 100.0, 5.0, "px"),       // Param 1: Radius
            new Parameter("Letters Removed", 1.0, 1.0, ""), // Param 2: Letters
            new Parameter("Cooldown", 10.0, -0.75, "s")     // Param 3: Cooldown (negative increase)
        );
        this.icon = "F";
        this.setIconPath("Assets/Icons/Firestaff.png");
    }


    @Override
    public void apply(GameModel model, Enemy target) {
        if (target == null) return;

        double radius = getParam1Value();
        int lettersToRemove = (int) getParam2Value();

        for (Enemy e : new ArrayList<>(model.getEnemies())) {
            if (e == target) continue;

            double distance = Math.hypot(e.x - target.x, e.y - target.y);

            if (distance <= radius) {
                int len = e.text.length();
                if (len > lettersToRemove) {
                    e.text = e.text.substring(0, len - lettersToRemove);
                } else {
                    // Kill the enemy
                    model.addScore(e.originalText.length());
                    model.removeEnemy(e);
                    model.resetTypingIfTarget(e);
                }
            }
        }
        int fx = target.x;
        int fy = target.y;
    double visualRadius = Math.min(radius, Config.UpgradeConfig.FIREBALL_MAX_RADIUS);
    model.createFireBallEffect(fx, fy, visualRadius, Config.UpgradeConfig.FIREBALL_VISUAL_DURATION);
        Audio.AudioManager.playFireballSfx();
    }
}