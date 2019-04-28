package bt.game.core.obj.col;

import org.dyn4j.dynamics.Body;

/**
 * The super type of all collision related interfaces.
 * 
 * @author &#8904
 */
public interface Collider
{
    public Body getBody();
}