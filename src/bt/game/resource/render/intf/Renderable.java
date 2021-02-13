package bt.game.resource.render.intf;

import bt.game.util.unit.Unit;

import java.awt.*;

/**
 * Defines a class which can be rendered.
 *
 * @author &#8904
 */
public interface Renderable
{
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering);

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        render(1, g, x, y, w, h, rotation, rotationOffsetX, rotationOffsetY, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        render(alpha, g, x, y, w, h, rotation, rotationOffsetX, rotationOffsetY, false);
    }

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        render(1, g, x, y, w, h, rotation, rotationOffsetX, rotationOffsetY);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, boolean debugRendering)
    {
        render(alpha, g, x, y, w, h, rotation, Unit.zero(), Unit.zero(), debugRendering);
    }

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, boolean debugRendering)
    {
        render(1, g, x, y, w, h, rotation, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation)
    {
        render(alpha, g, x, y, w, h, rotation, false);
    }

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation)
    {
        render(1, g, x, y, w, h, rotation);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        render(alpha, g, x, y, w, h, 0, debugRendering);
    }

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        render(1, g, x, y, w, h, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        render(alpha, g, x, y, w, h, false);
    }

    public default void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        render(1, g, x, y, w, h);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        render(alpha, g, x, y, getW(), getH(), rotation, Unit.zero(), Unit.zero(), debugRendering);
    }

    public default void render(Graphics2D g, Unit x, Unit y, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        render(1, g, x, y, rotation, rotationOffsetX, rotationOffsetY, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        render(alpha, g, x, y, rotation, Unit.zero(), Unit.zero(), false);
    }

    public default void render(Graphics2D g, Unit x, Unit y, double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        render(1, g, x, y, rotation, Unit.zero(), Unit.zero());
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, double rotation, boolean debugRendering)
    {
        render(alpha, g, x, y, rotation, Unit.zero(), Unit.zero(), debugRendering);
    }

    public default void render(Graphics2D g, Unit x, Unit y, double rotation, boolean debugRendering)
    {
        render(1, g, x, y, rotation, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, double rotation)
    {
        render(alpha, g, x, y, rotation, false);
    }

    public default void render(Graphics2D g, Unit x, Unit y, double rotation)
    {
        render(1, g, x, y, rotation);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y, boolean debugRendering)
    {
        render(alpha, g, x, y, 0, debugRendering);
    }

    public default void render(Graphics2D g, Unit x, Unit y, boolean debugRendering)
    {
        render(1, g, x, y, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, Unit x, Unit y)
    {
        render(alpha, g, x, y, false);
    }

    public default void render(Graphics2D g, Unit x, Unit y)
    {
        render(g, x, y, false);
    }

    public default void render(float alpha, Graphics2D g, double rotation, boolean debugRendering)
    {
        render(alpha, g, getX(), getY(), rotation, debugRendering);
    }

    public default void render(Graphics2D g, double rotation, boolean debugRendering)
    {
        render(1, g, rotation, debugRendering);
    }

    public default void render(float alpha, Graphics2D g, double rotation)
    {
        render(alpha, g, rotation, false);
    }

    public default void render(Graphics2D g, double rotation)
    {
        render(1, g, rotation);
    }

    public default void render(float alpha, Graphics2D g, boolean debugRendering)
    {
        render(alpha, g, 0, debugRendering);
    }

    public default void render(Graphics2D g, boolean debugRendering)
    {
        render(g, 0, debugRendering);
    }

    public default void render(float alpha, Graphics2D g)
    {
        render(alpha, g, false);
    }

    public default void render(Graphics2D g)
    {
        render(1, g);
    }

    /**
     * Gets the Z position of this object. This should be used to render the objects with the lowest Z value first.
     *
     * @return
     */
    public Unit getZ();

    /**
     * Sets the Z position of this object. This should be used to render the objects with the lowest Z value first.
     */
    public void setZ(Unit z);

    public Unit getX();

    public void setX(Unit x);

    public Unit getY();

    public void setY(Unit y);

    public Unit getW();

    public void setW(Unit w);

    public Unit getH();

    public void setH(Unit h);

    /**
     * A hint to the object handler whether this object should be rendered.
     *
     * @return true if it should be rendered, false otherwise.
     */
    public boolean shouldRender();

    /**
     * Sets a flag to indicate whether this object should be rendered by the object handler.
     *
     * @param shouldRender
     */
    public void shouldRender(boolean shouldRender);
}