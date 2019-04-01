package bt.game.core.obj.intf;

import bt.game.core.obj.GameObject;

/**
 * Defines collision behavior of a passive object. Passive means that the object can get hit, but can't actively hit
 * anything. A logical example would be a wall. The player object can run against the wall which is merely playing a
 * passive part in this collision.
 * 
 * @author &#8904
 */
@FunctionalInterface
public interface PassiveCollider
{
    /**
     * Defines the action that is executed when this object is hit by a game object.
     * 
     * @param object
     *            The object that collided with this one.
     */
    public void passiveCollision(GameObject object);
}