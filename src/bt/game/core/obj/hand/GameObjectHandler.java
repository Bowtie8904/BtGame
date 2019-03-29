package bt.game.core.obj.hand;

import java.awt.Graphics;

import bt.game.core.obj.GameObject;

/**
 * @author &#8904
 *
 */
public interface GameObjectHandler
{
    public void sortObjects();

    public void addObject(GameObject object);

    public void removeObject(GameObject object);

    public void tick();

    public void render(Graphics g);
}