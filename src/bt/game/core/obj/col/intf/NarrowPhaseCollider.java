package bt.game.core.obj.col.intf;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.world.NarrowphaseCollisionData;

/**
 * Allows an object to receive collision information during the narrow phase.
 * 
 * @author &#8904
 */
public interface NarrowPhaseCollider extends Collider
{
    public boolean onCollision(NarrowphaseCollisionData narrowphaseCollisionData);
}