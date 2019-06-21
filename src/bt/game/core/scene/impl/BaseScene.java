package bt.game.core.scene.impl;

import java.awt.Graphics2D;

import org.dyn4j.dynamics.World;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.hand.ObjectHandler;
import bt.game.core.obj.hand.impl.BaseObjectHandler;
import bt.game.core.scene.Scene;
import bt.game.core.scene.cam.Camera;
import bt.game.resource.load.ResourceLoader;
import bt.game.resource.load.impl.BaseResourceLoader;
import bt.game.util.unit.Unit;
import bt.runtime.InstanceKiller;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public abstract class BaseScene implements Scene
{
    protected ResourceLoader resourceLoader;
    protected ObjectHandler gameObjectHandler;
    protected GameContainer gameContainer;
    protected boolean isLoaded;
    protected String name;
    protected World world;

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

        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.getSettings().setStepFrequency(1 / 500.0);
        this.world.getSettings().setPositionConstraintSolverIterations(70);
        this.world.getSettings().setMaximumTranslation(Integer.MAX_VALUE);

        this.gameObjectHandler = new BaseObjectHandler(this);
    }

    public BaseScene(GameContainer gameContainer)
    {
        this(gameContainer, null);
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

    /**
     * @see bt.game.core.scene.Scene#getObjectHandler()
     */
    @Override
    public ObjectHandler getObjectHandler()
    {
        return this.gameObjectHandler;
    }

    /**
     * @see bt.game.core.scene.Scene#getResourceLoader()
     */
    @Override
    public ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    /**
     * @see bt.game.core.scene.Scene#getGameContainer()
     */
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
        this.resourceLoader.finishLoad();
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
     * @see bt.game.core.scene.Scene#refresh()
     */
    @Override
    public void refresh()
    {
        this.gameObjectHandler.refresh();
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

        this.world.removeAllBodiesAndJoints();
        this.world.removeAllListeners();
        Camera.currentCamera = null;
    }

    /**
     * @see bt.game.core.scene.Scene#tick()
     */
    @Override
    public synchronized void tick(double delta)
    {
        if (this.isLoaded)
        {
            this.gameObjectHandler.tick(delta);
            this.world.update(delta);

            if (Camera.currentCamera != null)
            {
                Camera.currentCamera.tick(delta);
            }
        }
    }

    /**
     * @see bt.game.core.scene.Scene#render(java.awt.Graphics)
     */
    @Override
    public synchronized void render(Graphics2D g)
    {
        if (this.isLoaded)
        {
            if (Camera.currentCamera != null)
            {
                Camera.currentCamera.render(g);
            }

            renderBackground(g);

            this.gameObjectHandler.render(g);
        }
    }

    /**
     * @see bt.game.core.scene.Scene#getWidth()
     */
    @Override
    public Unit getWidth()
    {
        return GameContainer.width();
    }

    /**
     * @see bt.game.core.scene.Scene#getHeight()
     */
    @Override
    public Unit getHeight()
    {
        return GameContainer.height();
    }

    /**
     * Supposed to render the background.
     * 
     * <p>
     * Called from inside the {@link #render(Graphics2D)} after the camera is translated.
     * </p>
     * 
     * @param g
     */
    public abstract void renderBackground(Graphics2D g);
}