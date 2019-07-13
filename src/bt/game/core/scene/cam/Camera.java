package bt.game.core.scene.cam;

import java.awt.Graphics2D;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

/**
 * A basic camera which translates the graphics context to its own position.
 * 
 * @author &#8904
 */
public class Camera implements Tickable
{
    /** The scene that this instance is used for. */
    protected Scene scene;

    /** The X position of this instance. */
    protected Unit x;

    /** The Y position of this instance. */
    protected Unit y;

    /**
     * Indicates that this camera should never move out of the bounds of the set scene. Indicated by the scenes
     * {@link Scene#getWidth() getWidth} and {@link Scene#getHeight() getHeight} methods.
     */
    protected boolean clipToBorder;
    /**
     * The currently active instance, used by the {@link MouseController} to fix position differences between mouse and
     * object. Scenes should manage this field.
     */
    public static Camera currentCamera;

    /**
     * Creates a new instance at the position 0|0.
     * 
     * @param scene
     */
    public Camera(Scene scene)
    {
        this.scene = scene;
        this.x = Unit.zero();
        this.y = Unit.zero();
    }

    /**
     * Sets whether this camera should never move out of the bounds of the set scene. Indicated by the scenes
     * {@link Scene#getWidth() getWidth} and {@link Scene#getHeight() getHeight} methods.
     * 
     * @param clip
     *            true to clip the camera to the bounds, false to let it move out of the scenes bounds.
     */
    public void clipToBorders(boolean clip)
    {
        this.clipToBorder = clip;
    }

    /**
     * Moves the camera to the given position.
     * 
     * <p>
     * If {@link #clipToBorders(boolean) clipToBorders} is set to true this method will correct its position immediately
     * if needed.
     * </p>
     * 
     * @param x
     * @param y
     */
    public void moveTo(Unit x, Unit y)
    {
        this.x = x;
        this.y = y;

        if (this.clipToBorder)
        {
            if (this.x.pixels() > this.scene.getWidth().pixels() - GameContainer.width().pixels())
            {
                this.x = Unit.forPixels(this.scene.getWidth().pixels() - GameContainer.width().pixels());
            }
            else if (this.x.pixels() < 0)
            {
                this.x = Unit.zero();
            }

            if (this.y.pixels() > this.scene.getHeight().pixels() - GameContainer.height().pixels())
            {
                this.y = Unit.forPixels(this.scene.getHeight().pixels() - GameContainer.height().pixels());
            }
            else if (this.y.pixels() < 0)
            {
                this.y = Unit.zero();
            }
        }
    }

    /**
     * Gets the X position of this instance.
     * 
     * @return
     */
    public Unit getX()
    {
        return this.x;
    }

    /**
     * Gets the Y position of this instance.
     * 
     * @return
     */
    public Unit getY()
    {
        return this.y;
    }

    /**
     * Translates the given graphics context to this instances x and y positions.
     * 
     * @param g
     */
    public void render(Graphics2D g)
    {
        g.translate((int)-this.x.pixels(),
                    (int)-this.y.pixels());
    }

    public void resetTranslation(Graphics2D g)
    {
        g.translate((int)this.x.pixels(),
                    (int)this.y.pixels());
    }

    /**
     * @see bt.game.core.obj.intf.Tickable#tick(double)
     */
    @Override
    public void tick(double delta)
    {
        moveTo(this.x,
               this.y);
    }
}