package bt.game.core.ctrl.spec.mouse.impl;

import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.ctrl.spec.mouse.MouseTarget;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class BaseMouseTarget implements MouseTarget
{
    protected Unit x;
    protected Unit y;
    protected Unit z;
    protected Unit w;
    protected Unit h;
    protected Shape shape;

    public BaseMouseTarget(Unit x, Unit y, Unit z, Unit w, Unit h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.z = z;
        this.shape = Geometry.createRectangle(w.units(), h.units());
        this.shape.translate(x.pixels(), y.pixels());
        MouseController.get().addMouseTarget(this);
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onRightClick()
     */
    @Override
    public void onRightClick()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onLeftClick()
     */
    @Override
    public void onLeftClick()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onDrag(bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void onDrag(Unit xOffset, Unit yOffset)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onHover()
     */
    @Override
    public void onHover()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#afterHover()
     */
    @Override
    public void afterHover()
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onMouseWheelMove(int)
     */
    @Override
    public void onMouseWheelMove(int clicks)
    {
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getShape()
     */
    @Override
    public Shape getShape()
    {
        return this.shape;
    }
}