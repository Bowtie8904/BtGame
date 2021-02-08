package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.impl.GameBody;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.unit.Unit;
import bt.utils.Array;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A basic line that is monitored by collision detection and consists of one or more points.
 *
 * @author &#8904
 */
public class TerrainLine extends Terrain
{
    protected List<TerrainLineSegment> segements;

    /**
     * Creates a new instance with the given start position and points. This line can collide with the classes
     * that are passed with the collisionFilter array.
     *
     * @param scene
     * @param x
     * @param y
     * @param z
     */
    public TerrainLine(Scene scene, Class[] collisionFilter, Vector2 first, Vector2 second, Vector2... points)
    {
        super(scene);
        this.segements = new ArrayList<>();
        points = Array.concat(new Vector2[] { first, second }, points, Vector2[]::new);

        List<Link> links = Geometry.createLinks(points, false);

        for (Link link : links)
        {
            this.segements.add(new TerrainLineSegment(scene, collisionFilter, link));
        }
    }

    /**
     * Creates a new instacne with the given start position and points that can collide with every GameBody.
     *
     * @param scene
     * @param start
     * @param points
     */
    public TerrainLine(Scene scene, Vector2 first, Vector2 second, Vector2... points)
    {
        this(scene, new Class[] { GameBody.class }, first, second, points);
    }

    /**
     * @see bt.game.core.obj.intf.GameObject#render(Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
    }

    /**
     * @see Renderable#render(Graphics, Unit,
     * Unit, Unit, Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
    }
}