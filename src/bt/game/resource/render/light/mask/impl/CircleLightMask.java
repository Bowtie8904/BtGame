package bt.game.resource.render.light.mask.impl;

import bt.game.resource.render.light.mask.LightMask;
import bt.game.util.unit.Unit;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CircleLightMask implements LightMask
{
    private static Unit radius = Unit.forUnits(1000);

    BufferedImage mask = new BufferedImage((int)CircleLightMask.radius.multiplyWith(2).pixels(),
                                           (int)CircleLightMask.radius.multiplyWith(2).pixels(),
                                           BufferedImage.TYPE_INT_ARGB);

    @Override
    public void apply(Graphics2D g, Unit lightX, Unit lightY)
    {
        /**
         Point2D center = new Point2D.Double(lightX.pixels(), lightY.pixels());
         float[] distance = { 0.0F, 1.0F };
         Color[] colors = { LightMask.DARKNESS, new Color(0, 0, 0, 0) };
         RadialGradientPaint p = new RadialGradientPaint(center, (float)this.radius.pixels(), distance, colors);
         g.setPaint(p);
         g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
         g.fillRect((int)lightX.subtractUnits(this.radius).pixels(),
         (int)lightY.subtractUnits(this.radius).pixels(),
         (int)this.radius.multiplyWith(2).pixels(),
         (int)this.radius.multiplyWith(2).pixels());
         */
    }

    @Override
    public void kill()
    {

    }
}