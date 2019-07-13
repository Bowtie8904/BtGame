package bt.game.core.scene.impl;

import java.awt.Graphics2D;

import org.dyn4j.dynamics.World;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.obj.hand.impl.BaseObjectHandler;
import bt.game.core.obj.hand.intf.ObjectHandler;
import bt.game.core.scene.cam.Camera;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.load.impl.BaseResourceLoader;
import bt.game.resource.load.impl.BaseTextLoader;
import bt.game.resource.load.intf.ResourceLoader;
import bt.game.resource.load.intf.TextLoader;
import bt.game.util.unit.Unit;
import bt.runtime.InstanceKiller;
import bt.utils.log.Logger;

/**
 * @author &#8904
 */
public abstract class BaseScene implements Scene
{
    protected ResourceLoader resourceLoader;
    protected TextLoader textLoader;
    protected ObjectHandler gameObjectHandler;
    protected GameContainer gameContainer;
    protected boolean isLoaded;
    protected String name;
    protected World world;
    protected Camera camera;

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

        this.textLoader = new BaseTextLoader();

        this.world = new World();
        this.world.setGravity(World.ZERO_GRAVITY);
        this.world.getSettings().setStepFrequency(1 / 500.0);
        this.world.getSettings().setPositionConstraintSolverIterations(70);
        this.world.getSettings().setMaximumTranslation(Integer.MAX_VALUE);

        this.gameObjectHandler = new BaseObjectHandler(this);
    }

    public BaseScene(GameContainer gameContainer)
    {
        this(gameContainer,
             null);
    }

    @Override
    public World getWorld()
    {
        return this.world;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getObjectHandler()
     */
    @Override
    public ObjectHandler getObjectHandler()
    {
        return this.gameObjectHandler;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getResourceLoader()
     */
    @Override
    public ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getTextLoader()
     */
    @Override
    public TextLoader getTextLoader()
    {
        return this.textLoader;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getGameContainer()
     */
    @Override
    public GameContainer getGameContainer()
    {
        return this.gameContainer;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        this.isLoaded = false;
        this.gameObjectHandler.init();
        InstanceKiller.killOnShutdown(this,
                                      Integer.MIN_VALUE + 2);
        this.name = name;
        loadTextLoader(name);
        load();
        loadResourceLoader(name);
        this.isLoaded = true;
    }

    protected void loadTextLoader(String name)
    {
        this.textLoader.load(name);
    }

    protected void loadResourceLoader(String name)
    {
        this.resourceLoader.load(name);
        this.resourceLoader.finishLoad();
    }

    /**
     * @see bt.game.core.scene.intf.Scene#isLoaded()
     */
    @Override
    public boolean isLoaded()
    {
        return this.isLoaded;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#refresh()
     */
    @Override
    public void refresh()
    {
        if (this.textLoader.getLoadMode() == TextLoader.LAZY_LOADING)
        {
            this.textLoader.load(this.name);
        }

        this.gameObjectHandler.refresh();
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
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

        this.textLoader.kill();

        this.world.removeAllBodiesAndJoints();
        this.world.removeAllListeners();

        if (Camera.currentCamera != null && Camera.currentCamera.equals(this.camera))
        {
            Camera.currentCamera = null;
        }
    }

    /**
     * @see bt.game.core.scene.intf.Scene#tick()
     */
    @Override
    public void tick(double delta)
    {
        if (this.isLoaded)
        {
            this.gameObjectHandler.tick(delta);
            this.world.update(delta);

            if (this.camera != null)
            {
                this.camera.tick(delta);
            }
        }
    }

    /**
     * @see bt.game.core.scene.intf.Scene#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g)
    {
        if (this.isLoaded)
        {
            if (this.camera != null)
            {
                this.camera.render(g);
            }

            renderBackground(g);

            this.gameObjectHandler.render(g);
        }
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getWidth()
     */
    @Override
    public Unit getWidth()
    {
        return GameContainer.width();
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getHeight()
     */
    @Override
    public Unit getHeight()
    {
        return GameContainer.height();
    }

    @Override
    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    @Override
    public void start()
    {
        Camera.currentCamera = this.camera;
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

    /**
     * Supposed to setup/register additional resources.
     * 
     * <p>
     * This method is called from inside the {@link #load(String)} method after {@link #loadTextLoader(String)} and
     * before {@link #loadResourceLoader(String)}.
     * </p>
     */
    public abstract void load();
}