package bt.game.core.obj.intf;

/**
 * Defines collision behavior of a passive object. Passive means that the object can get hit, but can't actively hit
 * anything. A logical example would be a wall. The player object can run against the wall which is merely playing a
 * passive part in this collision.
 * 
 * @author &#8904
 */
public interface PassiveCollider extends Bounds
{
    /**
     * Defines the action that is executed when this object is hit by an active collider.
     * 
     * @param object
     *            The object that collided with this one.
     */
    public void passiveCollision(ActiveCollider object);
}