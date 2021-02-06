package bt.game.core.scene.impl;

import java.awt.Color;
import java.awt.Graphics2D;

import org.dyn4j.dynamics.World;

import bt.game.core.container.abstr.GameContainer;
import bt.game.util.unit.Unit;
import bt.log.Logger;
import bt.runtime.InstanceKiller;

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
    private int highlight;
    private double count;
    private int ticks;

    /**
     * Creates a new scene.
     *
     * @param ticksPerMove
     *            Specifies the interval at which the highlighted bar will move. Default is 5, which means that the bar
     *            will be moved every 5 ticks.
     */
    public LoadingScene(GameContainer gameContainer, int ticksPerMove)
    {
        super(gameContainer);
        this.ticks = ticksPerMove;
    }

    /**
     * Creates a new scene which moves the bar every 5 ticks.
     */
    public LoadingScene(GameContainer gameContainer)
    {
        this(gameContainer,
             5);
    }

    @Override
    public synchronized void kill()
    {
        this.isLoaded = false;
        Logger.global().print("Killing loading scene.");

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
        this.count += delta;

        if (this.count > this.ticks)
        {
            this.highlight ++ ;
            if (this.highlight == 10)
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
    public void render(Graphics2D g)
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

        for (int i = 0; i < 9; i ++ )
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
    public void renderBackground(Graphics2D g)
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