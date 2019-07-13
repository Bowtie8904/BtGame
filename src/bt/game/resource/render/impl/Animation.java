package bt.game.resource.render.impl;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import bt.game.core.obj.intf.Tickable;
import bt.game.resource.load.intf.ResourceLoader;
import bt.game.util.unit.Unit;

/**
 * A class to render an animation consisting of multiple {@link RenderableImages}. This class offers options to execute
 * actions at specific frames and at the end of the animation.
 * 
 * @author &#8904
 */
public class Animation extends AdvancedRenderable implements Tickable
{
    private ResourceLoader resourceLoader;
    private RenderableImage[] images;
    private String[] imageNames;
    private int currentIndex = -1;
    private double rotation;
    private Map<Integer, Runnable> onFrame;
    private boolean loop;
    private Runnable onEnd;
    private long interval;
    private long lastTime;
    private long time;

    /**
     * Creates a new animation.
     * 
     * <p>
     * This instance will use the resource loader of the given scene to load the images with the given names.
     * </p>
     * 
     * <p>
     * The animation will be played within the given time frame. <br>
     * <br>
     * 
     * <b>Example:</b><br>
     * An animation consisting of two images and with a set time of 1000 ms will switch images every 500 ms. So the
     * interval between images will be <i>time / n</i> where <i>n</i> is the number of images.
     * </p>
     * 
     * <p>
     * Before this animation can be used {@link #setup()} must be called.
     * </p>
     * 
     * @param resourceLoader
     *            The resource loader that is used to gather the images.
     * @param time
     *            The total time this animation takes in milliseconds.
     * @param images
     *            The names of the {@link RenderableImages} that are used in the mapping of the resource loader.
     */
    public Animation(ResourceLoader resourceLoader, long time, String... images)
    {
        if (images.length == 0)
        {
            throw new IllegalArgumentException("Must pass at least one image name.");
        }

        this.resourceLoader = resourceLoader;
        this.imageNames = images;
        this.onFrame = new HashMap<>();
        this.time = time;
    }

    /**
     * Gathers the images from the recource loader of the set scene, calculates the interval between images based on the
     * number of gathered {@link RenderableImage}s and resets the timer and image index.
     */
    public void setup()
    {
        this.images = Arrays.stream(this.imageNames)
                            .map(this.resourceLoader::getRenderable)
                            .filter(RenderableImage.class::isInstance)
                            .map(RenderableImage.class::cast)
                            .toArray(RenderableImage[]::new);

        if (this.images.length != this.imageNames.length)
        {
            throw new IllegalArgumentException(
                                               "Unable to receive enough RenderableImages from the resource loader. Some image names are not mapped to instances of RenderableImage.");
        }

        this.interval = this.time / this.images.length;
        this.lastTime = 0;
        this.currentIndex = -1;
    }

    /**
     * Sets the total time this animation takes in milliseconds.
     * 
     * <p>
     * This will recalculate the interval between images. This method should not be called before {@link #setup()}.
     * </p>
     * 
     * @param time
     */
    public void setTime(long time)
    {
        this.time = time;
        this.interval = time / this.images.length;
    }

    /**
     * Gets the total time this animation takes in milliseconds.
     * 
     * @return
     */
    public long getTime()
    {
        return this.time;
    }

    /**
     * Sets the rotation angle at which this animation will be drawn.
     * 
     * @param rotation
     */
    public void setRotation(double rotation)
    {
        this.rotation = rotation;
    }

    /**
     * Gets the rotation angle at which this animation will be drawn.
     * 
     * @return
     */
    public double getRotation()
    {
        return this.rotation;
    }

    /**
     * Sets whether this animation will play in a loop or just once.
     * 
     * @param loop
     */
    public void setLoop(boolean loop)
    {
        this.loop = loop;
    }

    /**
     * Sets an action that will be executed at the specified frame.
     * 
     * <p>
     * Newly set actions will overwrite the old one for the specified frame if it exists.
     * </p>
     * 
     * @param frame
     *            The zero index based frame.
     * @param action
     *            The action to execute.
     */
    public void onFrame(int frame, Runnable action)
    {
        this.onFrame.put(frame,
                         action);
    }

    /**
     * Defines an action that is executed when a non looping animation ends (after the last frame).
     * 
     * @param action
     */
    public void onEnd(Runnable action)
    {
        this.onEnd = action;
    }

    /**
     * Switches images at the caluclated interval and executes actions based on the frame index.
     * 
     * <p>
     * If a non looping animation has played all images it will trigger the {@link #onEnd} action if any is specified.
     * </p>
     * 
     * @see bt.game.core.obj.intf.Tickable#tick()
     */
    @Override
    public void tick(double delta)
    {
        if (this.currentIndex < this.images.length && System.currentTimeMillis() - this.lastTime >= this.interval
            || this.lastTime == 0)
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
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit,
     *      bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        if (this.currentIndex >= 0 && this.currentIndex < this.images.length)
        {
            this.images[this.currentIndex].render(g,
                                                  x,
                                                  y,
                                                  w,
                                                  h,
                                                  this.rotation);
        }
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g)
    {
        if (this.currentIndex >= 0 && this.currentIndex < this.images.length)
        {
            this.images[this.currentIndex].render(g,
                                                  this.rotation);
        }
    }

    /**
     * Resets the animation, meaning that it will start from the begining again.
     */
    public void reset()
    {
        this.currentIndex = 0;
    }

    /**
     * Gets the set image names.
     * 
     * @return the imageNames
     */
    public String[] getImageNames()
    {
        return this.imageNames;
    }
}