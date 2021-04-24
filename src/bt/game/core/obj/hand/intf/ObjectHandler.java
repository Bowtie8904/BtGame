package bt.game.core.obj.hand.intf;

import bt.types.Killable;

import java.awt.*;

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
    public void render(Graphics2D g, boolean debugRendering);

    /**
     * Modifies the given Graphics object to represent the help light sources.
     */
    public void renderLightSources(Graphics2D g, boolean debugRendering);

    /**
     * Updates the y velocities of all held gravity affected objects.
     */
    public void updateGravityVelocities(double delta);

    /**
     * Indicates that all objects should refresh their data.
     */
    public void refresh();
}