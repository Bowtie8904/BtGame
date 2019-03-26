package bt.game.resource.render.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;

/**
 * 
 * 
 * @author &#8904
 */
public class RenderableImage implements Renderable
{
    protected Image image;
    protected double rotationAngle;

    public RenderableImage(Image image)
    {
        this.image = image;
    }

    /**
     * Sets the angle by which this text will be rotated.
     * 
     * @param rotationAngle
     */
    public void setRotation(double rotationAngle)
    {
        this.rotationAngle = rotationAngle;
    }

    public double getRotation()
    {
        return this.rotationAngle;
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The given Graphics object is used
     * to create a {@link Graphics2D} copy, so that rotation actions can be performed.
     * 
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h)
    {
        if (this.rotationAngle == 0 || this.rotationAngle % 360 == 0)
        {
            g.drawImage(this.image, (int)x.pixels(), (int)y.pixels(), (int)w.pixels(), (int)h.pixels(), null);
        }
        else
        {
            Graphics2D g2 = (Graphics2D)g.create();

            g2.rotate(Math.toRadians(this.rotationAngle),
                    x.pixels() + w.pixels() / 2,
                    y.pixels() + h.pixels() / 2);

            g2.drawImage(this.image, (int)x.pixels(), (int)y.pixels(), (int)w.pixels(), (int)h.pixels(), null);
            g2.dispose();
        }
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        this.image.flush();
    }
}