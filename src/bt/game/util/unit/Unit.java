package bt.game.util.unit;

/**
 * A measurement conversion class between pixels and custom game units. This class uses a set ratio of pixels per unit
 * to do its calculations.
 * 
 * @author &#8904
 */
public class Unit
{
    private static float ratio = 1;
    private float pixels;
    private float units;

    /**
     * Sets the pixel per unit ratio. The given value is the amount of pixels one unit should consist of. If this method
     * is not called, the default ratio of 1 will be used.
     * 
     * @param pixelsPerUnit
     */
    public static void setRatio(float pixelsPerUnit)
    {
        Unit.ratio = pixelsPerUnit;
    }

    /**
     * Creates a new instance where the given value is treated as units.
     * 
     * @param units
     * @return
     */
    public static Unit forUnits(float units)
    {
        Unit unit = new Unit();
        unit.setUnits(units);
        return unit;
    }

    /**
     * Creates a new instance where the given value is treated as pixels.
     * 
     * @param pixels
     * @return
     */
    public static Unit forPixels(float pixels)
    {
        Unit unit = new Unit();
        unit.setPixels(pixels);
        return unit;
    }

    /**
     * Adds the given amount of units to this instances units.
     * 
     * @param units
     */
    public void addUnits(float units)
    {
        setUnits(this.units + units);
    }

    /**
     * Adds the given amount of pixels to this instances pixels.
     * 
     * @param pixels
     */
    public void addPixels(float pixels)
    {
        setPixels(this.pixels + pixels);
    }

    /**
     * Sets the units of this instance to the given value. The pixels will be calculated with the set ratio.
     * 
     * @param units
     */
    public void setUnits(float units)
    {
        this.units = units;
        this.pixels = units * Unit.ratio;
    }

    /**
     * Sets the pixels of this instance to the given value. The units will be calculated with the set ratio.
     * 
     * @param units
     */
    public void setPixels(float pixels)
    {
        this.pixels = pixels;
        this.units = pixels / Unit.ratio;
    }

    /**
     * Gets the value for units this instance holds.
     * 
     * @return
     */
    public float units()
    {
        return this.units;
    }

    /**
     * Gets the value for pixels this instance holds.
     * 
     * @return
     */
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