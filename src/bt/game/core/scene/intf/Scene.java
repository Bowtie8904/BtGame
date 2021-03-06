package bt.game.core.scene.intf;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.obj.hand.intf.ObjectHandler;
import bt.game.core.scene.cam.Camera;
import bt.game.core.scene.map.MapComponentLoader;
import bt.game.resource.load.intf.Loader;
import bt.game.resource.load.intf.ResourceLoader;
import bt.game.util.unit.Unit;
import bt.io.text.intf.TextLoader;
import bt.types.Killable;
import org.dyn4j.world.World;

import java.awt.*;

/**
 * @author &#8904
 */
public interface Scene extends Killable, Loader
{
    public ObjectHandler getObjectHandler();

    public ResourceLoader getResourceLoader();

    public TextLoader getTextLoader();

    public MapComponentLoader getMapComponentLoader();

    public GameContainer getGameContainer();

    public World getWorld();

    public Unit getWidth();

    public Unit getHeight();

    public void setWidth(Unit width);

    public void setHeight(Unit height);

    public boolean isLoaded();

    public void start();

    public void tick(double delta);

    public void render(Graphics2D g, boolean debugRendering);

    public void refresh();

    public void setCamera(Camera camera);

    public void setLit(boolean lit);

    public boolean isLit();
}