package bt.game.core.container;

import bt.game.core.container.abstr.GameContainer;
import bt.game.util.unit.Unit;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A configuration class for {@link GameContainer}s.
 *
 * @author &#8904
 */
public class ContainerSettings
{
    private float unitWidth;
    private float unitHeight;
    private int frameWidth;
    private int frameHeight;
    private boolean undecorated;
    private boolean fullscreen;
    private Image imageIcon;
    private String title;
    private boolean debugRendering;

    /**
     * Sets the measurements of the the game canvas in {@link Unit units}. This has no effect on the actual window size,
     * it just measn that the window width <i>n</i> will consist of <i>unitWidth</i> units.
     *
     * @param unitWidth  The units of the X axis.
     * @param unitHeight The units of the Y axis.
     * @return This instance for chaining.
     */
    public ContainerSettings gameUnits(float unitWidth, float unitHeight)
    {
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        return this;
    }

    /**
     * Sets the size of the frame. This setting has no effect if {@link #fullscreen(boolean)} is set to true.
     *
     * @param width  The width of the window.
     * @param height The height of the window.
     * @return This instance for chaining.
     */
    public ContainerSettings frameSize(int width, int height)
    {
        this.frameWidth = width;
        this.frameHeight = height;
        return this;
    }

    /**
     * Sets whether the window will be decorated.
     *
     * @param undecorated true to remove window decorations.
     * @return This instance for chaining.
     */
    public ContainerSettings undecorated(boolean undecorated)
    {
        this.undecorated = undecorated;
        return this;
    }

    /**
     * Indicates whether the game should be in fullscreen mode.
     *
     * @param fullscreen true if the game shuld be in fullscreen.
     * @return This instance for chaining.
     */
    public ContainerSettings fullscreen(boolean fullscreen)
    {
        this.fullscreen = fullscreen;
        return this;
    }

    /**
     * Sets the title of the game shown in the taskbar.
     *
     * @param title The title.
     * @return This instance for chaining.
     */
    public ContainerSettings title(String title)
    {
        this.title = title;
        return this;
    }

    /**
     * Sets the icon of the game shown in the taskbar.
     *
     * @param icon The icon.
     * @return This instance for chaining.
     */
    public ContainerSettings icon(Image icon)
    {
        this.imageIcon = icon;
        return this;
    }

    /**
     * Sets the icon of the game shown in the taskbar.
     *
     * @param icon The image file.
     * @return This instance for chaining.
     */
    public ContainerSettings icon(File icon)
    {
        try
        {
            this.imageIcon = ImageIO.read(icon);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public float getUnitWidth()
    {
        return this.unitWidth;
    }

    public float getUnitHeight()
    {
        return this.unitHeight;
    }

    public int getFrameWidth()
    {
        return this.frameWidth;
    }

    public int getFrameHeight()
    {
        return this.frameHeight;
    }

    public boolean isUndecorated()
    {
        return this.undecorated;
    }

    public boolean isFullscreen()
    {
        return this.fullscreen;
    }

    public String getTitle()
    {
        return this.title;
    }

    public Image getIcon()
    {
        return this.imageIcon;
    }

    public boolean isDebugRendering()
    {
        return this.debugRendering;
    }

    public ContainerSettings setDebugRendering(boolean debugRendering)
    {
        this.debugRendering = debugRendering;
        return this;
    }
}