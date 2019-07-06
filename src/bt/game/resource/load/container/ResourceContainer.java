package bt.game.resource.load.container;

import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bt.game.resource.load.intf.Loadable;
import bt.game.resource.render.impl.Animation;
import bt.game.resource.render.intf.Renderable;
import bt.game.resource.text.Text;
import bt.types.sound.SoundSupplier;

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
    private Map<String, Object> objects;
    private Map<String, Animation> animations;
    private Map<String, Map<Integer, Text>> texts;

    /**
     * Creates a new instance with empty maps.
     */
    public ResourceContainer()
    {
        this.renderables = new HashMap<>();
        this.sounds = new HashMap<>();
        this.files = new HashMap<>();
        this.fonts = new HashMap<>();
        this.objects = new HashMap<>();
        this.animations = new HashMap<>();
        this.texts = new HashMap<>();
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The renderable to map in the resouce loader.
     */
    public void add(String name, Renderable res)
    {
        this.renderables.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The sound supplier to map in the resouce loader.
     */
    public void add(String name, SoundSupplier res)
    {
        this.sounds.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The file to map in the resouce loader.
     */
    public void add(String name, File res)
    {
        this.files.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The font to map in the resouce loader.
     */
    public void add(String name, Font res)
    {
        this.fonts.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The object to map in the resouce loader.
     */
    public void add(String name, Object res)
    {
        this.objects.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param name
     *            The name of the resource.
     * @param res
     *            The object to map in the resouce loader.
     */
    public void add(String name, Animation res)
    {
        this.animations.put(name, res);
    }

    /**
     * Maps the given resource with the given name.
     * 
     * @param id
     *            The id of the text.
     * @param res
     *            The text to map in the resouce loader.
     */
    public void add(int id, Text res)
    {
        var textsForLanguage = this.texts.get(res.getLanguage());

        if (textsForLanguage == null)
        {
            textsForLanguage = new HashMap<Integer, Text>();
            this.texts.put(res.getLanguage(), textsForLanguage);
        }

        this.texts.get(res.getLanguage()).put(id, res);
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
     * Gets all mapped text resources.
     * 
     * @return
     */
    public Map<String, Map<Integer, Text>> getTexts()
    {
        return this.texts;
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