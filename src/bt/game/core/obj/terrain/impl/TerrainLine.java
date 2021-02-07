package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.impl.GameBody;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import bt.utils.Array;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.List;

/**
 * A basic line that is monitored by collision detection and consists of one or more points.
 *
 * @author &#8904
 */
public class TerrainLine extends Terrain implements Renderable
{
    /**
     * Creates a new instance with the given start position and points. This line can collide with the classes
     * that are passed with the collisionFilter array.
     *
     * @param scene
     * @param x
     * @param y
     * @param z
     */
    public TerrainLine(Scene scene, Class[] collisionFilter, Vector2 start, Vector2... points)
    {
        super(scene);

        points = Array.concat(new Vector2[] { start }, points, Vector2[]::new);

        List<Link> links = Geometry.createLinks(points, false);

        for (Link link : links)
        {
            BodyFixture bf = new BodyFixture(link);
            bf.setFilter(new CollisionFilter(this, collisionFilter));
            addFixture(bf);
        }

        translate(start.x, start.y);
    }

    /**
     * Creates a new instacne with the given start position and points that can collide with every GameBody.
     *
     * @param scene
     * @param start
     * @param points
     */
    public TerrainLine(Scene scene, Vector2 start, Vector2... points)
    {
        this(scene, new Class[] { GameBody.class }, start, points);
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