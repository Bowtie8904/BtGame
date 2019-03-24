package bt.game.resource.render.impl;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

import bt.game.resource.render.Renderable;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class RenderableGif implements Renderable
{
    private Image image;

    public RenderableGif(ImageIcon image)
    {
        this.image = image.getImage();
    }

    /**
     * @see bt.game.resource.render.Renderable#render(java.awt.Graphics, bt.game.util.unit.Unit, bt.game.util.unit.Unit)
     */
    @Override
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h)
    {
        g.drawImage(image, (int)x.pixels(), (int)y.pixels(), (int)w.pixels(), (int)h.pixels(), null);
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        this.image.flush();
    }
}