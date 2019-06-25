package bt.game.resource.render.impl;

import java.awt.Graphics2D;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public abstract class AdvancedRenderable implements Renderable
{
    protected Unit x;
    protected Unit y;
    protected Unit w;
    protected Unit h;
    protected Unit z;
    protected boolean shouldRender;

    public AdvancedRenderable()
    {
        this.x = Unit.zero();
        this.y = Unit.zero();
        this.w = Unit.forUnits(64);
        this.h = Unit.forUnits(64);
        this.z = Unit.zero();
        this.shouldRender = true;
    }

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics2D)
     */
    @Override
    public void render(Graphics2D g)
    {
        render(g, this.x, this.y, this.w, this.h);
    }

    /**
     * @see bt.game.resource.render.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    /**
     * @see bt.game.resource.render.Renderable#setZ(bt.game.util.unit.Unit)
     */
    @Override
    public void setZ(Unit z)
    {
        this.z = z;
    }

    public Unit getX()
    {
        return this.x;
    }

    public void setX(Unit x)
    {
        this.x = x;
    }

    public Unit getY()
    {
        return this.y;
    }

    public void setY(Unit y)
    {
        this.y = y;
    }

    /**
     * @see bt.game.resource.render.Renderable#shouldRender()
     */
    @Override
    public boolean shouldRender()
    {
        return this.shouldRender;
    }

    /**
     * @see bt.game.resource.render.Renderable#shouldRender(boolean)
     */
    @Override
    public void shouldRender(boolean shouldRender)
    {
        this.shouldRender = shouldRender;
    }

    public Unit getW()
    {
        return this.w;
    }

    public void setW(Unit w)
    {
        this.w = w;
    }

    public Unit getH()
    {
        return this.h;
    }

    public void setH(Unit h)
    {
        this.h = h;
    }
}