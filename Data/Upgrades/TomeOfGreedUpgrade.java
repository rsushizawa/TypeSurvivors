package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;

public class TomeOfGreedUpgrade extends Upgrade {

    public TomeOfGreedUpgrade() {
        super(
            "Tome of Greed",
            UpgradeType.TOMB,
            "Increase coins and score gained from kills.",
            new Parameter("Coin Bonus", 0.1, 0.05, "%"),
            new Parameter("Score Mult", 1.1, 0.1, "x"),
            new Parameter("Cooldown", 0.0, 0.0, "s")
        );
    }

    @Override
    public void apply(GameModel model, Enemy target) {
    }
}
