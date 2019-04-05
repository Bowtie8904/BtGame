package bt.game.resource.render.impl;

import java.awt.Graphics;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.Scene;
import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class Animation implements Renderable, Tickable
{
    private Scene scene;
    private RenderableImage[] images;
    private String[] imageNames;
    private int currentIndex = -1;
    private double rotation;
    private Map<Integer, Runnable> onFrame;
    private boolean loop;
    private Runnable onEnd;
    private long interval;
    private long lastTime;

    public Animation(Scene scene, long time, String... images)
    {
        if (images.length == 0)
        {
            throw new IllegalArgumentException("Must pass at least one image name.");
        }

        this.scene = scene;
        this.imageNames = images;
        this.onFrame = new HashMap<>();
        this.interval = time / images.length;
    }

    public void setup()
    {
        this.images = Arrays.stream(this.imageNames)
                .map(this.scene.getResourceLoader()::getRenderable)
                .filter(RenderableImage.class::isInstance)
                .map(RenderableImage.class::cast)
                .toArray(RenderableImage[]::new);

        if (this.images.length == 0)
        {
            throw new IllegalArgumentException(
                    "Unable to receive enough RenderableImages from the resource loader. Some image names are not mapped to instances of RenderableImage.");
        }

        this.lastTime = 0;
        this.currentIndex = -1;
    }

    public void setTime(long time)
    {
        this.interval = time / this.images.length;
    }

    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    public void setLoop(boolean loop)
    {
        this.loop = loop;
    }

    public void onFrame(int index, Runnable action)
    {
        this.onFrame.put(index, action);
    }

    /**
     * @see bt.game.core.obj.intf.Tickable#tick()
     */
    @Override
    public void tick()
    {
        if (System.currentTimeMillis() - this.lastTime >= this.interval || this.lastTime == 0)
        {
            this.lastTime = System.currentTimeMillis();
            this.currentIndex ++ ;

            if (this.currentIndex >= this.images.length)
            {
                if (this.loop)
                {
                    this.currentIndex = 0;
                }
                else if (this.onEnd != null)
                {
                    this.onEnd.run();
                    return;
                }
            }

            Runnable action = this.onFrame.get(this.currentIndex);

            if (action != null)
            {
                action.run();
            }
        }
    }

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit,
     *      bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h)
    {
        if (this.currentIndex >= 0)
        {
            this.images[this.currentIndex].render(g, x, y, w, h, this.rotation);
        }
    }

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g)
    {
        if (this.currentIndex >= 0)
        {
            this.images[this.currentIndex].render(g, this.rotation);
        }
    }

    /**
     * @see bt.game.resource.render.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return Unit.forUnits(0);
    }
}