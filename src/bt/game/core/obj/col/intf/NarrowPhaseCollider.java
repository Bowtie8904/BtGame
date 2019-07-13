package bt.game.core.obj.col.intf;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;

/**
 * Allows an object to receive collision information during the narrow phase.
 * 
 * @author &#8904
 */
public interface NarrowPhaseCollider extends Collider
{
    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.narrowphase.Penetration)
     */
    public boolean onCollision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2,
                               Penetration penetration);
}