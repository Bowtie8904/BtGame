package bt.game.core.obj.col.intf;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.world.NarrowphaseCollisionData;

/**
 * Allows an object to receive collision information during the narrow phase.
 *
 * @author &#8904
 */
public interface NarrowPhaseCollider extends Collider
{
    public boolean onCollision(NarrowphaseCollisionData narrowphaseCollisionData, CollisionBody body);
}