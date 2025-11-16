package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;

public class HealthUpgrade extends Upgrade {

    public HealthUpgrade() {
        super(
            "Health Upgrade",
            UpgradeType.UTILITY,
            "Increase maximum player health.",
            new Parameter("Max HP +", 1.0, 1.0, "hp"),
            new Parameter("Bonus Shield", 0.0, 0.0, ""),
            new Parameter("Cooldown", 0.0, 0.0, "s")
        );
    }

    @Override
    public void apply(GameModel model, Enemy target) {
    }
}
