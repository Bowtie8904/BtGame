package bt.game.resource.render.impl.anim;

import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.impl.BaseRenderable;
import bt.game.util.unit.Unit;

import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ImageEmitter extends BaseRenderable implements Tickable
{
    private List<EmitterImage> emittedImages;
    private double alphaLoss = -1;
    private Scene scene;

    public ImageEmitter(double alphaLoss, Unit z)
    {
        this.emittedImages = new LinkedList<>();
        this.alphaLoss = alphaLoss;
        this.z = z;
    }

    public synchronized void emit(EmitterImage image)
    {
        image.setAlphaLoss(this.alphaLoss);
        this.emittedImages.add(image);
    }

    public void registerToScene(Scene scene)
    {
        this.scene = scene;
        this.scene.getObjectHandler().addObject(this);
    }

    public void unregisterFromScene()
    {
        this.scene.getObjectHandler().removeObject(this);
    }

    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        for (EmitterImage image : this.emittedImages)
        {
            image.render(g, debugRendering);
        }
    }

    @Override
    public synchronized void tick(double delta)
    {
        if (this.emittedImages.size() > 0)
        {
            Iterator<EmitterImage> ite = this.emittedImages.iterator();
            EmitterImage image;

            while (ite.hasNext())
            {
                image = ite.next();
                image.tick(delta);

                if (image.getCurrentAlpha() <= 0)
                {
                    ite.remove();
                }
            }
        }
    }
}
