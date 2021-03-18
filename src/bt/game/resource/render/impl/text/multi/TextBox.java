package bt.game.resource.render.impl.text.multi;

import bt.game.resource.render.impl.text.single.FixedText;
import bt.game.resource.render.impl.text.single.RenderableText;
import bt.game.util.shape.ShapeRenderer;
import bt.game.util.unit.Unit;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Shape;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * A text rendering class which will render the text in one or more rows with the given height.
 * <p>
 * Internally this class uses {@link FixedText FixedTexts} with a fixed height for each row.
 *
 * <p>
 * The rendered text will never exceed the bounds of the given width unless a single is wider than the bounds.
 * </p>
 *
 * @author &#8904
 */
public class TextBox extends RenderableText
{
    /**
     * The list of lines generated from the text.
     */
    private List<FixedText> lines = new ArrayList<>();

    /**
     * The maximum number of rows to display. -1 indicates an unlimited number.
     */
    private int maxLines = -1;

    /**
     * Indicates that the lines should be shifted down to center the text within the maximum number of rows.
     * <p>
     * For example if a maximum of 3 rows is specified but the given text will only take up a single row,
     * then this row will be shifted down to be in the middle of the designated 3 rows, now basically being rednered as the second row.
     */
    private boolean centerInMaxLines = false;

    /**
     * Indicates whether line break characters should be interpreted and reflected in the displayed lines.
     */
    private boolean lineBreak = true;

    /**
     * Defines the vertical space between lines.
     */
    private Unit lineSpacing = Unit.zero();

    /**
     * Creates a new instance.
     *
     * @param text       The text to display
     * @param x          The x position of the text box.
     * @param y          The y position of the first row.
     * @param w          The width of the rows.
     * @param lineHeight The height of each row.
     */
    public TextBox(String text, Unit x, Unit y, Unit w, Unit lineHeight)
    {
        super(text, x, y, w, lineHeight);
    }

    /**
     * Sets the vertical space between rows.
     *
     * @param lineSpacing
     */
    public void setLineSpacing(Unit lineSpacing)
    {
        this.lineSpacing = lineSpacing;
    }

    /**
     * Sets the maximum number of displayed rows.
     * <p>
     * -1 ius the default and indicates an unlimited number of rows.
     *
     * @param maxLines The number of rows.
     */
    public void setMaxLines(int maxLines)
    {
        this.maxLines = maxLines;
    }

    /**
     * Sets the maximum number of displayed rows.
     * <p>
     * -1 ius the default and indicates an unlimited number of rows.
     *
     * @param maxLines The number of rows.
     * @param center   Indicates whether the displayed rows should be centered within the space for the maximum rows. For example if a maximum
     *                 of 3 rows is specified but the given text will only take up a single row, then this row will be shifted down to be in
     *                 the middle of the designated 3 rows, now basically being rednered as the second row.
     */
    public void setMaxLines(int maxLines, boolean center)
    {
        this.maxLines = maxLines;
        this.centerInMaxLines = center;
    }

    /**
     * Sets whether line break characters should be ignored or used to skip rows.
     *
     * @param ignoreLineBreaks
     */
    public void ignoreLineBreaks(boolean ignoreLineBreaks)
    {
        this.lineBreak = !ignoreLineBreaks;
    }

    /**
     * Gets the number of rendered rows.
     *
     * @return
     */
    public int getLineCount()
    {
        return this.lines.size();
    }

    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit lineHeight, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        this.x = x;
        this.y = y;

        if (this.font == null)
        {
            this.font = g.getFont();
        }

        Font originalFont = g.getFont();

        g.setFont(this.font);

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                           RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // only do this if the scaling really needs to be adjusted
        if (this.shouldRecalculate
                || this.lastUnitRatio != Unit.getRatio())
        {
            doScaling(g, x, y, w, lineHeight);
            g.setFont(this.font.deriveFont(this.transform));
            generateLines(g, w, lineHeight);
            this.shouldRecalculate = false;
        }

        g.rotate(Math.toRadians(rotation),
                 (x.pixels() + w.pixels() / 2) + rotationOffsetX.pixels(),
                 (y.pixels() + lineHeight.pixels() / 2) + rotationOffsetY.pixels());

        g.setFont(originalFont);

        if (this.color != null)
        {
            g.setColor(this.color);
        }

        if (this.maxLines > 0 && this.lines.size() < this.maxLines && this.centerInMaxLines)
        {
            // add half of the spare space to the top and therefore move the lines down to center them
            y = y.addUnits(lineHeight.addUnits(this.lineSpacing).multiplyWith(this.maxLines - this.lines.size()).divideBy(2));
        }

        for (int i = 0; i < this.lines.size(); i++)
        {
            var line = this.lines.get(i);
            line.render(g, x, y.addUnits(lineHeight.addUnits(this.lineSpacing).multiplyWith(i)), w, lineHeight, debugRendering);
        }

        g.setFont(originalFont);

        if (debugRendering)
        {
            Shape sh = Geometry.createRectangle(w.units(), lineHeight.units());
            AffineTransform ot = g.getTransform();
            AffineTransform lt = new AffineTransform();

            lt.translate(x.pixels() + w.pixels() / 2,
                         y.pixels() + lineHeight.pixels() / 2);

            g.transform(lt);

            ShapeRenderer.render(g, sh, Color.MAGENTA);

            g.setTransform(ot);
        }

        g.rotate(Math.toRadians(-rotation),
                 x.pixels() + w.pixels() / 2,
                 y.pixels() + lineHeight.pixels() / 2);
    }

    @Override
    protected void doScaling(Graphics2D g, Unit x, Unit y, Unit w, Unit lineHeight)
    {
        Font font = g.getFont();
        FontRenderContext renderContext = g.getFontRenderContext();
        LineMetrics metrics = font.getLineMetrics(this.text, renderContext);

        this.lastUnitRatio = Unit.getRatio();
        float height = metrics.getAscent() + metrics.getDescent();
        double width = font.getStringBounds(this.text, renderContext).getWidth();
        double xScale = w.pixels() / width;
        double yScale = (double)(lineHeight.pixels() / height);

        double trueScale = (double)(lineHeight.pixels() / height);

        double xPos = 0;
        double yPos = 0;

        if (this.xCentered)
        {
            xPos = (w.pixels() - trueScale * width) / 2;
        }

        if (this.yCentered)
        {
            yPos = (lineHeight.pixels() + trueScale * height) / 2 - trueScale * metrics.getDescent();
        }

        this.transform = AffineTransform.getTranslateInstance(xPos, yPos);
        this.transform.scale(trueScale, trueScale);

        this.h = lineHeight;
        this.w = w;
    }

    protected void generateLines(Graphics2D g, Unit w, Unit lineHeight)
    {
        this.lines = new ArrayList<>();
        String[] words = this.text.split(" ");
        Font font = g.getFont();
        FontRenderContext renderContext = g.getFontRenderContext();

        String currentText = "";
        String tempText = "";
        String word = "";
        boolean addedLast = false;

        for (int i = 0; i < words.length; i++)
        {
            word = words[i];
            tempText += " " + word;
            tempText = tempText.trim();

            if (word.isEmpty())
            {
                continue;
            }

            double width = font.getStringBounds(tempText, renderContext).getWidth();

            if (width > w.pixels())
            {
                createAndAddLine(currentText, lineHeight);

                tempText = " " + word;
                tempText = tempText.trim();

                // this makes sure that the last line is still generated even if its only a single word
                addedLast = i != words.length - 1;
            }
            else
            {
                addedLast = false;
            }

            currentText = tempText;

            if (this.lineBreak && word.endsWith("\n"))
            {
                createAndAddLine(currentText, lineHeight);
                currentText = "";
                tempText = "";
                addedLast = true;
            }
        }

        if (!addedLast)
        {
            createAndAddLine(currentText, lineHeight);
        }
    }

    protected void createAndAddLine(String text, Unit lineHeight)
    {
        var line = new FixedText(text, FixedText.FIXED_HEIGHT, lineHeight);
        line.setXCentered(this.xCentered);
        line.setYCentered(this.yCentered);
        line.setColor(this.color);

        if (this.maxLines < 0 || this.lines.size() < this.maxLines)
        {
            this.lines.add(line);
        }
    }
}