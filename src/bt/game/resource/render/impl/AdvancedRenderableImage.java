package bt.game.resource.render.impl;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;

import java.awt.*;

/**
 * @author &#8904
 */
public class AdvancedRenderableImage extends AdvancedRenderable
{
    private String imageName;
    private Scene scene;

    public AdvancedRenderableImage(Scene scene, String imageName)
    {
        this.imageName = imageName;
        this.scene = scene;
    }

    /**
     * @see bt.game.resource.render.intf.Renderable#render(java.awt.Graphics2D, bt.game.util.unit.Unit,
     * bt.game.util.unit.Unit, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics2D g, Unit x, Unit y, Unit w, Unit h, boolean debugRendering)
    {
        this.scene.getResourceLoader()
                  .getRenderable(this.imageName)
                  .render(g,
                          x,
                          y,
                          w,
                          h,
                          debugRendering);
    }
}