package bt.game.resource.load.intf;

import bt.game.core.ctrl.spec.mouse.obj.Cursor;
import bt.game.resource.render.impl.anim.Animation;
import bt.game.resource.render.intf.Renderable;
import bt.io.sound.Sound;
import bt.types.Killable;

import java.awt.*;
import java.io.File;

/**
 * An interface definition for classes that should load, hold, distribute and close resources during their lifespan to
 * ensure centralized resource management.
 *
 * @author &#8904
 */
public interface ResourceLoader extends Killable, Loader
{
    /**
     * Finishes the loading process. This method should be used for things like setting up animations after all
     * resources were loaded.
     */
    public void finishLoad();

    /**
     * Registers the given object so that it will be handled depending on its interfaces.
     *
     * @param object The object to add.
     */
    public void register(Object object);

    /**
     * Registers the given runnable to be executed during the kill operation of this instance.
     *
     * @param closingOp The closing operation to execute.
     */
    public void registerClosingOperation(Runnable closingOp);

    /**
     * Gets the renderable for the given resource name if such an renderable has been loaded before.
     *
     * @param resourceName The unique name that the renderable was loaded with.
     * @return The renderable or null if no mapping for the recource name exists.
     */
    public default Renderable getRenderable(String resourceName)
    {
        return getRenderable(resourceName, Renderable.class);
    }

    /**
     * Gets the renderable for the given resource name if such an renderable has been loaded before.
     *
     * @param resourceName The unique name that the renderable was loaded with.
     * @param castTraget   The target class to cast the returned Renderable to.
     * @return The renderable or null if no mapping for the recource name exists.
     */
    public <T> T getRenderable(String resourceName, Class<T> castTraget);

    /**
     * Gets the sound for the given resource name if such a sound has been loaded before.
     *
     * @param resourceName The unique name that the sound was loaded with.
     * @return The sound or null if no mapping for the recource name exists.
     */
    public Sound getSound(String resourceName);

    /**
     * Gets the file for the given resource name if such a file has been loaded before.
     *
     * @param resourceName The unique name that the file was loaded with.
     * @return The file or null if no mapping for the recource name exists.
     */
    public File getFile(String resourceName);

    /**
     * Gets the font for the given resource name if such a font has been loaded before.
     *
     * @param resourceName The unique name that the font was loaded with.
     * @return The font or null if no mapping for the recource name exists.
     */
    public Font getFont(String resourceName);

    /**
     * Gets the cursor for the given resource name if such a cursor has been loaded before.
     *
     * @param resourceName The unique name that the cursor was loaded with.
     * @return The cursor or null if no mapping for the recource name exists.
     */
    public Cursor getCursor(String resourceName);

    /**
     * Gets the object for the given resource name if such an object has been loaded before.
     *
     * @param resourceName The unique name that the object was loaded with.
     * @return The object or null if no mapping for the recource name exists.
     */
    public Object get(String resourceName);

    /**
     * Gets the animation with tthe given resource name if such an animation has been set up before.
     *
     * @param resourceName The unique name that the animation was loaded with.
     * @return The animation or null if no mapping for the recource name exists.
     */
    public Animation getAnimation(String resourceName);
}