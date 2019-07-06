package bt.game.core.obj.intf;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

/**
 * A base interface for all objects that have a position in the game, i.e. characters in a scene.
 * 
 * @author &#8904
 */
public interface GameObject
{
    /**
     * Gets the scene that this object was created for.
     * 
     * @return
     */
    public Scene getScene();

    /**
     * Sets the width of this object.
     * 
     * @param w
     */
    public void setW(Unit w);

    /**
     * Sets the height of this object.
     * 
     * @param h
     */
    public void setH(Unit h);

    /**
     * Gets the height of this object.
     * 
     * @return
     */
    public Unit getH();

    /**
     * Gets the width of this object.
     * 
     * @return
     */
    public Unit getW();

    /**
     * Gets the x position of this object.
     * 
     * @return
     */
    public Unit getX();

    /**
     * Gets the y position of this object.
     * 
     * @return
     */
    public Unit getY();

    /**
     * Sets the x position of this object.
     * 
     * @param x
     */
    public void setX(Unit x);

    /**
     * Sets the y position of this object.
     * 
     * @param y
     */
    public void setY(Unit y);

    /**
     * Gets the x position of the center of this object.
     * 
     * @return
     */
    public Unit getCenterX();

    /**
     * Gets the x position of the center of this object.
     * 
     * @return
     */
    public Unit getCenterY();
}