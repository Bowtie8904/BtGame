package bt.game.core.obj.col.intf;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.world.ContactCollisionData;

/**
 * Allows an object to receive collision constraint information.
 *
 * @author &#8904
 */
public interface ConstraintCollider extends Collider
{
    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.contact.ContactConstraint)
     */
    public boolean onCollision(ContactCollisionData data, CollisionBody body);
}