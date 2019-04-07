package bt.game.core.scene.cam;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.GameObject;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * A {@link Camera} extension that centers on a given game object and follows it.
 * 
 * @author &#8904
 */
public class ObjectCamera extends Camera implements Tickable
{
    /** The object to follow. */
    protected GameObject object;

    /**
     * Creates a new instance and sets the object to follow.
     * 
     * @param scene
     */
    public ObjectCamera(Scene scene, GameObject object)
    {
        super(scene);

        if (object == null)
        {
            throw new IllegalArgumentException("Given object may not be null.");
        }

        this.object = object;
    }

    /**
     * Moves the camera to stay centered on the object.
     * 
     * <p>
     * This method calls {@link #moveTo(Unit, Unit) moveTo}, so the {@link #clipToBorders(boolean) clipToBorders}
     * behavior is handled there.
     * </p>
     * 
     * @see bt.game.core.obj.intf.Tickable#tick()
     */
    @Override
    public void tick()
    {
        float xPix = this.object.getX().pixels()
                + (this.object.getW().pixels() / 2)
                - GameContainer.width().pixels() / 2;
        float yPix = this.object.getY().pixels()
                + (this.object.getH().pixels() / 2)
                - GameContainer.height().pixels() / 2;

        moveTo(Unit.forPixels(xPix), Unit.forPixels(yPix));
    }
}