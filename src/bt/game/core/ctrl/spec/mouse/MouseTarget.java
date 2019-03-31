package bt.game.core.ctrl.spec.mouse;

import java.awt.geom.Area;

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
    
    public Area getBounds();
}