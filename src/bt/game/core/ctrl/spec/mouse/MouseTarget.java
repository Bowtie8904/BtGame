package bt.game.core.ctrl.spec.mouse;

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

    public Unit getX();

    public Unit getY();

    public Unit getZ();

    public Unit getW();

    public Unit getH();
}