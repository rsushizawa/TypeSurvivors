package Manager;

import Data.Upgrades.*;
import Data.UpgradeType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class UpgradeManager {

    private int playerLevel = 1;
    private int playerXP = 0;
    private int xpToNextLevel = 100; // Example starting value

    private final Map<String, Upgrade> allUpgrades = new HashMap<>();
    
    private final Map<String, Upgrade> playerUpgrades = new HashMap<>();

    private final List<Upgrade> currentLevelUpOffer = new ArrayList<>();
    
    private Upgrade chosenWeapon = null;
    private Upgrade chosenUtil = null;
    private Upgrade chosenTomb = null;
    private boolean isPoolLocked = false;
    
    private final Random rand = new Random();

    public UpgradeManager() {
        initializeAllUpgrades();
    }

    private void initializeAllUpgrades() {
        // --- WEAPONS ---
        allUpgrades.put("Fire Ball", new FireBallUpgrade());
        allUpgrades.put("Insect Spray", new InsectSprayUpgrade());
        allUpgrades.put("Split Shot", new SplitShotUpgrade());

        // --- UTILITY ---
        allUpgrades.put("Wall", new WallUpgrade());
        allUpgrades.put("Health Regen", new HealthRegenUpgrade());
        allUpgrades.put("Health Upgrade", new HealthUpgrade());

        // --- TOMB ---
        allUpgrades.put("Tome of Greed", new TomeOfGreedUpgrade());
    }
    
    public void addXP(int amount) {
        if (isPoolLocked && playerLevel >= 30) return;
        
        playerXP += amount;
        if (playerXP >= xpToNextLevel) {
            levelUp();
        }
    }
    
    private void levelUp() {
        playerLevel++;
        playerXP -= xpToNextLevel;
        xpToNextLevel = (int) (xpToNextLevel * 1.5);
        
        generateLevelUpOffer();
    }

    private void generateLevelUpOffer() {
        currentLevelUpOffer.clear();

        if (isPoolLocked) {
            currentLevelUpOffer.add(chosenWeapon);
            currentLevelUpOffer.add(chosenUtil);
            currentLevelUpOffer.add(chosenTomb);
        } else {
            List<Upgrade> available = new ArrayList<>();
            
            if (chosenWeapon == null) {
                available.addAll(getUpgradesOfType(UpgradeType.WEAPON, 0));
            }
            if (chosenUtil == null) {
                available.addAll(getUpgradesOfType(UpgradeType.UTILITY, 0));
            }
            if (chosenTomb == null) {
                available.addAll(getUpgradesOfType(UpgradeType.TOMB, 0));
            }
            
            available.addAll(playerUpgrades.values());

            Collections.shuffle(available);
            currentLevelUpOffer.addAll(available.stream().distinct().limit(3).collect(Collectors.toList()));
            
            if (currentLevelUpOffer.size() < 3) {
                List<Upgrade> allUnlevelled = allUpgrades.values().stream()
                    .filter(u -> u.level == 0 && !currentLevelUpOffer.contains(u))
                    .collect(Collectors.toList());
                Collections.shuffle(allUnlevelled);
                currentLevelUpOffer.addAll(allUnlevelled.stream().limit(3 - currentLevelUpOffer.size()).collect(Collectors.toList()));
            }
        }
    }
    
    public void selectUpgrade(int choiceIndex) {
        if (choiceIndex < 0 || choiceIndex >= currentLevelUpOffer.size()) {
            return;
        }
        
        Upgrade chosen = currentLevelUpOffer.get(choiceIndex);
        chosen.levelUp();
        
        if (!playerUpgrades.containsKey(chosen.name)) {
            playerUpgrades.put(chosen.name, chosen);
        }

        // Check for locking
        if (!isPoolLocked) {
            if (chosen.type == UpgradeType.WEAPON && chosenWeapon == null) {
                chosenWeapon = chosen;
            }
            if (chosen.type == UpgradeType.UTILITY && chosenUtil == null) {
                chosenUtil = chosen;
            }
            if (chosen.type == UpgradeType.TOMB && chosenTomb == null) {
                chosenTomb = chosen;
            }
            
            if (chosenWeapon != null && chosenUtil != null && chosenTomb != null) {
                isPoolLocked = true;
                playerUpgrades.clear();
                playerUpgrades.put(chosenWeapon.name, chosenWeapon);
                playerUpgrades.put(chosenUtil.name, chosenUtil);
                playerUpgrades.put(chosenTomb.name, chosenTomb);
            }
        }
    }
    
    private List<Upgrade> getUpgradesOfType(UpgradeType type, int level) {
        return allUpgrades.values().stream()
            .filter(u -> u.type == type && u.level == level)
            .collect(Collectors.toList());
    }

    public List<Upgrade> getCurrentLevelUpOffer() {
        return currentLevelUpOffer;
    }

    public int getPlayerLevel() { return playerLevel; }
    public int getPlayerXP() { return playerXP; }
    public int getXPToNextLevel() { return xpToNextLevel; }
    
    public Upgrade getUpgrade(String name) {
        return playerUpgrades.get(name);
    }
    
    public boolean hasUpgrade(String name) {
        return playerUpgrades.containsKey(name);
    }
}