package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;

public class HealthRegenUpgrade extends Upgrade {

    public HealthRegenUpgrade() {
        super(
            "Health Regen",
            UpgradeType.UTILITY,
            "Gradually restore player health over time.",
            new Parameter("Regen Rate", 1.0, 0.5, "hp/s"),
            new Parameter("Duration", 5.0, 1.0, "s"),
            new Parameter("Cooldown", 20.0, -1.0, "s")
        );
    }

    @Override
    public void apply(GameModel model, Enemy target) {
    }
}
