package bt.game.core.obj.bound;

import java.awt.Polygon;

import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class Hitbox
{
    private Unit x;
    private Unit y;
    private Unit[] xPoints;
    private Unit[] yPoints;
    private Polygon polygon;

    public Hitbox(Unit x, Unit y, Unit[] xPoints, Unit[] yPoints)
    {
        this.x = x;
        this.y = y;
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        calculatePolygon();
    }

    public void setBase(Unit x, Unit y)
    {
        this.x = x;
        this.y = y;
        calculatePolygon();
    }

    private void calculatePolygon()
    {
        int[] xCord = new int[this.xPoints.length];
        int[] yCord = new int[this.yPoints.length];

        for (int i = 0; i < this.xPoints.length; i ++ )
        {
            xCord[i] = (int)(this.x.pixels() + this.xPoints[i].pixels());
            yCord[i] = (int)(this.y.pixels() + this.yPoints[i].pixels());
        }

        this.polygon = new Polygon(xCord, yCord, xCord.length);
    }

    public Polygon getPolygon()
    {
        return this.polygon;
    }
}