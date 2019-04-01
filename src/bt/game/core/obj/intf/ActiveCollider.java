package bt.game.core.obj.intf;

import bt.game.core.obj.GameObject;

/**
 * @author &#8904
 *
 */
@FunctionalInterface
public interface ActiveCollider
{
    public void activeCollision(GameObject object);
}