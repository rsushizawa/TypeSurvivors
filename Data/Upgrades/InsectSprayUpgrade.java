package Data.Upgrades;

import Data.Parameter;
import Data.UpgradeType;
import Model.GameModel;
import Entity.Enemy.Enemy;
import Config.UpgradeConfig;

public class InsectSprayUpgrade extends Upgrade {
    public InsectSprayUpgrade() {
        super("Insect Spray", UpgradeType.WEAPON, "Poison wall slows & damages enemies.",
            new Parameter("Height", 5.0, 2.0, "px"),
            new Parameter("Letters/sec", 1.0, 0.5, ""),
            new Parameter("Cooldown", 15.0, -1.0, "s"));
        this.icon = "I";
        this.setIconPath("Assets/Icons/Gasmask.png");
    }

    @Override
    public void apply(GameModel model, Enemy target) {
        if (target == null) return;
        double height = getParam1Value();
        double lettersPerSec = getParam2Value();

        double width = UpgradeConfig.INSECT_SPRAY_BASE_WIDTH; // px
        double duration = UpgradeConfig.INSECT_SPRAY_BASE_DURATION; // seconds
        double slowFactor = UpgradeConfig.INSECT_SPRAY_BASE_SLOW; // 50% speed reduction while inside

        model.createPoisonWall(target.x, target.y, height, width, duration, lettersPerSec, slowFactor);
    }
}