package bt.game.resource.load;

/**
 * Interface to define a class that can load its own resources which can then be accessed by a {@link ResourceLoader}.
 * 
 * @author &#8904
 */
public interface Loadable
{
    /**
     * Loads all for the scene with the given name required resources into the given container.
     * 
     * @param name
     *            The name of the scene for which the resoueces should be loaded.
     * @param container
     *            The container that the resources are added to via its add methods.
     */
    public void load(String name, ResourceContainer container);
}