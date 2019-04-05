package bt.game.resource.load;

import java.awt.Font;
import java.io.File;

import bt.game.resource.render.Renderable;
import bt.runtime.Killable;
import bt.types.sound.Sound;

/**
 * An interface definition for classes that should load, hold, distribute and close resources during their lifespan to
 * ensure centralized resource management.
 * 
 * @author &#8904
 */
public interface ResourceLoader extends Killable
{
    /**
     * Loads all resources for the given context name (i. e. unique scene name).
     * 
     * @param name
     *            The context name to load resources for.
     */
    public void load(String name);

    /**
     * Finishes the loading process. This method should be used for things like setting up animations after all
     * resources were loaded.
     */
    public void finishLoad();

    /**
     * Registers the given object so that it will be handled depending on its interfaces.
     * 
     * @param object
     *            The object to add.
     */
    public void register(Object object);

    /**
     * Registers the given runnable to be executed during the kill operation of this instance.
     * 
     * @param closingOp
     *            The closing operation to execute.
     */
    public void registerClosingOperation(Runnable closingOp);

    /**
     * Gets the renderable for the given resource name if such an renderable has been loaded before.
     * 
     * @param resourceName
     *            The unique name that the renderable was loaded with.
     * @return The renderable or null if no mapping for the recource name exists.
     */
    public Renderable getRenderable(String resourceName);

    /**
     * Gets the sound for the given resource name if such a sound has been loaded before.
     * 
     * @param resourceName
     *            The unique name that the sound was loaded with.
     * @return The sound or null if no mapping for the recource name exists.
     */
    public Sound getSound(String resourceName);

    /**
     * Gets the file for the given resource name if such a file has been loaded before.
     * 
     * @param resourceName
     *            The unique name that the file was loaded with.
     * @return The file or null if no mapping for the recource name exists.
     */
    public File getFile(String resourceName);

    /**
     * Gets the font for the given resource name if such a font has been loaded before.
     * 
     * @param resourceName
     *            The unique name that the font was loaded with.
     * @return The font or null if no mapping for the recource name exists.
     */
    public Font getFont(String resourceName);

    /**
     * Gets the object for the given resource name if such an object has been loaded before.
     * 
     * @param resourceName
     *            The unique name that the object was loaded with.
     * @return The object or null if no mapping for the recource name exists.
     */
    public Object get(String resourceName);
}