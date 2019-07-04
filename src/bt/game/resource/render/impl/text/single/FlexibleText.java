package bt.game.resource.render.impl.text.single;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

import bt.game.util.unit.Unit;

/**
 * A text rendering class which will render the text inside a given rectangular shape. The text will either fill out the
 * width or the height of the rectangle while keeping its aspect ratio. The axis that is filled depends on which is
 * reached first.
 * 
 * <p>
 * The rendered text will never exceed the rectangular shape.
 * </p>
 * 
 * @author &#8904
 */
public class FlexibleText extends RenderableText
{
    /**
     * Creates a new instance.
     * 
     * @param text
     *            The text to render.
     */
    public FlexibleText(String text)
    {
        super(text);
    }

    /**
     * Creates a new instance.
     * 
     * @param text
     *            The text to render.
     * @param x
     *            The x position of the text.
     * @param y
     *            The y position of the text.
     * @param w
     *            The width of the area that the text will be rendered in.
     * @param h
     *            The height of the area that the text will be rendered in.
     */
    public FlexibleText(String text, Unit x, Unit y, Unit w, Unit h)
    {
        super(text, x, y, w, h);
    }

    /**
     * Creates a new instance.
     * 
     * @param text
     *            The text to render.
     * @param x
     *            The x position of the text.
     * @param y
     *            The y position of the text.
     * @param w
     *            The width of the area that the text will be rendered in.
     * @param h
     *            The height of the area that the text will be rendered in.
     */
    public FlexibleText(String text, Unit w, Unit h)
    {
        super(text, w, h);
    }

    /**
     * @see bt.game.resource.render.impl.text.single.RenderableText#doScaling(java.awt.Graphics2D, bt.game.util.unit.Unit,
     *      bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    protected void doScaling(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        Font font = g.getFont();
        FontRenderContext renderContext = g.getFontRenderContext();
        LineMetrics metrics = font.getLineMetrics(this.text, renderContext);

        this.lastUnitRatio = Unit.getRatio();
        float height = metrics.getAscent() + metrics.getDescent();
        double width = font.getStringBounds(this.text, renderContext).getWidth();
        double xScale = w.pixels() / width;
        double yScale = (double)(h.pixels() / height);

        // keeping aspect ration by using the lowest scaling factor
        // keeps text within bounds
        double trueScale = yScale < xScale ? yScale : xScale;

        double xPos = 0;
        double yPos = 0;

        if (this.xCentered)
        {
            xPos = (w.pixels() - trueScale * width) / 2;
        }

        if (this.yCentered)
        {
            yPos = (h.pixels() + trueScale * height) / 2 - trueScale * metrics.getDescent();
        }

        this.transform = AffineTransform.getTranslateInstance(xPos, yPos);
        this.transform.scale(trueScale, trueScale);

        this.h = h;
        this.w = w;
    }
}