package GameObject;

import Entity.Enemy.Enemy;
import Model.GameModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PoisonWall {

    public final int x; 
    public final int y; 
    public final double height; 
    public final double width;

    private double remainingDuration;
    private final double lettersPerSecond;
    private final double slowFactor; 

    private final Map<Enemy, Double> accum = new HashMap<>();
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

    public void update(double deltaSeconds, List<Enemy> enemies, GameModel model) {
        remainingDuration -= deltaSeconds;

        List<Enemy> currentlyOverlapping = new ArrayList<>();

        int halfW = (int)(width / 2.0);
        int halfH = (int)(height / 2.0);

        int left = x - halfW;
        int right = x + halfW;
        int top = y - halfH;
        int bottom = y + halfH;

        for (Enemy e : new ArrayList<>(enemies)) {
            int eW = e.getScaledWidth();
            int eH = e.getScaledHeight();
            int eLeft = e.x - (eW / 2);
            int eRight = e.x + (eW / 2);
            int eTop = e.y - eH;
            int eBottom = e.y;

            boolean overlap = !(eRight < left || eLeft > right || eBottom < top || eTop > bottom);
            if (overlap) {
                currentlyOverlapping.add(e);

                if (!originalZSpeed.containsKey(e)) {
                    originalZSpeed.put(e, e.zSpeed);
                    e.zSpeed = e.zSpeed * (1.0 - slowFactor);
                }

                double acc = accum.getOrDefault(e, 0.0);
                acc += lettersPerSecond * deltaSeconds;

                while (acc >= 1.0) {
                    acc -= 1.0;
                    int len = e.text.length();
                    if (len > 1) {
                        e.text = e.text.substring(0, len - 1);
                    } else {
                        model.addScore(e.originalText.length());
                        model.removeEnemy(e);
                        model.resetTypingIfTarget(e);
                        accum.remove(e);
                        originalZSpeed.remove(e);
                        break;
                    }
                }

                accum.put(e, acc);
            }
        }

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
