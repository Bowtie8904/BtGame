package bt.game.resource.render.impl.anim;

import bt.game.core.obj.intf.Tickable;
import bt.game.resource.render.impl.BaseRenderable;
import bt.game.resource.render.impl.RenderableImage;
import bt.game.util.unit.Unit;
import bt.utils.NumberUtils;

import java.awt.*;

public class EmitterImage extends BaseRenderable implements Tickable
{
    private double rotation;
    private double alphaLoss;
    private RenderableImage image;
    private Unit rotationOffsetX;
    private Unit rotationOffsetY;
    private double currentAlpha = 1;

    public EmitterImage(RenderableImage image, Unit x, Unit y, Unit w, Unit h,
                        double rotation, Unit rotationOffsetX, Unit rotationOffsetY)
    {
        this.image = image;
        this.rotation = rotation;
        this.rotationOffsetX = rotationOffsetX;
        this.rotationOffsetY = rotationOffsetY;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setAlphaLoss(double alphaLoss)
    {
        this.alphaLoss = alphaLoss;
    }

    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        this.image.render(NumberUtils.clamp((float)this.currentAlpha, 0, 1),
                          g, this.x, this.y, this.w, this.h,
                          this.rotation, this.rotationOffsetX,
                          this.rotationOffsetY, debugRendering);
    }

    @Override
    public void tick(double delta)
    {
        this.currentAlpha -= this.alphaLoss * delta;
    }

    public double getCurrentAlpha()
    {
        return this.currentAlpha;
    }
}
