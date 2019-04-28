package bt.game.core.obj;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class GameBody extends Body implements GameObject
{
    /** The unit width of this object. */
    protected Unit w;

    /** The unit height of this object. */
    protected Unit h;

    /** The scene that this object is used in. */
    protected Scene scene;

    public GameBody(Scene scene)
    {
        this.scene = scene;
        translateToOrigin();
    }

    /**
     * Gets the scene that this object was created for.
     * 
     * @return
     */
    @Override
    public Scene getScene()
    {
        return this.scene;
    }

    /**
     * Sets the width of this object.
     * 
     * @param w
     */
    @Override
    public void setW(Unit w)
    {
        this.w = w;
    }

    /**
     * Sets the height of this object.
     * 
     * @param h
     */
    @Override
    public void setH(Unit h)
    {
        this.h = h;
    }

    @Override
    public Unit getH()
    {
        return this.h;
    }

    @Override
    public Unit getW()
    {
        return this.w;
    }

    @Override
    public Unit getX()
    {
        return Unit.forUnits(this.transform.getTranslationX() * Unit.getRatio());
    }

    @Override
    public Unit getY()
    {
        return Unit.forUnits(this.transform.getTranslationY() * Unit.getRatio());
    }

    @Override
    public Unit getCenterX()
    {
        return Unit.forUnits(this.getWorldCenter().x * Unit.getRatio());
    }

    @Override
    public Unit getCenterY()
    {
        return Unit.forUnits(this.getWorldCenter().y * Unit.getRatio());
    }

    public Unit getLocalCenterX()
    {
        return Unit.forUnits(this.getLocalCenter().x * Unit.getRatio());
    }

    public Unit getLocalCenterY()
    {
        return Unit.forUnits(this.getLocalCenter().y * Unit.getRatio());
    }

    public double getRotation()
    {
        return this.transform.getRotation();
    }

    public void setVelocity(double x, double y)
    {
        setLinearVelocity(x, y);
        setAsleep(false);
    }

    public void setVelocity(Vector2 v)
    {
        setVelocity(v.x, v.y);
    }

    public void setVelocityX(double x)
    {
        setVelocity(x, getLinearVelocity().y);
    }

    public void setVelocityY(double y)
    {
        setVelocity(getLinearVelocity().x, y);
    }

    /**
     * @see bt.game.core.obj.GameObject#setX(bt.game.util.unit.Unit)
     */
    @Override
    public void setX(Unit x)
    {
        x = x.subtractUnits(getX());
        translate(x.units(), getY().units());
    }

    /**
     * @see bt.game.core.obj.GameObject#setY(bt.game.util.unit.Unit)
     */
    @Override
    public void setY(Unit y)
    {
        y = y.subtractUnits(getX());
        translate(getX().units(), y.units());
    }
}