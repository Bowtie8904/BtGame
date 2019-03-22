package bt.game.core.scene;

import bt.game.resource.load.ResourceLoader;
import bt.game.resource.load.impl.BaseResourceLoader;
import bt.runtime.InstanceKiller;

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
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        this.resourceLoader.kill();
        InstanceKiller.unregister(this.resourceLoader);
        InstanceKiller.unregister(this);
    }
}