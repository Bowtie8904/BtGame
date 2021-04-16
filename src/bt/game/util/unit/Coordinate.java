package bt.game.util.unit;

import java.util.Objects;

public class Coordinate
{
    private Unit x;
    private Unit y;

    public Coordinate(Unit x, Unit y)
    {
        this.x = x;
        this.y = y;
    }

    public Unit getX()
    {
        return this.x;
    }

    public Unit getY()
    {
        return this.y;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Coordinate that = (Coordinate)o;
        return Objects.equals(this.x, that.x) && Objects.equals(this.y, that.y);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString()
    {
        return "Coordinate{" +
                "x=" + this.x +
                ", y=" + this.y +
                '}';
    }
}