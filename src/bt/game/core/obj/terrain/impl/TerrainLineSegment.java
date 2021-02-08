package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;

import java.awt.*;

public class TerrainLineSegment extends Terrain
{
    private Link link;

    /**
     * Creates a new instance for the given scene.
     *
     * @param scene
     */
    public TerrainLineSegment(Scene scene, Class[] collisionFilter, Link link)
    {
        super(scene);
        this.link = link;

        BodyFixture bf = new BodyFixture(link);
        bf.setFilter(new CollisionFilter(this, collisionFilter));
        addFixture(bf);

        System.out.println(getAngle());
    }

    public Vector2 getPoint1()
    {
        return this.link.getPoint1();
    }

    public Vector2 getPoint2()
    {
        return this.link.getPoint2();
    }

    public double getAngle()
    {
        double deltaY = (getPoint1().y - getPoint2().y);
        double deltaX = (getPoint2().x - getPoint1().x);
        double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360.0 + result) : result;
    }

    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        render(g, debugRendering);
    }

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
}