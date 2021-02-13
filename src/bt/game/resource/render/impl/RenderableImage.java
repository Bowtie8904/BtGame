package bt.game.resource.render.impl;

import bt.game.resource.render.intf.Renderable;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import bt.types.Killable;
import bt.utils.NumberUtils;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
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
    protected Unit z;
    protected boolean shouldRender;

    public RenderableImage(Image image)
    {
        this.image = image;
        this.scaledImage = image;
        this.transform = new AffineTransform();
        this.alpha = 1f;
        this.z = Unit.zero();
        this.shouldRender = true;
    }

    /**
     * Sets the alhpa value for this image.
     *
     * @param alpha A value between 0 and 1.
     */
    public void setAlpha(float alpha)
    {
        this.alpha = NumberUtils.clamp(alpha,
                                       0,
                                       1);
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
     * Returns a cropped version of the underlying image wrapped in a new {@link RenderableImage} instance.
     *
     * <p>
     * The returned RenderableImage copies the Z, alpha values and the {@link #shouldRender()} settting. It also shares
     * the same image data as this instance, which means that changes to the underlying image can affect both
     * RenderabelImages.
     * </p>
     *
     * @param x The x pixel position of the cropped area on the original underlying image.
     * @param y The y pixel position of the cropped area on the original underlying image.
     * @param w The width in pixels of the cropped area on the original underlying image.
     * @param h The height in pixels of the cropped area on the original underlying image.
     * @return The cropped RenderableImage if possible.
     * @throws UnsupportedOperationException if the underlying image is not an instance of {@link BufferedImage}.
     */
    public RenderableImage crop(int x, int y, int w, int h)
    {
        if (this.image instanceof BufferedImage)
        {
            RenderableImage croppedImage = new RenderableImage(((BufferedImage)this.image).getSubimage(x,
                                                                                                       y,
                                                                                                       w,
                                                                                                       h));
            croppedImage.setZ(this.z);
            croppedImage.setAlpha(this.alpha);
            croppedImage.shouldRender(this.shouldRender);
            return croppedImage;
        }
        else
        {
            throw new UnsupportedOperationException("Only instances of BufferedImage can be cropped.");
        }
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
        render(g,
               Unit.zero(),
               Unit.zero(),
               Unit.forUnits(this.image.getWidth(null)),
               Unit.forUnits(this.image.getHeight(null)),
               0,
               Unit.zero(),
               Unit.zero(),
               debugRendering);
    }

    public void render(Graphics2D g)
    {
        render(g, false);
    }

    public void render(Graphics2D g, double rotation)
    {
        render(g, rotation, false);
    }

    public void render(Graphics2D g, double rotation, boolean debugRendering)
    {
        render(g,
               Unit.zero(),
               Unit.zero(),
               Unit.forUnits(this.image.getWidth(null)),
               Unit.forUnits(this.image.getHeight(null)),
               rotation,
               Unit.zero(),
               Unit.zero(),
               debugRendering);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h.
     *
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit,
     * bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        render(g,
               x,
               y,
               w,
               h,
               0,
               Unit.zero(),
               Unit.zero(),
               debugRendering);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The image is rotated clockwise
     * around its middle point by the given rotation. The given Graphics object is used to create a {@link Graphics2D}
     * copy, so that rotation actions can be performed.
     */
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation)
    {
        render(g, x, y, w, h, rotation, Unit.zero(), Unit.zero(), false);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The image is rotated clockwise
     * around its middle point by the given rotation. The given offset values are applied to that middle point to alter the rotation origin.
     * The given Graphics object is used to create a {@link Graphics2D} copy, so that rotation actions can be performed.
     *
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param rotation
     * @param rotationOffsetX
     * @param rotationOffsetY
     */
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        render(g, x, y, w, h, rotation, rotationOffsetX, rotationOffsetY, false);
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The image is rotated clockwise
     * around its middle point by the given rotation. The given offset values are applied to that middle point to alter the rotation origin.
     * The given Graphics object is used to create a {@link Graphics2D} copy, so that rotation actions can be performed.
     *
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     * @param rotation The rotation of the image in degrees.
     */
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        AffineTransform origTransform = g.getTransform();
        Composite origComposite = g.getComposite();

        this.transform.setToRotation(Math.toRadians(rotation),
                                     (x.pixels() + w.pixels() / 2) + rotationOffsetX.pixels(),
                                     (y.pixels() + h.pixels() / 2) + rotationOffsetY.pixels());

        g.transform(this.transform);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.alpha));

        if (!w.equals(this.lastWidth) || !h.equals(this.lastHeight) || this.lastUnitRatio != Unit.getRatio())
        {
            this.lastUnitRatio = Unit.getRatio();
            this.scaledImage = this.image.getScaledInstance((int)w.pixels(),
                                                            (int)h.pixels(),
                                                            Image.SCALE_SMOOTH);
            this.lastHeight = h;
            this.lastWidth = w;
        }

        g.drawImage(this.scaledImage,
                    (int)x.pixels(),
                    (int)y.pixels(),
                    null);

        if (debugRendering)
        {
            Shape sh = Geometry.createRectangle(w.units(),
                                                h.units());
            AffineTransform ot = g.getTransform();
            AffineTransform lt = new AffineTransform();

            lt.translate(x.pixels() + w.pixels() / 2,
                         y.pixels() + h.pixels() / 2);

            g.transform(lt);

            ShapeRenderer.render(g, sh, Color.yellow);

            g.setTransform(ot);
        }

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

        if (this.scaledImage != null)
        {
            this.scaledImage.flush();
        }
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return this.z;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#setZ(bt.game.util.unit.Unit)
     */
    @Override
    public void setZ(Unit z)
    {
        this.z = z;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#shouldRender()
     */
    @Override
    public boolean shouldRender()
    {
        return this.shouldRender;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#shouldRender(boolean)
     */
    @Override
    public void shouldRender(boolean shouldRender)
    {
        this.shouldRender = shouldRender;
    }
}