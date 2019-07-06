package bt.game.core.scene.cam;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.obj.intf.GameObject;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

/**
 * A {@link Camera} extension that centers on a given game object and follows it.
 * 
 * @author &#8904
 */
public class ObjectCamera extends Camera
{
    /** The object to follow. */
    protected GameObject object;

    /**
     * The area to each side on the X axis of the objects middle point that the given object can move in without moving
     * the camera.
     */
    protected Unit movementOffsetX;

    /**
     * The area to each side on the Y axis of the objects middle point that the given object can move in without moving
     * the camera.
     */
    protected Unit movementOffsetY;

    /**
     * Creates a new instance and sets the object to follow when it leaves the area defined by the given offset.
     * 
     * @param scene
     * @param object
     * @param movementOffset
     */
    public ObjectCamera(Scene scene, GameObject object, Unit movementOffsetX, Unit movementOffsetY)
    {
        super(scene);
        this.movementOffsetX = movementOffsetX;
        this.movementOffsetY = movementOffsetY;

        if (object == null)
        {
            throw new IllegalArgumentException("Given object may not be null.");
        }

        this.object = object;
    }

    /**
     * Creates a new instance and sets the object to follow.
     * 
     * @param scene
     * @param object
     */
    public ObjectCamera(Scene scene, GameObject object)
    {
        this(scene, object, Unit.zero(), Unit.zero());
    }

    /**
     * Moves the camera to keep the object inside the area defined by the offsets.
     * 
     * <p>
     * This method calls {@link #moveTo(Unit, Unit) moveTo}, so the {@link #clipToBorders(boolean) clipToBorders}
     * behavior is handled there.
     * </p>
     * 
     * @see bt.game.core.obj.intf.Tickable#tick()
     */
    @Override
    public void tick(double delta)
    {
        double objectX = this.object.getCenterX().units();
        double objectY = this.object.getCenterY().units();
        
        double camX = getX().units() + GameContainer.width().units() / 2;
        double camY = getY().units() + GameContainer.height().units() / 2;
        
        double xMove = 0;
        double yMove = 0;
        
        if (objectX > camX + this.movementOffsetX.units())
        {
            xMove = objectX - camX - this.movementOffsetX.units();
        }
        else if (objectX < camX - this.movementOffsetX.units())
        {
            xMove = objectX - camX + this.movementOffsetX.units();
        }

        if (objectY > camY + this.movementOffsetY.units())
        {
            yMove = objectY - camY - this.movementOffsetY.units();
        }
        else if (objectY < camY - this.movementOffsetY.units())
        {
            yMove = objectY - camY + this.movementOffsetY.units();
        }

        moveTo(getX().addUnits(xMove), getY().addUnits(yMove));
    }
}