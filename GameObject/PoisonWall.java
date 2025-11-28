package GameObject;

import Entity.Enemy.Enemy;
import Model.GameModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoisonWall {

    public final int x; // center x position
    public final int y; // center y position
    public final double height; // px
    public final double width; // px (horizontal width of the wall)

    private double remainingDuration; // seconds
    private final double lettersPerSecond;
    private final double slowFactor; // fraction to reduce zSpeed by (0.0-1.0)

    // Track fractional progress per enemy for letters removal
    private final Map<Enemy, Double> accum = new HashMap<>();
    // Track original zSpeed so we can restore it when enemy leaves
    private final Map<Enemy, Double> originalZSpeed = new HashMap<>();

    public PoisonWall(int x, int y, double height, double width, double durationSeconds, double lettersPerSecond, double slowFactor) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.remainingDuration = durationSeconds;
        this.lettersPerSecond = lettersPerSecond;
        this.slowFactor = slowFactor;
    }

    public boolean isExpired() {
        return remainingDuration <= 0.0;
    }

    /** Update the wall: apply slow and letter damage to overlapping enemies. */
    public void update(double deltaSeconds, List<Enemy> enemies, GameModel model) {
        remainingDuration -= deltaSeconds;

        List<Enemy> currentlyOverlapping = new ArrayList<>();

        // Simple AABB overlap check (centered rectangle)
        int halfW = (int)(width / 2.0);
        int halfH = (int)(height / 2.0);

        int left = x - halfW;
        int right = x + halfW;
        int top = y - halfH;
        int bottom = y + halfH;

        for (Enemy e : new ArrayList<>(enemies)) {
            // Use enemy scaled size for collision
            int eW = e.getScaledWidth();
            int eH = e.getScaledHeight();
            int eLeft = e.x - (eW / 2);
            int eRight = e.x + (eW / 2);
            int eTop = e.y - eH;
            int eBottom = e.y;

            boolean overlap = !(eRight < left || eLeft > right || eBottom < top || eTop > bottom);
            if (overlap) {
                currentlyOverlapping.add(e);

                // Apply slow: store original zSpeed if first time
                if (!originalZSpeed.containsKey(e)) {
                    originalZSpeed.put(e, e.zSpeed);
                    e.zSpeed = e.zSpeed * (1.0 - slowFactor);
                }

                // Letters removal over time
                double acc = accum.getOrDefault(e, 0.0);
                acc += lettersPerSecond * deltaSeconds;

                while (acc >= 1.0) {
                    acc -= 1.0;
                    // remove one letter from enemy
                    int len = e.text.length();
                    if (len > 1) {
                        e.text = e.text.substring(0, len - 1);
                    } else {
                        // kill enemy
                        model.addScore(e.originalText.length());
                        model.removeEnemy(e);
                        model.resetTypingIfTarget(e);
                        // cleanup maps
                        accum.remove(e);
                        originalZSpeed.remove(e);
                        break; // enemy removed, stop processing this enemy
                    }
                }

                accum.put(e, acc);
            }
        }

        // For enemies that previously were slowed but are no longer overlapping, restore speed
        List<Enemy> toRestore = new ArrayList<>();
        for (Enemy e : new ArrayList<>(originalZSpeed.keySet())) {
            if (!currentlyOverlapping.contains(e)) {
                toRestore.add(e);
            }
        }
        for (Enemy e : toRestore) {
            Double orig = originalZSpeed.remove(e);
            if (orig != null) {
                e.zSpeed = orig;
            }
            accum.remove(e);
        }

        // When wall expires, restore any remaining slowed enemies
        if (isExpired()) {
            for (Map.Entry<Enemy, Double> entry : originalZSpeed.entrySet()) {
                Enemy e = entry.getKey();
                Double orig = entry.getValue();
                if (orig != null) e.zSpeed = orig;
            }
            originalZSpeed.clear();
            accum.clear();
        }
    }
}
