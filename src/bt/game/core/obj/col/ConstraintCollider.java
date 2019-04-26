package bt.game.core.obj.col;

import org.dyn4j.dynamics.contact.ContactConstraint;

/**
 * @author &#8904
 */
public interface ConstraintCollider extends Collider
{
    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.contact.ContactConstraint)
     */
    public boolean onCollision(ContactConstraint contactConstraint);
}