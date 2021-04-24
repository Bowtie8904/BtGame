package bt.game.resource.render.light.mask;

import bt.game.util.unit.Unit;
import bt.types.Killable;

import java.awt.*;

public interface LightMask extends Killable
{
    public static Color DARKNESS = new Color(0, 0, 0, 100);

    public void apply(Graphics2D g, Unit lightX, Unit lightY);
}