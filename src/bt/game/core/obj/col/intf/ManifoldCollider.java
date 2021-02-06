package bt.game.core.obj.col.intf;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.world.ManifoldCollisionData;

/**
 * Allows an object to receive manifold collision information.
 * 
 * @author &#8904
 */
public interface ManifoldCollider extends Collider
{
    public boolean onCollision(ManifoldCollisionData manifoldCollisionData);
}