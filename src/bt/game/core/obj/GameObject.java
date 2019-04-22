package bt.game.core.obj;

import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Vector2;

import bt.game.core.obj.hand.ObjectHandler;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * An abstract base class for all objects that have a position in the game and need to be handled by an
 * {@link ObjectHandler}, i.e. characters in a scene.
 * 
 * @author &#8904
 */
public abstract class GameObject extends Body
{
    /** The unit Z position of this object. */
    protected Unit z;

    /** The unit width of this object. */
    protected Unit w;

    /** The unit height of this object. */
    protected Unit h;

    /** The scene that this object is used in. */
    protected Scene scene;

    /**
     * Creates a new instance and initializes its fields.
     * 
     * @param scene
     *            The scene that this object is created for.
     * @param x
     *            The X position.
     * @param y
     *            The Y position.
     * @param z
     *            The Z position.
     */
    public GameObject(Scene scene, Unit x, Unit y, Unit z)
    {
        this.scene = scene;
        this.z = z;
        translateToOrigin();
    }

    /**
     * Gets the scene that this object was created for.
     * 
     * @return
     */
    public Scene getScene()
    {
        return this.scene;
    }

    /**
     * Sets the Z position.
     * 
     * @param z
     */
    public void setZ(Unit z)
    {
        this.z = z;
    }

    /**
     * Sets the width of this object.
     * 
     * @param w
     */
    public void setW(Unit w)
    {
        this.w = w;
    }

    /**
     * Sets the height of this object.
     * 
     * @param h
     */
    public void setH(Unit h)
    {
        this.h = h;
    }

    public Unit getH()
    {
        return this.h;
    }

    public Unit getW()
    {
        return this.w;
    }

    public Unit getX()
    {
        return Unit.forUnits(this.transform.getTranslationX() * Unit.getRatio());
    }

    public Unit getY()
    {
        return Unit.forUnits(this.transform.getTranslationY() * Unit.getRatio());
    }

    public Unit getCenterX()
    {
        return Unit.forUnits(this.getWorldCenter().x * Unit.getRatio());
    }

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

    public Unit getZ()
    {
        return this.z;
    }
}