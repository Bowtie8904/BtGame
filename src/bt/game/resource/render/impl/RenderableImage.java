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
        this.z = Unit.zero();
        this.shouldRender = true;
    }

    /**
     * Returns a cropped version of the underlying image wrapped in a new {@link RenderableImage} instance.
     *
     * <p>
     * The returned RenderableImage copies the Z and the {@link #shouldRender()} settting. It also shares
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
            croppedImage.shouldRender(this.shouldRender);
            return croppedImage;
        }
        else
        {
            throw new UnsupportedOperationException("Only instances of BufferedImage can be cropped.");
        }
    }

    /**
     * Returns a cropped version of the underlying image wrapped in a new {@link RenderableImage} instance.
     * <p>
     * The returned RenderableImage copies the Z and the {@link #shouldRender()} settting. It also shares
     * the same image data as this instance, which means that changes to the underlying image can affect both
     * RenderabelImages.
     * </p>
     *
     * @param cropStrat   The strategy which defines which side of the image will not be cropped off.
     * @param widthParts  The width parts of the aspect ratio. If you want a 16:9 image then this value needs to be 16.
     * @param heightParts The height parts of the aspect ratio. If you want a 16:9 image then this value needs to be 9.
     * @return The cropped RenderableImage if possible.
     * @throws UnsupportedOperationException if the underlying image is not an instance of {@link BufferedImage}.
     */
    public RenderableImage crop(Cropping cropStrat, int widthParts, int heightParts)
    {
        int width = this.image.getWidth(null);
        int height = this.image.getHeight(null);

        if (cropStrat == Cropping.MAINTAIN_HEIGHT)
        {
            double singlePixel = height / (double)heightParts;
            width = NumberUtils.clamp((int)(singlePixel * widthParts), 0, width);
        }
        else if (cropStrat == Cropping.MAINTAIN_WIDTH)
        {
            double singlePixel = width / (double)widthParts;
            height = NumberUtils.clamp((int)(singlePixel * heightParts), 0, height);
        }

        return crop(0, 0, width, height);
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
    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        AffineTransform origTransform = g.getTransform();
        Composite origComposite = g.getComposite();

        this.transform.setToRotation(Math.toRadians(rotation),
                                     (x.pixels() + w.pixels() / 2) + rotationOffsetX.pixels(),
                                     (y.pixels() + h.pixels() / 2) + rotationOffsetY.pixels());

        g.transform(this.transform);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

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

    @Override
    public Unit getX()
    {
        return Unit.zero();
    }

    @Override
    public void setX(Unit x)
    {
    }

    @Override
    public Unit getY()
    {
        return Unit.zero();
    }

    @Override
    public void setY(Unit y)
    {
    }

    @Override
    public Unit getW()
    {
        return Unit.forUnits(this.image.getWidth(null));
    }

    @Override
    public void setW(Unit w)
    {

    }

    @Override
    public Unit getH()
    {
        return Unit.forUnits(this.image.getHeight(null));
    }

    @Override
    public void setH(Unit h)
    {

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