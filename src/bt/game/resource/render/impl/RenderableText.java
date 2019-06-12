package bt.game.resource.render.impl;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class RenderableText implements Renderable
{
    private Unit lastWidth;
    private Unit lastHeight;
    private AffineTransform transform;
    private String text;
    private double rotationAngle;

    public RenderableText(String text)
    {
        this.text = text;
        this.lastWidth = Unit.forUnits(0);
        this.lastHeight = Unit.forUnits(0);
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
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

    public double getRotation()
    {
        return this.rotationAngle;
    }

    /**
     * Renders this instance at the position x|y with a width of w and a height of h. The given Graphics object is used
     * to create a {@link Graphics2D} copy, so that accurate scaling actions can be performed.
     * 
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit,
     *      bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h)
    {
        Graphics2D g2 = (Graphics2D)g.create();

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        Font font = g2.getFont();
        FontRenderContext renderContext = g2.getFontRenderContext();
        LineMetrics metrics = font.getLineMetrics(this.text, renderContext);

        // only do this if the scaling really needs to be adjusted
        if (!w.equals(this.lastWidth) || !h.equals(this.lastHeight))
        {
            float height = metrics.getAscent() + metrics.getDescent();
            double width = font.getStringBounds(this.text, renderContext).getWidth();
            double xScale = w.pixels() / width;
            double yScale = (double)(h.pixels() / height);

            // keeping aspect ration by using the lowest scaling factor
            // keeps text within bounds
            double trueScale = yScale < xScale ? yScale : xScale;

            double xPos = (w.pixels() - trueScale * width) / 2;
            double yPos = (h.pixels() + trueScale * height) / 2 - trueScale * metrics.getDescent();

            this.transform = AffineTransform.getTranslateInstance(xPos, yPos);
            this.transform.scale(trueScale, trueScale);

            this.lastHeight = h;
            this.lastWidth = w;
        }

        g2.rotate(Math.toRadians(this.rotationAngle),
                x.pixels() + w.pixels() / 2,
                y.pixels() + h.pixels() / 2);

        g2.setFont(font.deriveFont(this.transform));

        g2.drawString(this.text, (int)x.pixels(), (int)y.pixels());
        g2.dispose();
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
                this.lastWidth,
                this.lastHeight);
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