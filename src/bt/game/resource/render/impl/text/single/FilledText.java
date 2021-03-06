package bt.game.resource.render.impl.text.single;

import bt.game.resource.render.impl.text.RenderableText;
import bt.game.util.unit.Unit;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

/**
 * A text rendering class which will render the text inside a given rectangular shape. The text will fill out the entire
 * rectangle without any regards to its aspect ratio.
 *
 * <p>
 * The rendered text will never exceed the rectangular shape.
 * </p>
 *
 * @author &#8904
 */
public class FilledText extends RenderableText
{
    /**
     * Creates a new instance.
     *
     * @param text The text to render.
     */
    public FilledText(String text)
    {
        super(text);
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
    public FilledText(String text, Unit x, Unit y, Unit w, Unit h)
    {
        super(text,
              x,
              y,
              w,
              h);
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
    public FilledText(String text, Unit w, Unit h)
    {
        super(text,
              w,
              h);
    }

    /**
     * @see RenderableText#doScaling(java.awt.Graphics2D,
     * bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    protected void doScaling(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        Font font = g.getFont();
        FontRenderContext renderContext = g.getFontRenderContext();
        LineMetrics metrics = font.getLineMetrics(this.text,
                                                  renderContext);

        this.lastUnitRatio = Unit.getRatio();
        float height = metrics.getAscent() + metrics.getDescent();
        double width = font.getStringBounds(this.text,
                                            renderContext)
                           .getWidth();
        double xScale = w.pixels() / width;
        double yScale = (double)(h.pixels() / height);

        double xPos = 0;
        double yPos = 0;

        if (this.xCentered)
        {
            xPos = (w.pixels() - xScale * width) / 2;
        }

        if (this.yCentered)
        {
            yPos = (h.pixels() + yScale * height) / 2 - yScale * metrics.getDescent();
        }

        this.transform = AffineTransform.getTranslateInstance(xPos,
                                                              yPos);
        this.transform.scale(xScale,
                             yScale);

        this.h = h;
        this.w = w;
    }
}