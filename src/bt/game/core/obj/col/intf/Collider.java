package bt.game.core.obj.col.intf;

import org.dyn4j.collision.CollisionBody;

/**
 * The super type of all collision related interfaces.
 * 
 * @author &#8904
 */
public interface Collider
{
    /**
     * Gets the body that is used during collision operations.
     * 
     * @return
     */
    public CollisionBody getBody();
}