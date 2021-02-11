package bt.game.core.obj.terrain.intf;

import bt.game.core.obj.terrain.impl.TerrainLineSegment;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.world.NarrowphaseCollisionData;

public interface TerrainLineCollider
{
    public boolean onCollision(NarrowphaseCollisionData data, CollisionBody body, TerrainLineSegment segment);
}
