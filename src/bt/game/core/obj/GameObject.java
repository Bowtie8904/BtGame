package bt.game.core.obj;

import bt.game.core.obj.hand.ObjectHandler;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * An abstract base class for all objects that have a position in the game and need to be handled by an
 * {@link ObjectHandler}, i.e. characters in a scene.
 * 
 * @author &#8904
 */
public abstract class GameObject
{
    /** The unit X position of this object. */
    protected Unit x;

    /** The unit Y position of this object. */
    protected Unit y;

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
     * @param z
     *            The Z position.
     */
    public GameObject(Scene scene, Unit x, Unit y)
    {
        this.scene = scene;
        this.x = x;
        this.y = y;
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
        return this.x;
    }

    public Unit getY()
    {
        return this.y;
    }

    public void setX(Unit x)
    {
        this.x = x;
    }

    public void setY(Unit y)
    {
        this.y = y;
    }
}