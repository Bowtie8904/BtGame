package bt.game.resource.load.container;

import bt.game.core.ctrl.spec.mouse.obj.Cursor;
import bt.game.resource.load.intf.Loadable;
import bt.game.resource.render.impl.anim.Animation;
import bt.game.resource.render.intf.Renderable;
import bt.io.sound.SoundSupplier;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A class which can hold maps of resources.
 *
 * <p>
 * Used to let {@link Loadable loadables} give their required resources back to the requesting resource loader.
 * </p>
 *
 * @author &#8904
 */
public class ResourceContainer
{
    private Map<String, Renderable> renderables;
    private Map<String, SoundSupplier> sounds;
    private Map<String, File> files;
    private Map<String, Font> fonts;
    private Map<String, Cursor> cursors;
    private Map<String, Object> objects;
    private Map<String, Animation> animations;

    /**
     * Creates a new instance with empty maps.
     */
    public ResourceContainer()
    {
        this.renderables = new HashMap<>();
        this.sounds = new HashMap<>();
        this.files = new HashMap<>();
        this.fonts = new HashMap<>();
        this.cursors = new HashMap<>();
        this.objects = new HashMap<>();
        this.animations = new HashMap<>();
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The renderable to map in the resouce loader.
     */
    public void add(String name, Renderable res)
    {
        this.renderables.put(name,
                             res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The sound supplier to map in the resouce loader.
     */
    public void add(String name, SoundSupplier res)
    {
        this.sounds.put(name,
                        res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The file to map in the resouce loader.
     */
    public void add(String name, File res)
    {
        this.files.put(name,
                       res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The font to map in the resouce loader.
     */
    public void add(String name, Font res)
    {
        this.fonts.put(name,
                       res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The font to map in the resouce loader.
     */
    public void add(String name, Cursor res)
    {
        this.cursors.put(name,
                         res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The object to map in the resouce loader.
     */
    public void add(String name, Object res)
    {
        this.objects.put(name,
                         res);
    }

    /**
     * Maps the given resource with the given name.
     *
     * @param name The name of the resource.
     * @param res  The object to map in the resouce loader.
     */
    public void add(String name, Animation res)
    {
        this.animations.put(name,
                            res);
    }

    /**
     * Gets all mapped renderable resources.
     *
     * @return
     */
    public Map<String, Renderable> getRenderables()
    {
        return this.renderables;
    }

    /**
     * Gets all mapped sound resources.
     *
     * @return
     */
    public Map<String, SoundSupplier> getSounds()
    {
        return this.sounds;
    }

    /**
     * Gets all mapped file resources.
     *
     * @return
     */
    public Map<String, File> getFiles()
    {
        return this.files;
    }

    /**
     * Gets all mapped font resources.
     *
     * @return
     */
    public Map<String, Font> getFonts()
    {
        return this.fonts;
    }

    /**
     * Gets all mapped cursor resources.
     *
     * @return
     */
    public Map<String, Cursor> getCursors()
    {
        return this.cursors;
    }

    /**
     * Gets all mapped object resources.
     *
     * <p>
     * This does only include resources that where specifically added via {@link #add(String, Object)}.
     * </p>
     *
     * @return
     */
    public Map<String, Object> getObjects()
    {
        return this.objects;
    }

    /**
     * Gets all mapped animation resources.
     *
     * @return
     */
    public Map<String, Animation> getAnimations()
    {
        return this.animations;
    }
}