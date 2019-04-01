package bt.game.core.obj.intf;

import bt.game.core.obj.GameObject;

/**
 * Defines collision behavior of an active object. Active means that the object can actively collide with another
 * object. A logical example would be a bullet. The bullet can be fired at a wall, so it plays the active part in the
 * collision.
 * 
 * @author &#8904
 */
@FunctionalInterface
public interface ActiveCollider
{
    /**
     * Defines the action that is executed when this object hits a game object.
     * 
     * @param object
     *            The object that has been hit by this one.
     */
    public void activeCollision(GameObject object);
}