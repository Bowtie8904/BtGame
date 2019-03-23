package bt.game.util.unit;

/**
 * @author &#8904
 *
 */
public class Unit
{
    private static float ratio = 1;
    private float pixels;
    private float units;

    public static void setRatio(float pixelsPerUnit)
    {
        if (pixelsPerUnit <= 0)
        {
            // throw new IllegalArgumentException("A unit can't consist of zero or less pixels.");
        }
        Unit.ratio = pixelsPerUnit;
    }

    public static Unit forUnits(float units)
    {
        Unit unit = new Unit();
        unit.setUnits(units);
        return unit;
    }

    public static Unit forPixels(float pixels)
    {
        Unit unit = new Unit();
        unit.setPixels(pixels);
        return unit;
    }

    public void addUnits(float units)
    {
        setUnits(this.units + units);
    }

    public void addPixels(float pixels)
    {
        setPixels(this.pixels + pixels);
    }

    protected void setUnits(float units)
    {
        this.units = units;
        this.pixels = units * Unit.ratio;
    }

    protected void setPixels(float pixels)
    {
        this.pixels = pixels;
        this.units = pixels / Unit.ratio;
    }

    public float units()
    {
        return this.units;
    }

    public float pixels()
    {
        return this.pixels;
    }

    protected Unit()
    {
    }

    @Override
    public String toString()
    {
        return "Pixels: " + this.pixels + "  Units: " + this.units;
    }
}