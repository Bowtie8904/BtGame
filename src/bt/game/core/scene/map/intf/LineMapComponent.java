package bt.game.core.scene.map.intf;

import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Coordinate;
import bt.game.util.unit.Unit;
import org.json.JSONObject;

public interface LineMapComponent extends MapComponent
{
    public void initMapComponent(Scene scene, Unit z, Coordinate[] coordinates, JSONObject additionalInfo);
}