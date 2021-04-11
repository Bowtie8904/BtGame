package bt.game.core.scene.impl;

import bt.game.core.container.abstr.GameContainer;
import bt.game.util.unit.Unit;
import bt.runtime.InstanceKiller;
import bt.utils.Exceptions;
import org.dyn4j.world.World;

import java.awt.*;

/**
 * A very simple scene displaying a black background with blinking dark grey / white loading bars in the middle.
 *
 * <p>
 * This can be used as a simple transition before a scene that requires a longer loading time for resources.
 * </p>
 *
 * @author &#8904
 */
public class LoadingScene extends BaseScene
{
    protected int highlight;
    protected int maxHighlightable = 10;
    protected double count;
    protected long timePerMove = 100;
    protected long minLoadingTime = 0;
    protected double currentLoadingTime = 0;
    protected Object lock = new Object();

    /**
     * Creates a new scene which moves the bar every 5 ticks.
     */
    public LoadingScene(GameContainer gameContainer)
    {
        super(gameContainer);
    }

    /**
     * Specifies the interval at which the highlighted bar will move. Default is 100ms.
     *
     * @return
     */
    public LoadingScene timePerMove(long timePerMoveMs)
    {
        this.timePerMove = timePerMoveMs;
        return this;
    }

    /**
     * Specifies a minimum loading time in milliseconds. This scene will not be closed before at least the given time has passed.
     *
     * @param minLoadingTimeMs
     * @return
     */
    public LoadingScene minLoadingTime(long minLoadingTimeMs)
    {
        this.minLoadingTime = minLoadingTimeMs;
        return this;
    }

    @Override
    public synchronized void kill()
    {
        // check if we can kill this scene yet if a minimum loading time has been set
        if (this.currentLoadingTime < this.minLoadingTime && !InstanceKiller.isActive())
        {
            Exceptions.ignoreThrow(() -> {
                synchronized (this.lock)
                {
                    this.lock.wait();
                }
            });
        }

        this.isLoaded = false;
        System.out.println("Killing loading scene.");

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

    /**
     * @see bt.game.core.scene.intf.Scene#tick()
     */
    @Override
    public void tick(double delta)
    {
        this.count += delta * 1000;
        this.currentLoadingTime += delta * 1000;

        if (this.currentLoadingTime >= this.minLoadingTime)
        {
            synchronized (this.lock)
            {
                this.lock.notifyAll();
            }
        }

        if (this.count > this.timePerMove)
        {
            this.highlight++;
            if (this.highlight == this.maxHighlightable)
            {
                this.highlight = 0;
            }
            this.count = 0;
        }
    }

    /**
     * @see bt.game.core.scene.intf.Scene#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0,
                   0,
                   (int)GameContainer.width().pixels(),
                   (int)GameContainer.height().pixels());

        Unit length = Unit.forUnits(GameContainer.width().units() / 27);
        Unit height = Unit.forUnits(length.units() / 1.8f);
        Unit y = Unit.forUnits(GameContainer.height().units() / 2);
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)(9 * length.pixels()),
                   (int)y.pixels(),
                   (int)(length.pixels() * 9),
                   (int)height.pixels());

        for (int i = 0; i < 9; i++)
        {
            if (this.highlight == i)
            {
                g.setColor(Color.WHITE);
                g.fillRect((int)((9 + i) * length.pixels()),
                           (int)y.pixels(),
                           (int)length.pixels(),
                           (int)height.pixels());
            }
        }
    }

    /**
     * @see bt.game.core.scene.intf.Scene#getWorld()
     */
    @Override
    public World getWorld()
    {
        return null;
    }

    /**
     * @see bt.game.core.scene.intf.Scene#refresh()
     */
    @Override
    public void refresh()
    {
        this.gameObjectHandler.refresh();
    }

    /**
     * @see bt.game.core.scene.impl.BaseScene#renderBackground(java.awt.Graphics2D)
     */
    @Override
    public void renderBackground(Graphics2D g, boolean debugRendering)
    {
    }

    @Override
    public void load()
    {

    }

    @Override
    public void setup()
    {

    }
}