package bt.game.core.ctrl.spec.mouse.obj;

import java.awt.*;

public class Cursor
{
    private Point hotspot;
    private Image cursorImage;

    public Cursor(Image cursor, Point hotspot)
    {
        this.cursorImage = cursor;
        this.hotspot = hotspot;
    }

    public Cursor(Image cursor)
    {
        this(cursor, new Point(0, 0));
    }

    public Point getHotspot()
    {
        return this.hotspot;
    }

    public void setHotspot(Point hotspot)
    {
        this.hotspot = hotspot;
    }

    public Image getCursorImage()
    {
        return this.cursorImage;
    }

    public void setCursorImage(Image cursorImage)
    {
        this.cursorImage = cursorImage;
    }
}