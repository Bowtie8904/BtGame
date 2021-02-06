package bt.game.core.obj.col.intf;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.world.BroadphaseCollisionData;

/**
 * Allows an object to receive collision information during the broad phase.
 * 
 * @author &#8904
 */
public interface BroadPhaseCollider extends Collider
{
    public boolean onCollision(BroadphaseCollisionData broadphaseCollisionData);
}