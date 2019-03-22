package bt.game.resource.load.impl;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.Clip;

import bt.game.resource.load.Loadable;
import bt.game.resource.load.ResourceLoader;
import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.types.sound.Sound;
import bt.utils.log.Logger;

/**
 * A basic implementation of the {@link ResourceLoader} interface. This implementation is fully functional and is
 * focused on loading resources from {@link Loadable}s that were {@link #register(Loadable) registered}.
 * 
 * @author &#8904
 */
public class BaseResourceLoader implements ResourceLoader
{
    private Map<String, BufferedImage> images;
    private Map<String, Sound> sounds;
    private Map<String, File> files;
    private Map<String, Font> fonts;
    private Map<String, Object> objects;
    private List<Loadable> loadables;

    /**
     * Creates a new instance and initializes its maps and lists.
     * 
     * <p>
     * This constructor will add the instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable) killOnShutdown} to close resources on application shutdown. The
     * one controlling this resource loader should however call {@link #kill()} and
     * {@link InstanceKiller#unregister(Killable) unregister} the instance as soon as the resources are not needed
     * anymore.
     * </p>
     */
    public BaseResourceLoader()
    {
        this.images = new HashMap<>();
        this.sounds = new HashMap<>();
        this.files = new HashMap<>();
        this.fonts = new HashMap<>();
        this.objects = new HashMap<>();
        this.loadables = new ArrayList<>();
        InstanceKiller.killOnShutdown(this);
    }

    /**
     * Closes all resources loaded by or for this instance.
     * 
     * <p>
     * If an object resource implements {@link Closeable} its {@link Closeable#close() close} method is called. If an
     * object resource implements {@link Killable} its {@link Killable#kill() kill} method is called.
     * </p>
     * 
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Closing resources.");

        for (BufferedImage image : this.images.values())
        {
            image.flush();
        }

        for (Object obj : this.objects.values())
        {
            if (obj instanceof Closeable)
            {
                try
                {
                    ((Closeable)obj).close();
                }
                catch (IOException e)
                {
                    Logger.global().print(e);
                }
            }

            if (obj instanceof Killable)
            {
                ((Killable)obj).kill();
            }
        }

        this.images.clear();
        this.sounds.clear();
        this.files.clear();
        this.fonts.clear();
        this.objects.clear();
        this.loadables.clear();
    }

    /**
     * Maps the given image by the given (case insensitive) resource name. Once the image has been added it becomes
     * accessible by {@link #getImage(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given image.
     * @param value
     *            The image to map.
     */
    protected void add(String resourceName, BufferedImage value)
    {
        this.images.put(resourceName.toUpperCase(), value);
    }

    /**
     * Maps the given sound by the given (case insensitive) resource name. Once the sound has been added it becomes
     * accessible by {@link #getSound(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given sound.
     * @param value
     *            The sound to map.
     */
    protected void add(String resourceName, Sound value)
    {
        this.sounds.put(resourceName.toUpperCase(), value);
    }

    /**
     * Maps the given file by the given (case insensitive) resource name. Once the file has been added it becomes
     * accessible by {@link #getFile(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given file.
     * @param value
     *            The file to map.
     */
    protected void add(String resourceName, File value)
    {
        this.files.put(resourceName.toUpperCase(), value);
    }

    /**
     * Maps the given font by the given (case insensitive) resource name. Once the font has been added it becomes
     * accessible by {@link #getFont(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given font.
     * @param value
     *            The font to map.
     */
    protected void add(String resourceName, Font value)
    {
        this.fonts.put(resourceName.toUpperCase(), value);
    }

    /**
     * Maps the given object by the given (case insensitive) resource name. Once the object has been added it becomes
     * accessible by {@link #get(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given object.
     * @param value
     *            The object to map.
     */
    protected void add(String resourceName, Object value)
    {
        this.objects.put(resourceName.toUpperCase(), value);
    }

    /**
     * @see bt.game.resource.ResourceLoader#getImage(java.lang.String)
     */
    @Override
    public BufferedImage getImage(String resourceName)
    {
        return this.images.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#getSound(java.lang.String)
     */
    @Override
    public Clip getSound(String resourceName)
    {
        return this.sounds.get(resourceName.toUpperCase()).getClip();
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFile(java.lang.String)
     */
    @Override
    public File getFile(String resourceName)
    {
        return this.files.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFont(java.lang.String)
     */
    @Override
    public Font getFont(String resourceName)
    {
        return this.fonts.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#get(java.lang.String)
     */
    @Override
    public Object get(String resourceName)
    {
        return this.objects.get(resourceName.toUpperCase());
    }

    /**
     * Loads all {@link #register(Loadable) registered} {@link Loadable}s by calling their load methods and mapping
     * their returned values in this instance.
     * 
     * @see bt.game.resource.load.ResourceLoader#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        Map<String, BufferedImage> loadedImages;
        Map<String, Sound> loadedSounds;
        Map<String, File> loadedFiles;
        Map<String, Font> loadedFonts;
        Map<String, Object> loadedObjects;

        for (Loadable loadable : this.loadables)
        {
            // images
            loadedImages = loadable.loadImages(name);

            if (loadedImages != null)
            {
                for (String resourceKey : loadedImages.keySet())
                {
                    add(resourceKey, loadedImages.get(resourceKey));
                    Logger.global()
                            .print("Loaded image '" + resourceKey + "' for " + loadable.getClass().getName() + ".");
                }
            }

            // sounds
            loadedSounds = loadable.loadSounds(name);

            if (loadedSounds != null)
            {
                for (String resourceKey : loadedSounds.keySet())
                {
                    add(resourceKey, loadedSounds.get(resourceKey));
                    Logger.global()
                            .print("Loaded sound '" + resourceKey + "' for " + loadable.getClass().getName() + ".");
                }
            }

            // files
            loadedFiles = loadable.loadFiles(name);

            if (loadedFiles != null)
            {
                for (String resourceKey : loadedFiles.keySet())
                {
                    add(resourceKey, loadedFiles.get(resourceKey));
                    Logger.global()
                            .print("Loaded file '" + resourceKey + "' for " + loadable.getClass().getName() + ".");
                }
            }

            // fonts
            loadedFonts = loadable.loadFonts(name);

            if (loadedFonts != null)
            {
                for (String resourceKey : loadedFonts.keySet())
                {
                    add(resourceKey, loadedFonts.get(resourceKey));
                    Logger.global()
                            .print("Loaded font '" + resourceKey + "' for " + loadable.getClass().getName() + ".");
                }
            }

            // objects
            loadedObjects = loadable.loadObjects(name);

            if (loadedObjects != null)
            {
                for (String resourceKey : loadedObjects.keySet())
                {
                    add(resourceKey, loadedObjects.get(resourceKey));
                    Logger.global()
                            .print("Loaded object '" + resourceKey + "' for " + loadable.getClass().getName() + ".");
                }
            }
        }
    }

    /**
     * @see bt.game.resource.load.ResourceLoader#register(bt.game.resource.load.Loadable)
     */
    @Override
    public void register(Loadable loadable)
    {
        this.loadables.add(loadable);
    }
}