package bt.game.core.scene.impl;

import java.awt.Graphics;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.hand.GameObjectHandler;
import bt.game.core.obj.hand.impl.BaseGameObjectHandler;
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
    protected GameObjectHandler gameObjectHandler;
    protected GameContainer gameContainer;
    protected boolean isLoaded;
    protected String name;

    public BaseScene(GameContainer gameContainer, ResourceLoader resourceLoader)
    {
        this.gameContainer = gameContainer;

        if (resourceLoader == null)
        {
            this.resourceLoader = new BaseResourceLoader();
        }
        else
        {
            this.resourceLoader = resourceLoader;
        }

        this.gameObjectHandler = new BaseGameObjectHandler();
    }

    public BaseScene(GameContainer gameContainer)
    {
        this(gameContainer, null);
    }

    @Override
    public GameObjectHandler getGameObjectHandler()
    {
        return this.gameObjectHandler;
    }

    @Override
    public ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    @Override
    public GameContainer getGameContainer()
    {
        return this.gameContainer;
    }

    /**
     * @see bt.game.core.scene.Scene#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        this.isLoaded = false;
        this.gameObjectHandler.init();
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 2);
        this.name = name;
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
    public synchronized void kill()
    {
        this.isLoaded = false;
        Logger.global().print(this.name == null ? "Killing scene." : "Killing scene '" + this.name + "'.");

        // kill resource loader if instance killer is not already doing it or if the loader is not registered for
        // termination at all
        if (!InstanceKiller.isActive() || !InstanceKiller.isRegistered(this.resourceLoader))
        {
            this.resourceLoader.kill();
            InstanceKiller.unregister(this.resourceLoader);
            InstanceKiller.unregister(this);
        }

        // kill game object handler if instance killer is not already doing it or if the handler is not registered for
        // termination at all
        if (!InstanceKiller.isActive() || !InstanceKiller.isRegistered(this.gameObjectHandler))
        {
            this.gameObjectHandler.kill();
            InstanceKiller.unregister(this.gameObjectHandler);
            InstanceKiller.unregister(this);
        }
    }

    @Override
    public synchronized void tick()
    {
        this.gameObjectHandler.tick();
    }

    @Override
    public synchronized void render(Graphics g)
    {
        this.gameObjectHandler.render(g);
    }
}