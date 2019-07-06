package bt.game.core.obj.col.intf;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;

/**
 * Allows an object to receive collision information during the broad phase.
 * 
 * @author &#8904
 */
public interface BroadPhaseCollider extends Collider
{
    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture)
     */
    public boolean onCollision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2);
}