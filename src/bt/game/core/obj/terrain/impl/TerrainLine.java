package bt.game.core.obj.terrain.impl;

import bt.game.core.obj.gravity.GravityAffected;
import bt.game.core.obj.impl.GameBody;
import bt.game.core.obj.terrain.base.Terrain;
import bt.game.core.scene.intf.Scene;
import bt.game.core.scene.map.intf.LineMapComponent;
import bt.game.resource.load.exc.LoadException;
import bt.game.resource.render.intf.Renderable;
import bt.game.util.unit.Coordinate;
import bt.game.util.unit.Unit;
import bt.utils.Array;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.world.NarrowphaseCollisionData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A basic line that is monitored by collision detection and consists of one or more points.
 * <p>
 * The following behaviors can be customized by overriding the {@link TerrainLine#onCollision(NarrowphaseCollisionData, CollisionBody, TerrainLineSegment)}
 * method:
 * If a segment has a friction of 0 and is touched by an object that implements
 * the {@link bt.game.core.obj.gravity.GravityAffected} interface, then the
 * velocity Y will be set to the value returned by {@link GravityAffected#getMaxGravityVelocity()}.
 * This creates a unclimbable hill effect by sliding the object back down instantly.
 * <p>
 * If a segment has a friction higher than 0 and is touched by a {@link bt.game.core.obj.intf.GameObject}
 * then the objects velocity X will be set to 0. This avoids a sliding effect with low friction numbers.
 *
 * @author &#8904
 */
public class TerrainLine extends Terrain implements LineMapComponent
{
    protected List<TerrainLineSegment> segements;

    /**
     * An array of vectors describing the frictions above different degrees.
     * <p>
     * Default: Everything of 0 or more degree pitch will have a friction of 1.
     */
    protected Vector2[] frictions = new Vector2[] { new Vector2(0, 1) };

    /**
     * Creates a new instance with the given start position and points. This line can collide with the classes
     * that are passed with the collisionFilter array.
     */
    public TerrainLine(Scene scene, Class[] collisionFilter, Coordinate first, Coordinate second, Coordinate... points)
    {
        super(scene);
        setup(scene, collisionFilter, first, second, points);
    }

    /**
     * Creates a new instacne with the given start position and points that can collide with every GameBody.
     */
    public TerrainLine(Scene scene, Coordinate first, Coordinate second, Coordinate... points)
    {
        this(scene, new Class[] { GameBody.class }, first, second, points);
    }

    public TerrainLine()
    {
        super();
    }

    public void setup(Scene scene, Class[] collisionFilter, Coordinate first, Coordinate second, Coordinate... points)
    {
        super.setup(scene);
        this.segements = new ArrayList<>();
        points = Array.concat(new Coordinate[] { first, second }, points, Coordinate[]::new);

        Vector2[] vectorCoords = Arrays.stream(points)
                                       .map(coord -> new Vector2(coord.getX().units(), coord.getY().units()))
                                       .toArray(Vector2[]::new);

        List<Link> links = Geometry.createLinks(vectorCoords, false);

        for (Link link : links)
        {
            this.segements.add(new TerrainLineSegment(this.scene, collisionFilter, link, this.frictions, this::onCollision));
        }
    }

    /**
     * Sets an array of frictions with their respective minimum angles.
     * <p>
     * This method will go through all segments of this line. Each segment
     * will go through the fractions array sequentially. If a segments angle is
     * equal or higher than the x value of friction, then it willset its own
     * friction to the y value of the vector.
     * <p>
     * Example:
     * <pre>
     *     line.setFrictions(new Vector2(0, 1),
     *                       new Vector2(30, 0));
     * </pre>
     * This will set the friction of any segment with an angle equal to or
     * higher than 0 to 1. Any segment with an angle of 30 or higher will get a
     * friction of 0.
     *
     * @param frictions
     */
    public void setFrictions(Vector2... frictions)
    {
        this.frictions = frictions;

        for (TerrainLineSegment segm : this.segements)
        {
            segm.setFriction(frictions);
        }
    }

    /**
     * @see Renderable#render(Graphics, Unit,
     * Unit, Unit, Unit)
     */
    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
    }

    public boolean onCollision(NarrowphaseCollisionData narrowphaseCollisionData, CollisionBody body, TerrainLineSegment segment)
    {
        if (segment.getFixture().getFriction() > 0 && body instanceof GameBody)
        {
            ((GameBody)body).setVelocityX(0);
        }

        if (segment.getFixture().getFriction() == 0 && body instanceof GravityAffected)
        {
            ((GravityAffected)body).setVelocityY(((GravityAffected)body).getMaxGravityVelocity());
        }

        return true;
    }

    @Override
    public void initMapComponent(Scene scene, Unit z, Coordinate[] coordinates, JSONObject additionalInfo)
    {
        if (coordinates == null || coordinates.length < 2)
        {
            throw new LoadException("Cant set up TerrainLine with less that two coordinates.");
        }

        this.z = z;

        Coordinate[] additionalCoords = Arrays.stream(coordinates).skip(2).toArray(Coordinate[]::new);

        setup(scene, new Class[] { GameBody.class }, coordinates[0], coordinates[1], additionalCoords);

        if (additionalInfo != null)
        {
            if (additionalInfo.has("frictions"))
            {
                JSONArray frictionsArray = additionalInfo.getJSONArray("frictions");
                JSONObject obj;
                List<Vector2> frictionVectors = new ArrayList<>();

                for (int i = 0; i < frictionsArray.length(); i++)
                {
                    obj = frictionsArray.getJSONObject(i);

                    if (obj.has("angle") && obj.has("friction"))
                    {
                        frictionVectors.add(new Vector2(obj.getDouble("angle"), obj.getDouble("friction")));
                    }
                    else
                    {
                        throw new LoadException("Incomplete frictions configuration. Values 'angle' and 'friction' expected.");
                    }
                }

                setFrictions(frictionVectors.stream().toArray(Vector2[]::new));
            }
        }
    }
}