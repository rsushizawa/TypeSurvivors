package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Config.UpgradeConfig;

public class WallUpgrade extends Upgrade {
    public WallUpgrade() {
        super("Wall", UpgradeType.UTILITY, "Creates a temporary barrier.",
            new Parameter("Duration", UpgradeConfig.WALL_BASE_DURATION, UpgradeConfig.WALL_DURATION_PER_LEVEL, "s"),
            new Parameter("Cooldown", UpgradeConfig.WALL_BASE_COOLDOWN, UpgradeConfig.WALL_COOLDOWN_PER_LEVEL, "s"),
            new Parameter("Distance", UpgradeConfig.WALL_BASE_DISTANCE, UpgradeConfig.WALL_DISTANCE_PER_LEVEL, "px"));
    }
}