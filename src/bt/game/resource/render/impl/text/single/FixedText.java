package bt.game.resource.render.impl.text.single;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

import bt.game.util.unit.Unit;

/**
 * A text rendering class which will render the text inside a given rectangular shape. The text will either fill out the
 * width or the height of the rectangle while keeping its aspect ratio. The axis that is filled depends on the given
 * parameter. Pass either {@link #FIXED_HEIGHT} or {@link #FIXED_WIDTH}.
 * 
 * <p>
 * The rendered text will never exceed the bounds of the fixed axis. No guarantee is made for the other axis.
 * </p>
 * 
 * @author &#8904
 */
public class FixedText extends RenderableText
{
    /**
     * Indicates that the text should be rendered with a fixed width. Meaning it will never exceed the width of the set
     * rectangular shape.
     */
    public static final int FIXED_WIDTH = 1;

    /**
     * Indicates that the text should be rendered with a fixed height. Meaning it will never exceed the height of the
     * set rectangular shape.
     */
    public static final int FIXED_HEIGHT = 2;

    /** Saves the set type. Either {@link #FIXED_HEIGHT} or {@link #FIXED_WIDTH}. */
    private int fixedType;

    /**
     * Creates a new instance.
     * 
     * @param text
     *            The text to render.
     */
    public FixedText(String text, int fixedType)
    {
        super(text);
        this.fixedType = fixedType;
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
    public FixedText(String text, int fixedType, Unit x, Unit y, Unit fixedValue)
    {
        super(text,
              x,
              y,
              fixedValue,
              fixedValue);
        this.fixedType = fixedType;
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
    public FixedText(String text, int fixedType, Unit fixedValue)
    {
        super(text,
              fixedValue,
              fixedValue);
        this.fixedType = fixedType;
    }

    /**
     * @see bt.game.resource.render.impl.text.single.RenderableText#doScaling(java.awt.Graphics2D,
     *      bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
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

        double trueScale = 1; // yScale < xScale ? yScale : xScale;

        if (this.fixedType == FixedText.FIXED_HEIGHT)
        {
            trueScale = (double)(h.pixels() / height);
        }
        else if (this.fixedType == FixedText.FIXED_WIDTH)
        {
            trueScale = w.pixels() / width;
        }

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

        this.transform = AffineTransform.getTranslateInstance(xPos,
                                                              yPos);
        this.transform.scale(trueScale,
                             trueScale);

        this.h = h;
        this.w = w;
    }
}