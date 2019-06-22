package bt.game.resource.load;

/**
 * @author &#8904
 */
public interface Loader
{
    /**
     * Loads all resources for the given context name (i. e. unique scene name).
     * 
     * @param name
     *            The context name to load resources for.
     */
    public void load(String name);
}