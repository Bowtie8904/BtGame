package bt.game.core.obj.terrain.base;

import bt.game.core.obj.impl.GameBody;
import bt.game.core.scene.intf.Scene;
import org.dyn4j.geometry.MassType;

/**
 * A common supertype for all kinds of terrain.
 */
public class Terrain extends GameBody
{
    /**
     * Creates a new instance for the given scene.
     *
     * @param scene
     */
    public Terrain(Scene scene)
    {
        super(scene);
        setMass(MassType.INFINITE);
        scene.getObjectHandler().addObject(this);
    }
}