package bt.game.core.scene.cam;

import java.awt.Graphics;

import bt.game.core.container.GameContainer;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class Camera
{
    protected Scene scene;
    protected Unit x;
    protected Unit y;
    protected boolean clipToBorder;
    public static Camera currentCamera;

    public Camera(Scene scene)
    {
        this.scene = scene;
        this.x = Unit.zero();
        this.y = Unit.zero();
    }

    public void clipToBorders(boolean clip)
    {
        this.clipToBorder = clip;
    }

    public void moveTo(Unit x, Unit y)
    {
        this.x = x;
        this.y = y;

        if (this.clipToBorder)
        {
            if (this.x.pixels() > this.scene.getWidth().pixels() - GameContainer.width().pixels())
            {

            }
        }
    }

    public Unit getX()
    {
        return this.x;
    }

    public Unit getY()
    {
        return this.y;
    }

    public void render(Graphics g)
    {
        g.translate((int)-this.x.pixels(), (int)-this.y.pixels());
    }
}