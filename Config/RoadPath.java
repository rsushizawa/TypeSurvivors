package Config;

import java.awt.geom.Point2D;

public class RoadPath {
    private final Point2D.Double p0; // horizon
    private final Point2D.Double p1; // control 1
    private final Point2D.Double p2; // control 2
    private final Point2D.Double p3; // player line

    public RoadPath(Point2D.Double p0, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public Point2D.Double getP0() { return p0; }
    public Point2D.Double getP1() { return p1; }
    public Point2D.Double getP2() { return p2; }
    public Point2D.Double getP3() { return p3; }

    private double cubicBezier(double a, double b, double c, double d, double t) {
        double it = 1.0 - t;
        return (it*it*it)*a + 3*(it*it)*t*b + 3*it*(t*t)*c + (t*t*t)*d;
    }

    public Point2D.Double getPointForZ(double z) {
        double t = Math.max(0.0, Math.min(1.0, z));
        double x = cubicBezier(p0.x, p1.x, p2.x, p3.x, t);
        double y = cubicBezier(p0.y, p1.y, p2.y, p3.y, t);
        return new Point2D.Double(x, y);
    }

    public double getCenterXForZ(double z) {
        return getPointForZ(z).x;
    }
    
    /**
     * Returns the tangent vector (dx,dy) of the cubic Bezier at parameter t=z.
     * This is the first derivative of the cubic Bezier curve.
     */
    public Point2D.Double getTangentForZ(double z) {
        double t = Math.max(0.0, Math.min(1.0, z));
        double it = 1.0 - t;
        // derivative: 3(1-t)^2 (p1-p0) + 6(1-t)t (p2-p1) + 3 t^2 (p3-p2)
        double dx = 3*(it*it)*(p1.x - p0.x) + 6*(it*t)*(p2.x - p1.x) + 3*(t*t)*(p3.x - p2.x);
        double dy = 3*(it*it)*(p1.y - p0.y) + 6*(it*t)*(p2.y - p1.y) + 3*(t*t)*(p3.y - p2.y);
        return new Point2D.Double(dx, dy);
    }
    
    /**
     * Returns the unit normal vector (perpendicular) to the tangent at z.
     * The normal is ( -dy, dx ) normalized.
     */
    public Point2D.Double getNormalForZ(double z) {
        Point2D.Double tan = getTangentForZ(z);
        double nx = -tan.y;
        double ny = tan.x;
        double len = Math.hypot(nx, ny);
        if (len == 0.0) return new Point2D.Double(1.0, 0.0);
        return new Point2D.Double(nx/len, ny/len);
    }
}
