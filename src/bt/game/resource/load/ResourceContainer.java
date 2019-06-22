package bt.game.resource.load;

import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bt.game.resource.render.Renderable;
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
    private Map<Integer, Text> texts;

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
     * @param id
     *            The id of the text.
     * @param res
     *            The text to map in the resouce loader.
     */
    public void add(int id, Text res)
    {
        this.texts.put(id, res);
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
    public Map<Integer, Text> getTexts()
    {
        return this.texts;
    }

    /**
     * Gets all mapped object resources.
     * 
     * @return
     */
    public Map<String, Object> getObjects()
    {
        return this.objects;
    }
}