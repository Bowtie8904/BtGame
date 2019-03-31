package bt.game.core.ctrl.spec.mouse.impl;

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
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#onClick()
     */
    @Override
    public void onClick()
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
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getX()
     */
    @Override
    public Unit getX()
    {
        return this.x;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getY()
     */
    @Override
    public Unit getY()
    {
        return this.y;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getWidth()
     */
    @Override
    public Unit getW()
    {
        return this.w;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getHeight()
     */
    @Override
    public Unit getH()
    {
        return this.h;
    }

    /**
     * @see bt.game.core.ctrl.spec.mouse.MouseTarget#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }
}