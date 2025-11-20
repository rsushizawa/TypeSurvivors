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
    private int playerXP = 1000;
    private int xpToNextLevel = 100;

    public enum UpgradeId {
        FIRE_BALL,
        INSECT_SPRAY,
        SPLIT_SHOT,
        WALL,
        HEALTH_REGEN,
        HEALTH_UPGRADE,
        XP_TOME
    }

    private final Map<UpgradeId, Upgrade> allUpgrades = new HashMap<>();

    private final Map<UpgradeId, Upgrade> playerUpgrades = new HashMap<>();

    private final List<Upgrade> currentLevelUpOffer = new ArrayList<>();
    
    private Upgrade chosenWeapon = null;
    private Upgrade chosenUtil = null;
    private Upgrade chosenTomb = null;
    private boolean isPoolLocked = false;
    
    private final Random rand = new Random();

    public UpgradeManager() {
        initializeAllUpgrades();
    }

    public java.util.List<Upgrade> getPlayerUpgrades() {
        return new ArrayList<>(playerUpgrades.values());
    }

    private void initializeAllUpgrades() {
        // --- WEAPONS ---
        allUpgrades.put(UpgradeId.FIRE_BALL, new FireBallUpgrade());
        allUpgrades.put(UpgradeId.INSECT_SPRAY, new InsectSprayUpgrade());
        allUpgrades.put(UpgradeId.SPLIT_SHOT, new SplitShotUpgrade());

        // --- UTILITY ---
        allUpgrades.put(UpgradeId.WALL, new WallUpgrade());
        allUpgrades.put(UpgradeId.HEALTH_REGEN, new HealthRegenUpgrade());
        allUpgrades.put(UpgradeId.HEALTH_UPGRADE, new HealthUpgrade());

        // --- TOMB ---
        allUpgrades.put(UpgradeId.XP_TOME, new XpTomeUpgrade());
    }
    
    public void addXP(int amount) {
        if (isPoolLocked && playerLevel >= 30) return;
        
        playerXP += amount * 1.5;
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
        
        UpgradeId chosenId = idFor(chosen);
        if (chosenId != null && !playerUpgrades.containsKey(chosenId)) {
            playerUpgrades.put(chosenId, chosen);
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
                UpgradeId wId = idFor(chosenWeapon);
                UpgradeId uId = idFor(chosenUtil);
                UpgradeId tId = idFor(chosenTomb);
                if (wId != null) playerUpgrades.put(wId, chosenWeapon);
                if (uId != null) playerUpgrades.put(uId, chosenUtil);
                if (tId != null) playerUpgrades.put(tId, chosenTomb);
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
        UpgradeId id = nameToId(name);
        return id == null ? null : playerUpgrades.get(id);
    }
    
    public boolean hasUpgrade(String name) {
        UpgradeId id = nameToId(name);
        return id != null && playerUpgrades.containsKey(id);
    }

    // Enum-based accessors
    public Upgrade getUpgrade(UpgradeId id) {
        return playerUpgrades.get(id);
    }

    public boolean hasUpgrade(UpgradeId id) {
        return playerUpgrades.containsKey(id);
    }

    private UpgradeId nameToId(String name) {
        for (Map.Entry<UpgradeId, Upgrade> e : allUpgrades.entrySet()) {
            if (e.getValue().getName().equals(name)) return e.getKey();
        }
        return null;
    }

    public UpgradeId idFor(Upgrade u) {
        for (Map.Entry<UpgradeId, Upgrade> e : allUpgrades.entrySet()) {
            if (e.getValue().getName().equals(u.getName())) return e.getKey();
        }
        return null;
    }
}