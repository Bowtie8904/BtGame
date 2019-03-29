package bt.game.resource.load.impl;

import java.awt.Font;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bt.game.resource.load.Loadable;
import bt.game.resource.load.ResourceLoader;
import bt.game.resource.render.Renderable;
import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.types.sound.Sound;
import bt.types.sound.SoundSupplier;
import bt.utils.log.Logger;

/**
 * A basic implementation of the {@link ResourceLoader} interface. This implementation is fully functional and is
 * focused on loading resources from {@link Loadable}s that were {@link #register(Loadable) registered}.
 * 
 * @author &#8904
 */
public class BaseResourceLoader implements ResourceLoader
{
    protected boolean killed = false;
    private Map<String, Renderable> renderables;
    private Map<String, SoundSupplier> sounds;
    private Map<String, File> files;
    private Map<String, Font> fonts;
    private Map<String, Object> objects;
    private List<Loadable> loadables;
    private List<Runnable> closingOpeartions;

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
        this.renderables = new HashMap<>();
        this.sounds = new HashMap<>();
        this.files = new HashMap<>();
        this.fonts = new HashMap<>();
        this.objects = new HashMap<>();
        this.loadables = new ArrayList<>();
        this.closingOpeartions = new ArrayList<>();
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

        this.killed = true;

        for (Renderable renderable : this.renderables.values())
        {
            renderable.kill();
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

            if (obj instanceof Killable
                    && (!InstanceKiller.isActive() || !InstanceKiller.isRegistered((Killable)obj)))
            {
                ((Killable)obj).kill();
            }
        }

        for (Runnable closingOp : this.closingOpeartions)
        {
            closingOp.run();
        }

        this.renderables.clear();
        this.sounds.clear();
        this.files.clear();
        this.fonts.clear();
        this.objects.clear();
        this.loadables.clear();
        this.closingOpeartions.clear();
    }

    /**
     * Maps the given renderable by the given (case insensitive) resource name. Once the renderable has been added it
     * becomes accessible by {@link #getRenderable(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given renderable.
     * @param value
     *            The renderable to map.
     */
    protected void add(String resourceName, Renderable value)
    {
        this.renderables.put(resourceName.toUpperCase(), value);
    }

    /**
     * Maps the given sound supplier by the given (case insensitive) resource name. Once the sound supplier has been
     * added its services become accessible by {@link #getSound(String)}.
     * 
     * @param resourceName
     *            The unique resource name for the given sound supplier.
     * @param value
     *            The sound to map.
     */
    protected void add(String resourceName, SoundSupplier value)
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
     * @see bt.game.resource.ResourceLoader#getRenderable(java.lang.String)
     */
    @Override
    public Renderable getRenderable(String resourceName)
    {
        if (this.killed)
        {
            throw new IllegalStateException("Killed ResourceLoader can't supply resources.");
        }

        return this.renderables.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#getSound(java.lang.String)
     */
    @Override
    public Sound getSound(String resourceName)
    {
        if (this.killed)
        {
            throw new IllegalStateException("Killed ResourceLoader can't supply resources.");
        }

        return this.sounds.get(resourceName.toUpperCase()).getSound();
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFile(java.lang.String)
     */
    @Override
    public File getFile(String resourceName)
    {
        if (this.killed)
        {
            throw new IllegalStateException("Killed ResourceLoader can't supply resources.");
        }

        return this.files.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFont(java.lang.String)
     */
    @Override
    public Font getFont(String resourceName)
    {
        if (this.killed)
        {
            throw new IllegalStateException("Killed ResourceLoader can't supply resources.");
        }

        return this.fonts.get(resourceName.toUpperCase());
    }

    /**
     * @see bt.game.resource.ResourceLoader#get(java.lang.String)
     */
    @Override
    public Object get(String resourceName)
    {
        if (this.killed)
        {
            throw new IllegalStateException("Killed ResourceLoader can't supply resources.");
        }

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
        InstanceKiller.killOnShutdown(this);

        Map<String, Renderable> loadedRenderables;
        Map<String, SoundSupplier> loadedSounds;
        Map<String, File> loadedFiles;
        Map<String, Font> loadedFonts;
        Map<String, Object> loadedObjects;

        List<String> loadedClasses = new ArrayList<>();

        for (Loadable loadable : this.loadables)
        {
            if (loadedClasses.contains(loadable.getClass().getName()))
            {
                continue;
            }
            else
            {
                loadedClasses.add(loadable.getClass().getName());
            }
            
            // images
            loadedRenderables = loadable.loadRenderables(name);

            if (loadedRenderables != null)
            {
                for (String resourceKey : loadedRenderables.keySet())
                {
                    add(resourceKey, loadedRenderables.get(resourceKey));
                    Logger.global()
                            .print("Loaded renderable '" + resourceKey + "' for " + loadable.getClass().getName()
                                    + ".");
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

        this.killed = false;
    }

    /**
     * @see bt.game.resource.load.ResourceLoader#register(bt.game.resource.load.Loadable)
     */
    @Override
    public void register(Loadable loadable)
    {
        this.loadables.add(loadable);
    }

    /**
     * @see bt.game.resource.load.ResourceLoader#registerClosingOperation(java.lang.Runnable)
     */
    @Override
    public void registerClosingOperation(Runnable closingOp)
    {
        this.closingOpeartions.add(closingOp);
    }
}