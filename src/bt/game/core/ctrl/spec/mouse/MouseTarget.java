package bt.game.core.ctrl.spec.mouse;

import org.dyn4j.geometry.Shape;

import bt.game.util.unit.Unit;

/**
 * @author &#8904
 */
public interface MouseTarget
{
    public void onRightClick();

    public void onLeftClick();

    public void onDrag(Unit xOffset, Unit yOffset);

    public void onHover();

    public void afterHover();

    public void onMouseWheelMove(int clicks);

    public Unit getZ();
    
    public Shape getShape();
}