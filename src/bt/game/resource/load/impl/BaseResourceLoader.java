package bt.game.resource.load.impl;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.Clip;

import bt.game.resource.load.ResourceLoader;
import bt.game.resource.type.Sound;
import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public abstract class BaseResourceLoader implements ResourceLoader, Killable
{
    protected Map<String, BufferedImage> images;
    protected Map<String, Sound> sounds;
    protected Map<String, File> files;
    protected Map<String, Font> fonts;
    protected Map<String, Object> objects;

    public BaseResourceLoader()
    {
        this.images = new HashMap<>();
        this.sounds = new HashMap<>();
        this.files = new HashMap<>();
        this.fonts = new HashMap<>();
        this.objects = new HashMap<>();
        InstanceKiller.killOnShutdown(this);
    }

    /**
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
        }
    }

    /**
     * @see bt.game.resource.ResourceLoader#getImage(java.lang.String)
     */
    @Override
    public BufferedImage getImage(String resourceName)
    {
        return this.images.get(resourceName);
    }

    /**
     * @see bt.game.resource.ResourceLoader#getSound(java.lang.String)
     */
    @Override
    public Clip getSound(String resourceName)
    {
        return this.sounds.get(resourceName).getClip();
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFile(java.lang.String)
     */
    @Override
    public File getFile(String resourceName)
    {
        return this.files.get(resourceName);
    }

    /**
     * @see bt.game.resource.ResourceLoader#getFont(java.lang.String)
     */
    @Override
    public Font getFont(String resourceName)
    {
        return this.fonts.get(resourceName);
    }

    /**
     * @see bt.game.resource.ResourceLoader#get(java.lang.String)
     */
    @Override
    public Object get(String resourceName)
    {
        return this.objects.get(resourceName);
    }
}