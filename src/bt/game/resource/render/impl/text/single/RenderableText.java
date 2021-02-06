package bt.game.resource.render.impl.text.single;

import bt.game.resource.render.impl.AdvancedRenderable;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Base class for text that can be rendered on the screen.
 *
 * <p>
 * Subclasses are:
 * <ul>
 * <li>{@link FilledText} to make a text fill out a given rectangle.</li>
 * <li>{@link FlexibleText} to make a text fill out a given rectangle either in width or height while keeping its aspect
 * ratio. The axis that will be filled first is chosen.</li>
 * <li>{@link FixedText} to make a text fill out a given rectangle either in width or height while keeping its aspect
 * ratio. The axis that will be filled depends on a given parameter.</li>
 * </ul>
 * </p>
 *
 * @author &#8904
 */
public abstract class RenderableText extends AdvancedRenderable
{
    /**
     * The last used unit to pixel ration. Used to detect when the size of the text should be recalculated.
     */
    protected double lastUnitRatio = Unit.getRatio();

    /**
     * The transform used to scale the text.
     */
    protected AffineTransform transform;

    /**
     * The text to display.
     */
    protected String text;

    /**
     * The angle at which the text is displayed.
     */
    protected double rotationAngle;

    /**
     * The used font.
     */
    protected Font font;

    /**
     * The used color.
     */
    protected Color color;

    /**
     * Indicates whether the text is centered on the x axis.
     */
    protected boolean xCentered;

    /**
     * Indicates whether the text is centered on the y axis.
     */
    protected boolean yCentered;

    /**
     * Indicates whether the size of the text should be recalculated due a change in text, font, width or height.
     */
    protected boolean shouldRecalculate;

    /**
     * Creates a new instance.
     *
     * @param text The text to render.
     */
    public RenderableText(String text)
    {
        this(text,
             Unit.zero(),
             Unit.zero(),
             Unit.zero(),
             Unit.zero());
    }

    /**
     * Creates a new instance.
     *
     * @param text The text to render.
     * @param x    The x position of the text.
     * @param y    The y position of the text.
     * @param w    The width of the area that the text will be rendered in.
     * @param h    The height of the area that the text will be rendered in.
     */
    protected RenderableText(String text, Unit x, Unit y, Unit w, Unit h)
    {
        this.text = text;
        this.w = w;
        this.h = h;
        this.x = x;
        this.y = y;
        this.shouldRecalculate = true;
    }

    /**
     * Creates a new instance.
     *
     * @param text The text to render.
     * @param w    The width of the area that the text will be rendered in.
     * @param h    The height of the area that the text will be rendered in.
     */
    protected RenderableText(String text, Unit w, Unit h)
    {
        this(text,
             Unit.zero(),
             Unit.zero(),
             w,
             h);
    }

    /**
     * The text that will be rendered.
     *
     * @return
     */
    public String getText()
    {
        return this.text;
    }

    /**
     * Sets the text that will be rendered.
     *
     * @param text
     */
    public void setText(String text)
    {
        this.text = text;
        this.shouldRecalculate = true;
    }

    /**
     * The font that is used for rendering.
     *
     * @return
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * Sets the font that is used for rendering.
     *
     * @param font
     */
    public void setFont(Font font)
    {
        this.font = font;
        this.shouldRecalculate = true;
    }

    /**
     * The color that is used for rendering.
     *
     * @return
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * Sets the color that is used for rendering.
     *
     * @param font
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * Indicates whether the text will be centered on the x axis.
     *
     * @return
     */
    public boolean isXCentered()
    {
        return this.xCentered;
    }

    /**
     * Sets the text to either be centered on the x axis.
     *
     * @param centered
     */
    public void setXCentered(boolean centered)
    {
        if (centered != this.xCentered)
        {
            this.shouldRecalculate = true;
        }
        this.xCentered = centered;
    }

    /**
     * Indicates whether the text will be centered on the y axis.
     *
     * @return
     */
    public boolean isYCentered()
    {
        return this.yCentered;
    }

    /**
     * Sets the text to either be centered on the y axis.
     *
     * @param centered
     */
    public void setYCentered(boolean centered)
    {
        if (centered != this.yCentered)
        {
            this.shouldRecalculate = true;
        }
        this.yCentered = centered;
    }

    /**
     * Sets the angle by which this text will be rotated. The axis point will be in the middle of the the defined
     * rectangle in {@link #render(Graphics, Unit, Unit, Unit, Unit) render}.
     *
     * @param rotationAngle
     */
    public void setRotation(double rotationAngle)
    {
        this.rotationAngle = rotationAngle;
    }

    /**
     * Gets the rotation angle at which the text will be rendered.
     *
     * @return
     */
    public double getRotation()
    {
        return this.rotationAngle;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#getZ()
     */
    @Override
    public Unit getZ()
    {
        return Unit.zero();
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h.
     *
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit,
     * bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        this.x = x;
        this.y = y;

        if (this.font == null)
        {
            this.font = g.getFont();
        }

        g.setFont(this.font);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // only do this if the scaling really needs to be adjusted
        if (this.shouldRecalculate
                || !w.equals(this.w)
                || !h.equals(this.h)
                || this.lastUnitRatio != Unit.getRatio())
        {
            doScaling(g,
                      x,
                      y,
                      w,
                      h);
        }

        g.rotate(Math.toRadians(this.rotationAngle),
                 this.x.pixels() + this.w.pixels() / 2,
                 this.y.pixels() + this.h.pixels() / 2);

        g.setFont(this.font.deriveFont(this.transform));

        if (this.color != null)
        {
            g.setColor(this.color);
        }

        g.drawString(this.text,
                     (int)this.x.pixels(),
                     (int)this.y.pixels());

        if (debugRendering)
        {
            Shape sh = Geometry.createRectangle(w.units(),
                                                h.units());
            AffineTransform ot = g.getTransform();
            AffineTransform lt = new AffineTransform();

            lt.translate(this.x.pixels() + this.w.pixels() / 2,
                         this.y.pixels() + this.h.pixels() / 2);

            g.transform(lt);

            ShapeRenderer.render(g, sh, Color.MAGENTA);

            g.setTransform(ot);
        }

        g.rotate(Math.toRadians(-this.rotationAngle),
                 this.x.pixels() + this.w.pixels() / 2,
                 this.y.pixels() + this.h.pixels() / 2);
    }

    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        render(g, x, y, w, h, false);
    }

    /**
     * Renders this instance with a width of w and a height of h.
     */
    public void render(Graphics2D g, Unit w, Unit h)
    {
        render(g,
               w,
               h,
               false);
    }

    public void render(Graphics2D g, Unit w, Unit h, boolean debugRendering)
    {
        render(g,
               this.x,
               this.y,
               w,
               h,
               debugRendering);
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
        render(g,
               this.x,
               this.y,
               this.w,
               this.h,
               debugRendering);
    }

    public void render(Graphics2D g)
    {
        render(g,
               this.x,
               this.y,
               this.w,
               this.h,
               false);
    }

    /**
     * This method should cover all scaling actions of the text that are needed to fulfil the implementations
     * requirements.
     *
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    protected abstract void doScaling(Graphics2D g, Unit x, Unit y, Unit w, Unit h);
}