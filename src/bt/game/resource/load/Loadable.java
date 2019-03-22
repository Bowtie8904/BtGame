package bt.game.resource.load;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import bt.types.sound.Sound;

/**
 * Interface to define a class that can load its own resources which can then be accessed by a {@link ResourceLoader}.
 * 
 * @author &#8904
 */
public interface Loadable
{
    /**
     * Loads all images this implementation needs.
     * 
     * <p>
     * The images need to be mapped to a (ResourceLoader)-unique resource name, so that they can be accessed by
     * {@link ResourceLoader#getImage(String)}.
     * </p>
     * 
     * @param name
     *            The name of the resource context, i. e. a unique name for a scene, passed by the ResourceLoader.
     * @return A map containing all loaded images or null if no images need to be loaded.
     */
    public Map<String, BufferedImage> loadImages(String name);

    /**
     * Loads all sounds this implementation needs.
     * 
     * <p>
     * The sounds need to be mapped to a (ResourceLoader)-unique resource name, so that they can be accessed by
     * {@link ResourceLoader#getSound(String)}.
     * </p>
     * 
     * @param name
     *            The name of the resource context, i. e. a unique name for a scene, passed by the ResourceLoader.
     * @return A map containing all loaded sounds or null if no sounds need to be loaded.
     */
    public Map<String, Sound> loadSounds(String name);

    /**
     * Loads all files this implementation needs.
     * 
     * <p>
     * The files need to be mapped to a (ResourceLoader)-unique resource name, so that they can be accessed by
     * {@link ResourceLoader#getFile(String)}.
     * </p>
     * 
     * @param name
     *            The name of the resource context, i. e. a unique name for a scene, passed by the ResourceLoader.
     * @return A map containing all loaded files or null if no files need to be loaded.
     */
    public Map<String, File> loadFiles(String name);

    /**
     * Loads all fonts this implementation needs.
     * 
     * <p>
     * The fonts need to be mapped to a (ResourceLoader)-unique resource name, so that they can be accessed by
     * {@link ResourceLoader#getFont(String)}.
     * </p>
     * 
     * @param name
     *            The name of the resource context, i. e. a unique name for a scene, passed by the ResourceLoader.
     * @return A map containing all loaded fonts or null if no fonts need to be loaded.
     */
    public Map<String, Font> loadFonts(String name);

    /**
     * Loads all objects this implementation needs. (not including any objects that are already loaded by the other load
     * implementations)
     * 
     * <p>
     * The objects need to be mapped to a (ResourceLoader)-unique resource name, so that they can be accessed by
     * {@link ResourceLoader#get(String)}.
     * </p>
     * 
     * @param name
     *            The name of the resource context, i. e. a unique name for a scene, passed by the ResourceLoader.
     * @return A map containing all loaded objects or null if no objects need to be loaded.
     */
    public Map<String, Object> loadObjects(String name);
}