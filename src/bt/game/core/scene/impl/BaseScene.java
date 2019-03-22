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

    public BaseScene()
    {
        this.resourceLoader = new BaseResourceLoader();
    }

    /**
     * @see bt.game.core.scene.Scene#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 2); // higher priority than resource loaders to avoid
                                                                    // killing them twice
        this.resourceLoader.load(name);
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Killing scene.");

        if (!InstanceKiller.isActive())
        {
            this.resourceLoader.kill();
            InstanceKiller.unregister(this.resourceLoader);
            InstanceKiller.unregister(this);
        }
    }
}