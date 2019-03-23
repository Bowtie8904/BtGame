package bt.game.core.scene.impl;

import bt.game.core.scene.Scene;
import bt.game.resource.load.ResourceLoader;
import bt.game.resource.load.impl.BaseResourceLoader;
import bt.runtime.InstanceKiller;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public abstract class BaseScene implements Scene
{
    protected ResourceLoader resourceLoader;
    protected boolean isLoaded;

    public BaseScene(ResourceLoader resourceLoader)
    {
        if (resourceLoader == null)
        {
            this.resourceLoader = new BaseResourceLoader();
        }
        else
        {
            this.resourceLoader = resourceLoader;
        }

    }

    public BaseScene()
    {
        this(null);
    }

    /**
     * @see bt.game.core.scene.Scene#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        this.isLoaded = false;
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 2);
        this.resourceLoader.load(name);
        this.isLoaded = true;
    }

    /**
     * @see bt.game.core.scene.Scene#isLoaded()
     */
    @Override
    public boolean isLoaded()
    {
        return this.isLoaded;
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        this.isLoaded = false;
        Logger.global().print("Killing scene.");

        // kill resource loader if instance killer is not already doing it or if the loader is not registered for
        // termination at all
        if (!InstanceKiller.isActive() || !InstanceKiller.isRegistered(this.resourceLoader))
        {
            this.resourceLoader.kill();
            InstanceKiller.unregister(this.resourceLoader);
            InstanceKiller.unregister(this);
        }
    }
}