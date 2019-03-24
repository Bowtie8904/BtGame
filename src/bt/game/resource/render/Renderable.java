package bt.game.resource.render;

import java.awt.Graphics;

import bt.game.util.unit.Unit;
import bt.runtime.Killable;

/**
 * Defines a class which can be rendered.
 * 
 * @author &#8904
 */
public interface Renderable extends Killable
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
}