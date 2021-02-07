package bt.game.core.obj.terrain.base;

import bt.game.core.obj.impl.GameBody;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.unit.Unit;
import org.dyn4j.geometry.MassType;

/**
 * A common supertype for all kinds of terrain.
 */
public abstract class Terrain extends GameBody implements Renderable
{
    private Unit z;
    private boolean shouldRender;

    /**
     * Creates a new instance for the given scene.
     *
     * @param scene
     */
    public Terrain(Scene scene)
    {
        super(scene);
        setMass(MassType.INFINITE);
        scene.getObjectHandler().addObject(this);
        this.z = Unit.zero();
        this.shouldRender = true;
    }

    /**
     * @see Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    /**
     * @see Renderable#setZ(Unit)
     */
    @Override
    public void setZ(Unit z)
    {
        this.z = z;
    }

    /**
     * @see Renderable#shouldRender()
     */
    @Override
    public boolean shouldRender()
    {
        return this.shouldRender;
    }

    /**
     * @see Renderable#shouldRender(boolean)
     */
    @Override
    public void shouldRender(boolean shouldRender)
    {
        this.shouldRender = shouldRender;
    }
}