package bt.game.core.scene;

import java.awt.Graphics;

import bt.runtime.Killable;

/**
 * @author &#8904
 *
 */
public interface Scene extends Killable
{
    public void load(String name);

    public void tick();

    public void render(Graphics g);
}