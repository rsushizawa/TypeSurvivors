package Config;

import java.awt.geom.Point2D;

/**
 * Global perspective configuration. Holds tunables for focal point, scaling and road path.
 */
public final class PerspectiveConfig {
    private PerspectiveConfig() {}

    public static double MIN_SCALE = 0.1;
    public static double MAX_SCALE = 2;
    public static double HORIZON_Y = 100.0;
    public static double PLAYER_Y_LINE = 1000.0;

    public static double PLAYER_CENTER_X = 500.0;

    public static RoadPath ROAD_PATH = new RoadPath(
        new Point2D.Double(PLAYER_CENTER_X, HORIZON_Y),
        new Point2D.Double(PLAYER_CENTER_X, HORIZON_Y + (PLAYER_Y_LINE - HORIZON_Y) * 0.33),
        new Point2D.Double(PLAYER_CENTER_X, HORIZON_Y + (PLAYER_Y_LINE - HORIZON_Y) * 0.66),
        new Point2D.Double(PLAYER_CENTER_X, PLAYER_Y_LINE)
    );

    public static double ROAD_HALF_WIDTH_HORIZON = 10.0;
    public static double ROAD_HALF_WIDTH_PLAYER = 500.0;

    public static double getRoadHalfWidthForZ(double z) {
        double t = Math.max(0.0, Math.min(1.0, z));
        return ROAD_HALF_WIDTH_HORIZON + t * (ROAD_HALF_WIDTH_PLAYER - ROAD_HALF_WIDTH_HORIZON);
    }

    public static void setRoadPath(RoadPath rp) {
        if (rp != null) ROAD_PATH = rp;
    }

    public static void setPlayerCenterX(double x) {
        PLAYER_CENTER_X = x;
    }
    public static double CAMERA_Y_OFFSET = 0.0;

    public static double[] getWorldXBoundsForZ(double z) {
        double scale = MIN_SCALE + z * (MAX_SCALE - MIN_SCALE);
        double half = getRoadHalfWidthForZ(z);
        double minWorldX = PLAYER_CENTER_X - (half / scale);
        double maxWorldX = PLAYER_CENTER_X + (half / scale);
        if (minWorldX > maxWorldX) {
            double tmp = minWorldX; minWorldX = maxWorldX; maxWorldX = tmp;
        }
        return new double[] { minWorldX, maxWorldX };
    }
}
