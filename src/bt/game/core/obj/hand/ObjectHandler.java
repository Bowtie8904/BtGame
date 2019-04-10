package bt.game.core.obj.hand;

import java.awt.Graphics;

import bt.runtime.Killable;

/**
 * An interface describing the functionalities of an object handler. Implementations are supposed to hold collections of
 * objects and forward tick and render calls to them.
 * 
 * @author &#8904
 */
public interface ObjectHandler extends Killable
{
    /**
     * Initializes the handler.
     */
    public void init();

    /**
     * Sorts the held objects in a custom manner.
     */
    public void sortObjects();

    /**
     * Adds the given object to the collection.
     * 
     * @param object
     */
    public void addObject(Object object);

    /**
     * Removed the given object from the collection.
     * 
     * @param object
     */
    public void removeObject(Object object);

    /**
     * Forwards the tick call to all held objects.
     */
    public void tick(double delta);

    /**
     * Forwards the render call to all held objects.
     */
    public void render(Graphics g);
}