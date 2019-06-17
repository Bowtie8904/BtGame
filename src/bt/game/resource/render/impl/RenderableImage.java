package bt.game.resource.render.impl;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;
import bt.runtime.Killable;
import bt.utils.num.NumberUtils;

/**
 * 
 * 
 * @author &#8904
 */
public class RenderableImage implements Renderable, Killable
{
    protected Image image;
    protected Image scaledImage;
    protected float alpha;
    protected AffineTransform transform;
    protected Unit lastWidth;
    protected Unit lastHeight;
    protected double lastUnitRatio = Unit.getRatio();

    public RenderableImage(Image image)
    {
        this.image = image;
        this.transform = new AffineTransform();
        this.alpha = 1f;
    }

    /**
     * Sets the alhpa value for this image.
     * 
     * @param alpha
     *            A value between 0 and 1.
     */
    public void setAlpha(float alpha)
    {
        this.alpha = NumberUtils.clamp(alpha, 0, 1);
    }

    /**
     * Gets the alpha value for this image.
     * 
     * @return
     */
    public float getAlpha()
    {
        return this.alpha;
    }

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g)
    {
        render(g,
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(this.image.getWidth(null)),
                Unit.forUnits(this.image.getHeight(null)),
                0);
    }

    public void render(Graphics2D g, double rotation)
    {
        render(g,
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(this.image.getWidth(null)),
                Unit.forUnits(this.image.getHeight(null)),
                rotation);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h.
     * 
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
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
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation)
    {
        AffineTransform origTransform = g.getTransform();
        Composite origComposite = g.getComposite();

        this.transform.setToRotation(Math.toRadians(rotation),
                x.pixels() + w.pixels() / 2,
                y.pixels() + h.pixels() / 2);
        g.transform(this.transform);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));

        if (!w.equals(this.lastWidth) || !h.equals(this.lastHeight) || this.lastUnitRatio != Unit.getRatio())
        {
            this.lastUnitRatio = Unit.getRatio();
            this.scaledImage = this.image.getScaledInstance((int)w.pixels(), (int)h.pixels(), Image.SCALE_SMOOTH);
            this.lastHeight = h;
            this.lastWidth = w;
        }

        g.drawImage(this.scaledImage, (int)x.pixels(), (int)y.pixels(), null);

        this.transform.setToRotation(0);
        g.setComposite(origComposite);
        g.setTransform(origTransform);
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
     * @see bt.game.resource.render.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return Unit.forUnits(0);
    }
}