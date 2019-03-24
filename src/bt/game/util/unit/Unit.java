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
     * Adds the given amount of units to the value that this instance holds. A new Unit instance with the adjusted value
     * is created and returned. The original instance is not modified.
     * 
     * @param units
     */
    public Unit addUnits(float units)
    {
        return forUnits(this.units + units);
    }

    /**
     * Adds the given amount of pixels to the value that this instance holds. A new Unit instance with the adjusted
     * value is created and returned. The original instance is not modified.
     * 
     * @param pixels
     */
    public Unit addPixels(float pixels)
    {
        return forPixels(pixels() + pixels);
    }

    /**
     * Sets the units of this instance to the given value.
     * 
     * @param units
     */
    public void setUnits(float units)
    {
        this.units = units;
    }

    /**
     * Uses the given pixels to calculate the unit value.
     * 
     * @param pixels
     */
    public void setPixels(float pixels)
    {
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
     * Gets the value for pixels. This value is calculated every call to ensure accuracy in case the ratio is changed.
     * 
     * @return
     */
    public float pixels()
    {
        return this.units * Unit.ratio;
    }

    protected Unit()
    {
    }

    @Override
    public String toString()
    {
        return "Pixels: " + pixels() + "  Units: " + this.units;
    }
}