package bt.game.core.obj.impl;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Rotation;
import org.dyn4j.geometry.Vector2;

import bt.game.core.obj.intf.GameObject;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

/**
 * Super class to all objects in a game that should be involved in physics.
 * 
 * @author &#8904
 */
public class GameBody extends Body implements GameObject
{
    /** The unit width of this object. */
    protected Unit w;

    /** The unit height of this object. */
    protected Unit h;

    /** The scene that this object is used in. */
    protected Scene scene;

    /**
     * Creates a new instance for the given scene.
     * 
     * @param scene
     */
    public GameBody(Scene scene)
    {
        this.scene = scene;
        translateToOrigin();
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getScene()
     */
    @Override
    public Scene getScene()
    {
        return this.scene;
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#setW(bt.game.util.unit.Unit)
     */
    @Override
    public void setW(Unit w)
    {
        this.w = w;
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#setH(bt.game.util.unit.Unit)
     */
    @Override
    public void setH(Unit h)
    {
        this.h = h;
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getH()
     */
    @Override
    public Unit getH()
    {
        return this.h;
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getW()
     */
    @Override
    public Unit getW()
    {
        return this.w;
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#setX(bt.game.util.unit.Unit)
     */
    @Override
    public void setX(Unit x)
    {
        x = x.subtractUnits(getX());
        translate(x.units(),
                  0);
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#setY(bt.game.util.unit.Unit)
     */
    @Override
    public void setY(Unit y)
    {
        y = y.subtractUnits(getY());
        translate(0, y.units());
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getX()
     */
    @Override
    public Unit getX()
    {
        return Unit.forUnits(this.transform.getTranslationX());
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getY()
     */
    @Override
    public Unit getY()
    {
        return Unit.forUnits(this.transform.getTranslationY());
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getCenterX()
     */
    @Override
    public Unit getCenterX()
    {
        return Unit.forUnits(this.getWorldCenter().x);
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#getCenterY()
     */
    @Override
    public Unit getCenterY()
    {
        return Unit.forUnits(this.getWorldCenter().y);
    }

    /**
     * Returns the x coordinate of the center relative to the body.
     * 
     * @return
     */
    public Unit getLocalCenterX()
    {
        return Unit.forPixels(this.getLocalCenter().x);
    }

    /**
     * Returns the y coordinate of the center relative to the body.
     * 
     * @return
     */
    public Unit getLocalCenterY()
    {
        return Unit.forPixels(this.getLocalCenter().y);
    }

    /**
     * Gets the current rotation of this body.
     * 
     * @return
     */
    public Rotation getRotation()
    {
        return this.transform.getRotation();
    }

    /**
     * Sets the velocity of this body.
     * 
     * <p>
     * This will wake the body up.
     * </p>
     * 
     * @param x
     *            x-axis Velocity in Units per second.
     * @param y
     *            y-axis Velocity in Units per second.
     */
    public void setVelocity(double x, double y)
    {
        getLinearVelocity().x = x * 10;
        getLinearVelocity().y = y * 10;
        setAtRest(false);
    }

    /**
     * Sets the velocity of this body.
     * 
     * <p>
     * This will wake the body up.
     * </p>
     * 
     * @param v
     *            Velocity in Units per second.
     */
    public void setVelocity(Vector2 v)
    {
        getLinearVelocity().x = v.x * 10;
        getLinearVelocity().y = v.y * 10;
        setAtRest(false);
    }

    /**
     * Sets the x velocity of this body.
     * 
     * <p>
     * This will wake the body up.
     * </p>
     * 
     * @param x
     *            x-axis Velocity in Units per second.
     */
    public void setVelocityX(double x)
    {
        getLinearVelocity().x = x * 10;
        setAtRest(false);
    }

    /**
     * Sets the y velocity of this body.
     * 
     * <p>
     * This will wake the body up.
     * </p>
     * 
     * @param y
     *            y-axis Velocity in Units per second.
     */
    public void setVelocityY(double y)
    {
        getLinearVelocity().y = y * 10;
        setAtRest(false);
    }

    /**
     * Indictaes whether this body is currently moving.
     * 
     * <p>
     * It counts as moving when its linear velocity on either its x or y axis is not equal to 0.
     * </p>
     * 
     * @return
     */
    public boolean isMoving()
    {
        return getLinearVelocity().x != 0 || getLinearVelocity().y != 0;
    }
}