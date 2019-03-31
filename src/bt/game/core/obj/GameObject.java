package bt.game.core.obj;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.File;
import java.util.Map;

import bt.game.core.obj.hand.GameObjectHandler;
import bt.game.core.scene.Scene;
import bt.game.resource.load.Loadable;
import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;
import bt.types.sound.SoundSupplier;

/**
 * An abstract base class for all objects that need to be handled by a {@link GameObjectHandler}, i.e. characters in a
 * scene.
 * 
 * @author &#8904
 */
public abstract class GameObject implements Loadable
{
    /** The unit X position of this object. */
    protected Unit x;

    /** The unit Y position of this object. */
    protected Unit y;

    /** The unit Z position of this object. */
    protected Unit z;

    /** The unit width of this object. */
    protected Unit w;

    /** The unit height of this object. */
    protected Unit h;

    /** The unit velocity on the X axis. */
    protected Unit xVel;

    /** The unit velocity on the Y axis. */
    protected Unit yVel;

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
        this.x = x;
        this.y = y;
        this.z = z;
        this.xVel = Unit.forUnits(0);
        this.yVel = Unit.forUnits(0);
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
     * Sets the X position.
     * 
     * @param x
     */
    public void setX(Unit x)
    {
        this.x = x;
    }

    /**
     * Sets the Y position.
     * 
     * @param y
     */
    public void setY(Unit y)
    {
        this.y = y;
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
        return this.x;
    }

    public Unit getY()
    {
        return this.y;
    }

    public Unit getZ()
    {
        return this.z;
    }

    public void setXVelocity(Unit xVel)
    {
        this.xVel = xVel;
    }

    public void setYVelocity(Unit yVel)
    {
        this.yVel = yVel;
    }

    public Unit getXVelocity()
    {
        return this.xVel;
    }

    public Unit getYVelocity()
    {
        return this.yVel;
    }

    public Area getBounds()
    {
        return new Area(
                new Rectangle((int)this.x.pixels(),
                        (int)this.y.pixels(),
                        (int)this.w.pixels(),
                        (int)this.h.pixels()));
    }

    /**
     * @see bt.game.resource.load.Loadable#loadRenderables(java.lang.String)
     */
    @Override
    public Map<String, Renderable> loadRenderables(String name)
    {
        return null;
    }

    /**
     * @see bt.game.resource.load.Loadable#loadSounds(java.lang.String)
     */
    @Override
    public Map<String, SoundSupplier> loadSounds(String name)
    {
        return null;
    }

    /**
     * @see bt.game.resource.load.Loadable#loadFiles(java.lang.String)
     */
    @Override
    public Map<String, File> loadFiles(String name)
    {
        return null;
    }

    /**
     * @see bt.game.resource.load.Loadable#loadFonts(java.lang.String)
     */
    @Override
    public Map<String, Font> loadFonts(String name)
    {
        return null;
    }

    /**
     * @see bt.game.resource.load.Loadable#loadObjects(java.lang.String)
     */
    @Override
    public Map<String, Object> loadObjects(String name)
    {
        return null;
    }

    public abstract void render(Graphics g);

    public abstract void tick();
}