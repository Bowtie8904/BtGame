package bt.game.core.scene.cam;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.GameObject;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class ObjectCamera extends Camera implements Tickable
{
    protected GameObject object;

    /**
     * @param scene
     */
    public ObjectCamera(Scene scene, GameObject object)
    {
        super(scene);
        this.object = object;
    }

    /**
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