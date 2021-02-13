package bt.game.resource.render.impl.anim;

import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.load.intf.ResourceLoader;
import bt.game.resource.render.impl.BaseRenderable;
import bt.game.resource.render.impl.RenderableImage;
import bt.game.util.unit.Unit;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to render an animation consisting of multiple {@link RenderableImages}. This class offers options to execute
 * actions at specific frames and at the end of the animation.
 *
 * @author &#8904
 */
public class Animation extends BaseRenderable implements Tickable
{
    private ResourceLoader resourceLoader;
    private RenderableImage[] images;
    private String[] imageNames;
    private int currentIndex = -1;
    private double rotation;
    private double rotationGain = 0;
    private Map<Integer, Runnable> onFrame;
    private boolean loop;
    private Runnable onEnd;
    private long interval;
    private long lastTime;
    private long time;
    private Unit rotationOffsetX = Unit.zero();
    private Unit rotationOffsetY = Unit.zero();
    private boolean imageChanged;
    private ImageEmitter imageEmitter;

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
     * @param resourceLoader The resource loader that is used to gather the images.
     * @param time           The total time this animation takes in milliseconds.
     * @param images         The names of the {@link RenderableImages} that are used in the mapping of the resource loader.
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
     * <p>
     * This method needs to be the last call to finish the setup for the animation. No further initial settigns
     * should be changed after this.
     */
    public void setup(Scene scene)
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

        if (this.imageEmitter != null)
        {
            this.imageEmitter.registerToScene(scene);
        }
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
     * Sets a rotation gain in degrees. This amount will be added to
     * the rotation of this animation each second. A negtive amount
     * can be passed to reverse the rotation.
     * An amount above 360 or below -360 can be passed to do more
     * than a full rotation in one second.
     *
     * @param rotationGain
     */
    public void setRotationGain(double rotationGain)
    {
        this.rotationGain = rotationGain;
    }

    /**
     * Sets the amount of alpha that the images should lose each second.
     * <p>
     * This value can be higher than the maximum amount of alpha (1) to
     * indicate that the image should fade completely in less than a second.
     * For example an alphaLoss value of 5 would mean that an immage would fade out over 200ms.
     * <p>
     * Set this value to -1 to disable slow fading. Images will then disappear once the next
     * image is being rendered (default).
     * <p>
     * Set this value to 0 to render images without any fading. Images will be rendered indefinetely.
     * <p>
     * Images will keep the rotation that was set to this animation at the time of their first appearence.
     *
     * @param alphaLoss
     */
    public void setAlphaLoss(double alphaLoss)
    {
        if (alphaLoss == -1)
        {
            this.imageEmitter = null;
        }
        else
        {
            this.imageEmitter = new ImageEmitter(alphaLoss, this.z);
        }
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
     * @param frame  The zero index based frame.
     * @param action The action to execute.
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

    protected void calculateNewRotation(double delta)
    {
        this.rotation += this.rotationGain * delta;

        if (this.rotation > 360)
        {
            this.rotation = 0;
        }
        else if (this.rotation < 0)
        {
            this.rotation = 360;
        }
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
        if (this.imageEmitter != null)
        {
            this.imageEmitter.tick(delta);
        }

        calculateNewRotation(delta);

        if (this.currentIndex < this.images.length && System.currentTimeMillis() - this.lastTime >= this.interval
                || this.lastTime == 0)
        {
            this.lastTime = System.currentTimeMillis();

            this.currentIndex++;
            this.imageChanged = true;

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
     * bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        if (this.currentIndex >= 0 && this.currentIndex < this.images.length)
        {
            this.images[this.currentIndex].render(g,
                                                  x,
                                                  y,
                                                  w,
                                                  h,
                                                  this.rotation,
                                                  this.rotationOffsetX,
                                                  this.rotationOffsetY,
                                                  debugRendering);

            if (this.imageChanged && this.imageEmitter != null)
            {
                this.imageChanged = false;
                this.imageEmitter.setZ(this.z);
                this.imageEmitter.emit(new EmitterImage(this.images[this.currentIndex],
                                                        x,
                                                        y,
                                                        w,
                                                        h,
                                                        this.rotation,
                                                        this.rotationOffsetX,
                                                        this.rotationOffsetY));
            }
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

    public Unit getRotationOffsetX()
    {
        return this.rotationOffsetX;
    }

    public void setRotationOffsetX(Unit rotationOffsetX)
    {
        this.rotationOffsetX = rotationOffsetX;
    }

    public Unit getRotationOffsetY()
    {
        return this.rotationOffsetY;
    }

    public void setRotationOffsetY(Unit rotationOffsetY)
    {
        this.rotationOffsetY = rotationOffsetY;
    }
}