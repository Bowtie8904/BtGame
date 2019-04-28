package bt.game.core.scene;

import java.awt.Graphics2D;

import org.dyn4j.dynamics.World;

import bt.game.core.container.GameContainer;
import bt.game.core.obj.hand.ObjectHandler;
import bt.game.resource.load.ResourceLoader;
import bt.game.util.unit.Unit;
import bt.runtime.Killable;

/**
 * @author &#8904
 *
 */
public interface Scene extends Killable
{
    public ObjectHandler getObjectHandler();

    public ResourceLoader getResourceLoader();

    public GameContainer getGameContainer();

    public World getWorld();

    public Unit getWidth();

    public Unit getHeight();

    public void load(String name);

    public boolean isLoaded();

    public void start();

    public void tick(double delta);

    public void render(Graphics2D g);
}