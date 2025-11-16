package Data.Upgrades; // You'll want a new package for all the upgrade classes

import Data.UpgradeType;
import Data.Parameter;
import java.util.Random;

public abstract class Upgrade {
    
    public int level;
    public final String name;
    public final UpgradeType type;
    public final String description;
    
    protected Parameter param1;
    protected Parameter param2;
    protected Parameter param3;
    
    protected static final Random rand = new Random();

    public Upgrade(String name, UpgradeType type, String description, 
                   Parameter param1, Parameter param2, Parameter param3) {
        this.level = 0;
        this.name = name;
        this.type = type;
        this.description = description;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }

    public void levelUp() {
        this.level++;
        
        param1.justSkipped = false;
        param2.justSkipped = false;
        param3.justSkipped = false;

        int paramToSkip = rand.nextInt(3); 

        if (paramToSkip != 0) {
            param1.increase();
        } else {
            param1.justSkipped = true;
        }
        
        if (paramToSkip != 1) {
            param2.increase();
        } else {
            param2.justSkipped = true;
        }
        
        if (paramToSkip != 2) {
            param3.increase();
        } else {
            param3.justSkipped = true;
        }
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
    
    public UpgradeType getType() {
        return type;
    }
    
    public Parameter[] getParameters() {
        return new Parameter[] { param1, param2, param3 };
    }
    
    public double getParam1Value() { return param1.currentValue; }
    public double getParam2Value() { return param2.currentValue; }
    public double getParam3Value() { return param3.currentValue; }

    public void apply(Model.GameModel model, Entity.Enemy.Enemy target) {
        // Default: do nothing
    }
}