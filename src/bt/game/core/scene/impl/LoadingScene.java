package bt.game.core.scene.impl;

import java.awt.Color;
import java.awt.Graphics;

import bt.game.core.container.GameContainer;
import bt.game.util.unit.Unit;

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
    private int count;
    private int ticks;

    /**
     * Creates a new scene.
     * 
     * @param ticksPerMove
     *            Specifies the interval at which the highlighted bar will move. Default is 5, which means that the bar
     *            will be moved every 5 ticks.
     */
    public LoadingScene(int ticksPerMove)
    {
        this.ticks = ticksPerMove;
    }

    /**
     * Creates a new scene which moves the bar every 5 ticks.
     */
    public LoadingScene()
    {
        this.ticks = 5;
    }

    /**
     * @see bt.game.core.scene.Scene#tick()
     */
    @Override
    public void tick()
    {
        this.count ++ ;

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
     * @see bt.game.core.scene.Scene#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, (int)GameContainer.width().pixels(), (int)GameContainer.height().pixels());

        Unit length = Unit.forUnits(GameContainer.width().units() / 27);
        Unit height = Unit.forUnits(length.units() / 1.8f);
        Unit y = Unit.forUnits(GameContainer.height().units() / 2);
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int)(9 * length.pixels()), (int)y.pixels(), (int)(length.pixels() * 9), (int)height.pixels());

        for (int i = 0; i < 9; i ++ )
        {
            if (this.highlight == i)
            {
                g.setColor(Color.WHITE);
                g.fillRect((int)((9 + i) * length.pixels()), (int)y.pixels(), (int)length.pixels(),
                        (int)height.pixels());
            }
        }
    }

    /**
     * @see bt.game.core.scene.Scene#start()
     */
    @Override
    public void start()
    {
    }
}