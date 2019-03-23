package bt.game.resource.render;

import java.awt.Graphics;

import bt.game.util.unit.Unit;
import bt.runtime.Killable;

/**
 * @author &#8904
 *
 */
public interface Renderable extends Killable
{
    public void render(Graphics g, Unit x, Unit y, Unit w, Unit h);
}