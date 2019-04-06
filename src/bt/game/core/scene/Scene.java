package bt.game.core.scene;

import java.awt.Graphics;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.hand.GameObjectHandler;
import bt.game.resource.load.ResourceLoader;
import bt.game.util.unit.Unit;
import bt.runtime.Killable;

/**
 * @author &#8904
 *
 */
public interface Scene extends Killable
{
    public GameObjectHandler getGameObjectHandler();

    public ResourceLoader getResourceLoader();

    public GameContainer getGameContainer();

    public Unit getWidth();

    public Unit getHeight();

    public void load(String name);

    public boolean isLoaded();

    public void start();

    public void tick();

    public void render(Graphics g);
}