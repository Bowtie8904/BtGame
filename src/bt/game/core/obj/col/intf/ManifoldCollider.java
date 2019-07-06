package bt.game.core.obj.col.intf;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;

/**
 * Allows an object to receive manifold collision information.
 * 
 * @author &#8904
 */
public interface ManifoldCollider extends Collider
{
    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.manifold.Manifold)
     */
    public boolean onCollision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold);
}