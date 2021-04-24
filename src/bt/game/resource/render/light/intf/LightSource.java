package bt.game.resource.render.light.intf;

import bt.game.resource.render.light.mask.LightMask;
import bt.game.util.unit.Unit;

public interface LightSource
{
    public Unit getLightX();

    public Unit getLightY();

    public LightMask getLightMask();
}