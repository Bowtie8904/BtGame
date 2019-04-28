package bt.game.core.obj;

import bt.game.core.scene.Scene;
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

    public Unit getH();

    public Unit getW();

    public Unit getX();

    public Unit getY();

    public void setX(Unit x);

    public void setY(Unit y);

    public Unit getCenterX();

    public Unit getCenterY();
}