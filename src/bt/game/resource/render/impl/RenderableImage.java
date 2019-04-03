package bt.game.resource.render.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;
import bt.runtime.Killable;

/**
 * 
 * 
 * @author &#8904
 */
public class RenderableImage implements Renderable, Killable
{
    protected Image image;

    public RenderableImage(Image image)
    {
        this.image = image;
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h.
     * 
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h)
    {
        render(g, x, y, w, h, 0);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The image is rotated clockwise
     * around its middle point by the given rotation. The given Graphics object is used to create a {@link Graphics2D}
     * copy, so that rotation actions can be performed.
     * 
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param rotation
     *            The rotation of the image in degrees.
     */
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h, double rotation)
    {
        if (rotation == 0 || rotation % 360 == 0)
        {
            g.drawImage(this.image, (int)x.pixels(), (int)y.pixels(), (int)w.pixels(), (int)h.pixels(), null);
        }
        else
        {
            Graphics2D g2 = (Graphics2D)g.create();

            g2.rotate(Math.toRadians(rotation),
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

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g)
    {
        render(g,
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(this.image.getWidth(null)),
                Unit.forUnits(this.image.getHeight(null)),
                0);
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