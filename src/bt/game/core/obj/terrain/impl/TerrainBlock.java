package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.impl.GameBody;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;

import java.awt.*;

/**
 * A basic rectangular shape that is monitored by collision detection.
 *
 * @author &#8904
 */
public class TerrainBlock extends Terrain implements Renderable
{
    /**
     * Creates a new instance with the given dimensions. This block can collide with the classes
     * that are passed with the collisionFilter array.
     *
     * @param scene
     * @param x
     * @param y
     * @param z
     */
    public TerrainBlock(Scene scene, Class[] collisionFilter, Unit x, Unit y, Unit w, Unit h)
    {
        super(scene);
        this.w = w;
        this.h = h;

        BodyFixture bf = new BodyFixture(Geometry.createRectangle(w.units(), h.units()));
        bf.setFilter(new CollisionFilter(this, collisionFilter));
        addFixture(bf);

        translate(x.units() + w.units() / 2, y.units() + h.units() / 2);
    }

    /**
     * Creates a new instacne with the given dimensions that can collide with every GameBody.
     *
     * @param scene
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public TerrainBlock(Scene scene, Unit x, Unit y, Unit w, Unit h)
    {
        this(scene, new Class[] { GameBody.class }, x, y, w, h);
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#render(Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
        if (debugRendering)
        {
            ShapeRenderer.render(g,
                                 this,
                                 Color.blue);
        }
    }

    /**
     * @see Renderable#render(Graphics, Unit,
     * Unit, Unit, Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        render(g, debugRendering);
    }

    /**
     * @see Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return Unit.zero();
    }

    /**
     * @see Renderable#setZ(Unit)
     */
    @Override
    public void setZ(Unit z)
    {
    }

    /**
     * @see Renderable#shouldRender()
     */
    @Override
    public boolean shouldRender()
    {
        return true;
    }

    /**
     * @see Renderable#shouldRender(boolean)
     */
    @Override
    public void shouldRender(boolean shouldRender)
    {
    }
}