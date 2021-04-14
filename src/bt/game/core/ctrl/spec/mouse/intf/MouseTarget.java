package bt.game.core.ctrl.spec.mouse.intf;

import bt.game.util.unit.Unit;
import org.dyn4j.geometry.Shape;

/**
 * An interface to use for classes that can be targets of mouse actions such as buttons.
 *
 * @author &#8904
 */
public interface MouseTarget extends MouseListener
{
    /**
     * Called when the mouse is hovering over this instance.
     */
    public void onHover();

    /**
     * Called when the mouse stops hovering over this instance.
     */
    public void afterHover();

    /**
     * Gets the Z position of this instance.
     *
     * <p>
     * This value is relevant for when the mouse is hovering over multiple objects at once. Only the onHover method of
     * the object with the highest Z value will be called.
     * </p>
     *
     * @return
     */
    public Unit getZ();

    /**
     * Gets the shape of this instance. Used to determine whether a click was performed in this instances area and so
     * on.
     *
     * @return
     */
    public Shape getShape();

    /**
     * Indicates whether this target is affected by the camera translation, so that we offset is taken into account.
     *
     * @return
     */
    public boolean affectedByCamera();

    /**
     * Indicates whether this target is targetable while the game is paused..
     *
     * @return
     */
    public default boolean enabledDuringPause()
    {
        return false;
    }

}