package bt.game.core.ctrl.spec.mouse.impl;

import org.dyn4j.geometry.Shape;

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

    public BaseMouseTarget(Unit x, Unit y, Unit z, Unit w, Unit h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
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
        return null;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getShape()
     */
    @Override
    public Shape getShape()
    {
        return null;
    }
}