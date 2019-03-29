package bt.game.core.obj;

import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.util.Map;

import bt.game.core.scene.Scene;
import bt.game.resource.load.Loadable;
import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;
import bt.types.sound.SoundSupplier;

/**
 * @author &#8904
 *
 */
public abstract class GameObject implements Loadable
{
    protected Unit x;
    protected Unit y;
    protected Unit z;
    protected Unit w;
    protected Unit h;
    protected Unit xVel;
    protected Unit yVel;
    protected Scene scene;

    public GameObject(Scene scene, Unit x, Unit y, Unit z)
    {
        this.scene = scene;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xVel = Unit.forUnits(0);
        this.yVel = Unit.forUnits(0);
    }

    public Scene getScene()
    {
        return this.scene;
    }

    public void setX(Unit x)
    {
        this.x = x;
    }

    public void setY(Unit y)
    {
        this.y = y;
    }

    public void setZ(Unit z)
    {
        this.z = z;
    }

    public void setW(Unit w)
    {
        this.w = w;
    }

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