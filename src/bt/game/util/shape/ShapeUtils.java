package bt.game.util.shape;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 * @author &#8904
 *
 */
public final class ShapeUtils
{
    public static Shape getRotatedShape(Shape shape, double rotation, float anchorX, float anchorY)
    {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(rotation), anchorX, anchorY);
        return transform.createTransformedShape(shape);
    }

    public static Area getRotatedArea(Area area, double rotation, float anchorX, float anchorY)
    {
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(rotation), anchorX, anchorY);
        return area.createTransformedArea(transform);
    }
}