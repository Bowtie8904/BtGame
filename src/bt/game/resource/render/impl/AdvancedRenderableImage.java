package bt.game.resource.render.impl;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

import java.awt.*;

/**
 * @author &#8904
 */
public class AdvancedRenderableImage extends BaseRenderable
{
    private String imageName;
    private Scene scene;

    public AdvancedRenderableImage(Scene scene, String imageName)
    {
        this.imageName = imageName;
        this.scene = scene;
    }

    @Override
    public void render(float alpha, Graphics2D g, Unit x, Unit y, Unit w, Unit h, double rotation, Unit rotationOffsetX, Unit rotationOffsetY, boolean debugRendering)
    {
        this.scene.getResourceLoader()
                  .getRenderable(this.imageName)
                  .render(alpha,
                          g,
                          x,
                          y,
                          w,
                          h,
                          rotation,
                          rotationOffsetX,
                          rotationOffsetY,
                          debugRendering);
    }
}