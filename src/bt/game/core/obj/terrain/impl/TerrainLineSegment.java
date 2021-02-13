package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.col.intf.NarrowPhaseCollider;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.obj.terrain.intf.TerrainLineCollider;
import bt.game.core.scene.intf.Scene;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.NarrowphaseCollisionData;

import java.awt.*;

public class TerrainLineSegment extends Terrain implements NarrowPhaseCollider
{
    private Link link;
    private double angle;
    private BodyFixture fixture;
    private TerrainLineCollider collider;

    /**
     * Creates a new instance for the given scene.
     *
     * @param scene
     */
    public TerrainLineSegment(Scene scene, Class[] collisionFilter,
                              Link link, Vector2[] frictions, TerrainLineCollider collider)
    {
        super(scene);
        this.link = link;
        this.collider = collider;

        calculateAngle();

        this.fixture = new BodyFixture(link);
        this.fixture.setFilter(new CollisionFilter(this, collisionFilter));

        addFixture(this.fixture);
    }

    public void setFriction(Vector2[] frictions)
    {
        double friction = 0;

        for (Vector2 fric : frictions)
        {
            if (this.angle >= fric.x)
            {
                friction = fric.y;
            }
            else
            {
                break;
            }
        }

        this.fixture.setFriction(friction);
    }

    public BodyFixture getFixture()
    {
        return this.fixture;
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
        return this.angle;
    }

    public void calculateAngle()
    {
        double angle1 = getAngle(getPoint1(), getPoint2());
        double angle2 = getAngle(getPoint2(), getPoint1());
        this.angle = Math.min(angle1, angle2);
    }

    protected double getAngle(Vector2 p1, Vector2 p2)
    {
        double deltaY = (p1.y - p2.y);
        double deltaX = (p2.x - p1.x);
        double result = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return (result < 0) ? (360.0 + result) : result;
    }

    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        if (debugRendering)
        {
            ShapeRenderer.render(g,
                                 this,
                                 Color.blue);
        }
    }

    @Override
    public CollisionBody getBody()
    {
        return this;
    }

    @Override
    public boolean onCollision(NarrowphaseCollisionData narrowphaseCollisionData, CollisionBody body)
    {
        return this.collider.onCollision(narrowphaseCollisionData, body, this);
    }
}