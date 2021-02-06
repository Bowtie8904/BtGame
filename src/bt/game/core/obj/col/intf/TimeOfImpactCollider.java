package bt.game.core.obj.col.intf;

import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;

/**
 * @author &#8904
 *
 */
public interface TimeOfImpactCollider extends Collider
{
    public boolean onCollision(PhysicsBody physicsBody1, BodyFixture bodyFixture1, PhysicsBody physicsBody2, BodyFixture bodyFixture2, TimeOfImpact timeOfImpact);
}