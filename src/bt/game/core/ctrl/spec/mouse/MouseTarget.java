package bt.game.core.ctrl.spec.mouse;

import java.awt.event.MouseEvent;

import org.dyn4j.geometry.Shape;

import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 */
public interface MouseTarget
{
    /**
     * Called when a click with the right mouse button was performed on this instance.
     */
    public void onRightClick();

    /**
     * Called when a click with the left mouse button was performed on this instance.
     */
    public void onLeftClick();

    /**
     * Called when this instance was dragged with the mouse.
     * 
     * @param e
     *            The event that caused this call.
     * @param xOffset
     *            The offset on the x axis from this instances old position to its new one.
     * @param yOffset
     *            The offset on the y axis from this instances old position to its new one.
     */
    public void onDrag(MouseEvent e, Unit xOffset, Unit yOffset);

    /**
     * Called when the mouse is hovering over this instance.
     */
    public void onHover();

    /**
     * Called when the mouse stops hovering over this instance.
     */
    public void afterHover();

    /**
     * Called when the mouse is hovering over this instance and the mouse wheel is moved.
     * 
     * @param clicks
     *            The number of clicks that were performed. Positive value if the wheel was rotated down/towards the
     *            user and negative value if the wheel was rotated up/away from the user.
     */
    public void onMouseWheelMove(int clicks);

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
     * Gets the scene object that this target was created for.
     * 
     * @return
     */
    public Scene getScene();
}