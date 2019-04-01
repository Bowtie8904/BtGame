package bt.game.core.obj.intf;

import bt.game.core.obj.GameObject;

/**
 * @author &#8904
 *
 */
@FunctionalInterface
public interface PassiveCollider
{
    public void passiveCollision(GameObject object);
}