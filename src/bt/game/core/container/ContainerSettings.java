package bt.game.core.container;

/**
 * @author &#8904
 *
 */
public class ContainerSettings
{
    private float unitWidth;
    private float unitHeight;
    private int frameWidth;
    private int frameHeight;
    private boolean undecorated;
    private boolean fullscreen;

    public ContainerSettings gameUnits(float unitWidth, float unitHeight)
    {
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        return this;
    }

    public ContainerSettings frameSize(int width, int height)
    {
        this.frameWidth = width;
        this.frameHeight = height;
        return this;
    }

    public ContainerSettings undecorated(boolean undecorated)
    {
        this.undecorated = undecorated;
        return this;
    }

    public ContainerSettings fullscreen(boolean fullscreen)
    {
        this.fullscreen = fullscreen;
        return this;
    }

    protected float getUnitWidth()
    {
        return this.unitWidth;
    }

    protected float getUnitHeight()
    {
        return this.unitHeight;
    }

    protected int getFrameWidth()
    {
        return this.frameWidth;
    }

    protected int getFrameHeight()
    {
        return this.frameHeight;
    }

    protected boolean isUndecorated()
    {
        return this.undecorated;
    }

    protected boolean isFullscreen()
    {
        return this.fullscreen;
    }
}