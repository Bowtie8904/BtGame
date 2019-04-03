package bt.game.resource.render;

import java.awt.Graphics;

import bt.game.util.unit.Unit;

/**
 * Defines a class which can be rendered.
 * 
 * @author &#8904
 */
public interface Renderable
{
    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The given Graphics object is used
     * to do the drawing.
     * 
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h);
	
	/**
     * Renders this instance. The given Graphics object is used
     * to do the drawing.
     * 
     * @param g
     */
	public void render(Graphics g);

    /**
     * Gets the Z position of this object. This should be used to render the objects with the lowest Z value first.
     * 
     * @return
     */
    public Unit getZ();
}