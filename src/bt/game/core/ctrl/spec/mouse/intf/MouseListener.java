package bt.game.core.ctrl.spec.mouse.intf;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * An interface to use for classes that want to receive mouse actions inside the game window.
 */
public interface MouseListener
{
    /**
     * Called when a click with the right mouse button was performed.
     */
    public void onRightClick(MouseEvent e, Unit x, Unit y);

    /**
     * Called when a click with the left mouse button was performed.
     */
    public void onLeftClick(MouseEvent e, Unit x, Unit y);

    /**
     * Called when the mouse was dragged.
     *
     * @param e       The event that caused this call.
     * @param xOffset The offset on the x axis from the mouses old position to its new one.
     * @param yOffset The offset on the y axis from the mouses old position to its new one.
     */
    public void onDrag(MouseEvent e, Unit xOffset, Unit yOffset);

    /**
     * Called when the mouse wheel is being moved.
     *
     * @param clicks The number of clicks that were performed. Positive value if the wheel was rotated down/towards the
     *               user and negative value if the wheel was rotated up/away from the user.
     */
    public void onMouseWheelMove(MouseWheelEvent e, int clicks);

    /**
     * Gets the scene object that this listener was created for.
     * <p>
     * This is used for proper cleanup during a scenes kill method.
     *
     * @return
     */
    public Scene getScene();
}