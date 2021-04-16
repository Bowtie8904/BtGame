package bt.game.core.scene.map.intf;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import org.json.JSONObject;

public interface RectangularMapComponent extends MapComponent
{
    public void initMapComponent(Scene scene, Unit x, Unit y, Unit z, Unit w, Unit h, JSONObject additionalInfo);
}