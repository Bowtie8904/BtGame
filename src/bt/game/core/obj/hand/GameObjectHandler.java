package bt.game.core.obj.hand;

import java.awt.Graphics;

import bt.game.core.obj.GameObject;
import bt.runtime.Killable;

/**
 * An interface describing the functionalities of a game object handler. Implementations are supposed to hold
 * collections of game objects and forward tick and render calls to them.
 * 
 * @author &#8904
 */
public interface GameObjectHandler extends Killable
{
    /**
     * Sorts the held objects in a custom manner.
     */
    public void sortObjects();

    /**
     * Adds the given object to the collection.
     * 
     * @param object
     */
    public void addObject(GameObject object);

    /**
     * Removed the given object from the collection.
     * 
     * @param object
     */
    public void removeObject(GameObject object);

    /**
     * Forwards the tick call to all held objects.
     */
    public void tick();

    /**
     * Forwards the render call to all held objects.
     */
    public void render(Graphics g);
}