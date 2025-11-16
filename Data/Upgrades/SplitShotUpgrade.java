package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;

public class SplitShotUpgrade extends Upgrade {

    public SplitShotUpgrade() {
        super(
            "Split Shot",
            UpgradeType.WEAPON,
            "Fire multiple small projectiles that remove letters from targets.",
            new Parameter("Pellets", 3.0, 1.0, ""), // number of pellets
            new Parameter("Spread", 45.0, -2.0, "deg"),
            new Parameter("Cooldown", 8.0, -0.5, "s")
        );
    }

    @Override
    public void apply(GameModel model, Enemy target) {
        if (target == null) return;

        int pellets = (int) getParam1Value();
        int removed = Math.max(1, pellets / 2);

        int len = target.text.length();
        if (len > removed) {
            target.text = target.text.substring(0, len - removed);
        } else {
            model.addScore(target.originalText.length());
            model.removeEnemy(target);
            model.resetTypingIfTarget(target);
        }
    }
}
