package bt.game.resource.render.impl;

import bt.game.resource.render.intf.Renderable;
import bt.game.util.unit.Unit;

public abstract class BaseRenderable implements Renderable
{
    protected Unit x;
    protected Unit y;
    protected Unit w;
    protected Unit h;
    protected Unit z;
    protected boolean shouldRender;

    public BaseRenderable()
    {
        this.x = Unit.zero();
        this.y = Unit.zero();
        this.w = Unit.forUnits(64);
        this.h = Unit.forUnits(64);
        this.z = Unit.zero();
        this.shouldRender = true;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#setZ(bt.game.util.unit.Unit)
     */
    @Override
    public void setZ(Unit z)
    {
        this.z = z;
    }

    @Override
    public Unit getX()
    {
        return this.x;
    }

    @Override
    public void setX(Unit x)
    {
        this.x = x;
    }

    @Override
    public Unit getY()
    {
        return this.y;
    }

    @Override
    public void setY(Unit y)
    {
        this.y = y;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#shouldRender()
     */
    @Override
    public boolean shouldRender()
    {
        return this.shouldRender;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#shouldRender(boolean)
     */
    @Override
    public void shouldRender(boolean shouldRender)
    {
        this.shouldRender = shouldRender;
    }

    @Override
    public Unit getW()
    {
        return this.w;
    }

    @Override
    public void setW(Unit w)
    {
        this.w = w;
    }

    @Override
    public Unit getH()
    {
        return this.h;
    }

    @Override
    public void setH(Unit h)
    {
        this.h = h;
    }
}
