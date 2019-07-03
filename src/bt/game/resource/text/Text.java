package bt.game.resource.text;

import java.awt.Color;
import java.awt.Font;

/**
 * @author &#8904
 *
 */
public class Text
{
    private String text;
    private Color color;
    private Font font;
    private String language;
    private int id;

    /**
     * @return the id
     */
    public int getID()
    {
        return this.id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setID(int id)
    {
        this.id = id;
    }

    public Text(int id, String text)
    {
        this.id = id;
        this.text = text;
        this.language = "EN";
    }

    public void setLanguage(String language)
    {
        this.language = language.toUpperCase();
    }

    public String getLanguage()
    {
        return this.language;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return this.color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(Color color)
    {
        this.color = color;
    }

    /**
     * @return the font
     */
    public Font getFont()
    {
        return this.font;
    }

    /**
     * @param font
     *            the font to set
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}