package bt.game.core.obj.intf.col;

import java.awt.geom.Area;

/**
 * Defines an object that has bounds and can be checked for intersection.
 * 
 * @author &#8904
 */
public interface Bounds
{
    /**
     * Gets the area that this bounds object is defining.
     * 
     * @return
     */
    public Area getArea();
	
    /**
     * Checks if this bounds is intersecting with the given one.
     * 
     * @param obj
     * @return
     */
	public boolean intersects(Bounds obj);
}