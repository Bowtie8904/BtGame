package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.impl.GameBody;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.core.scene.map.intf.RectangularMapComponent;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Geometry;
import org.json.JSONObject;

import java.awt.*;

/**
 * A basic rectangular shape that is monitored by collision detection.
 *
 * @author &#8904
 */
public class TerrainBlock extends Terrain implements RectangularMapComponent
{
    private BodyFixture fixture;

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
        setup(scene, collisionFilter, x, y, w, h);
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

    public TerrainBlock()
    {
        super();
    }

    public void setup(Scene scene, Class[] collisionFilter, Unit x, Unit y, Unit w, Unit h)
    {
        super.setup(scene);
        this.w = w;
        this.h = h;

        this.fixture = new BodyFixture(Geometry.createRectangle(w.units(), h.units()));
        this.fixture.setFilter(new CollisionFilter(this, collisionFilter));
        this.fixture.setFriction(0);
        addFixture(this.fixture);

        translate(x.units() + w.units() / 2, y.units() + h.units() / 2);
    }

    public void setFriction(double friction)
    {
        this.fixture.setFriction(friction);
    }

    /**
     * @see Renderable#render(Graphics, Unit,
     * Unit, Unit, Unit)
     */
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
    public void initMapComponent(Scene scene, Unit x, Unit y, Unit z, Unit w, Unit h, JSONObject additionalInfo)
    {
        setup(scene, new Class[] { GameBody.class }, x, y, w, h);
        this.z = z;

        if (additionalInfo != null)
        {
            if (additionalInfo.has("friction"))
            {
                setFriction(additionalInfo.getDouble("friction"));
            }
        }
    }
}