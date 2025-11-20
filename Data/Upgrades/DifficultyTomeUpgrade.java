package Data.Upgrades;


import Data.Parameter;
import Data.UpgradeType;
import Entity.Enemy.Enemy;
import Model.GameModel;

public class DifficultyTomeUpgrade extends Upgrade {

    public DifficultyTomeUpgrade() {
        super(
            "Tome of Difficulty",
            UpgradeType.TOMB,
            "Increase XP and score gained from kills.",
            new Parameter("Dificulty Increase",0.1, 0.05, "%"),
            new Parameter("Bonus Score", 0.0, 0.0, ""),
            new Parameter("Cooldown", 0.0, 0.0, "s")
        );
        this.icon = "T";
        this.setIconPath("Assets/Icons/tome.png");
    }

    @Override
    public void apply(GameModel model, Enemy target) {
        if (target != null && target.originalText != null) {
            int base = target.originalText.length();
            int bonus = (int) Math.round(base * getParam1Value());
            if (bonus > 0) model.addScore(bonus);
        }
    }
}