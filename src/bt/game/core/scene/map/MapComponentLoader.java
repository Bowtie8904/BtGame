package bt.game.core.scene.map;

import bt.game.core.scene.intf.Scene;
import bt.game.core.scene.map.intf.LineMapComponent;
import bt.game.core.scene.map.intf.MapComponent;
import bt.game.core.scene.map.intf.RectangularMapComponent;
import bt.game.resource.load.exc.LoadException;
import bt.game.resource.load.intf.Loader;
import bt.game.util.unit.Coordinate;
import bt.game.util.unit.Unit;
import bt.io.json.JSON;
import bt.reflect.classes.Classes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapComponentLoader implements Loader
{
    protected String resourceDir;
    protected Scene scene;
    protected Map<String, MapComponent> components;

    /**
     * Creates a new instance and sets the directory that contains the map files for the {@link #load(String)}
     * implementation.
     *
     * @param resourceDir
     */
    public MapComponentLoader(Scene scene, File resourceDir)
    {
        this(scene, resourceDir.getAbsolutePath());
    }

    /**
     * Creates a new instance and sets the directory that contains the map files for the {@link #load(String)}
     * implementation.
     *
     * @param resourcePath
     */
    public MapComponentLoader(Scene scene, String resourcePath)
    {
        this.resourceDir = resourcePath;
        this.scene = scene;
    }

    public MapComponent getComponent(String name)
    {
        return this.components.get(name);
    }

    /**
     * Loads all defined components by instantiating their classes and
     * calling their {@link MapComponent#initMapComponent(Scene, Unit, Unit, Unit, Unit, Unit) initMapComponent} method.
     * <p>
     * Any configured component classes must implement {@link MapComponent} and must have a no-args constructor.
     * <p>
     * Possible component declarations:
     * <ul>
     *     <li>rectangularComponents: Implementations of the {@link RectangularMapComponent} interface. Single coordinate, rectangular objects.</li>
     *     <li>lineComponents: Implementations of the {@link RectangularMapComponent} interface. Single coordinate, rectangular objects.</li>
     * </ul>
     * <p>
     * Each component can have another optional json object "additionInfo" which can be customized to give some additional
     * type specific information to the instance.
     *
     * <pre>
     * {
     * "map":
     * {
     *  "rectangularComponents":
     *  [
     *  {
     *      "className":"fully.qualified.name.MyClass",
     *      "x":"111",
     *      "y":"54",
     *      "z":"-1",
     *      "width":"89",
     *      "height":"566",
     *      "additionalInfo": {...}
     *  },
     *  ...
     *  ]
     *  "lineComponents":
     *  [
     *  {
     *      "className":"fully.qualified.name.MyClass2",
     *      "coordinates":
     *      [
     *          {
     *              "x":"4563",
     *              "y":"56"
     *          },
     *          {
     *              "x":"3",
     *              "y":"3457"
     *          },
     *          ...
     *      ],
     *      "z":"-1",
     *      "additionalInfo": {...}
     *  },
     *  ...
     *  ]
     * }
     * }
     * </pre>
     */
    @Override
    public void load(String name)
    {
        JSONObject json = getJsonForName(name);

        if (json == null)
        {
            throw new LoadException(String.format("[%s] Failed to read JSON map file.", name));
        }

        json = json.getJSONObject("map");

        if (json.has("width"))
        {
            this.scene.setWidth(Unit.forUnits(json.getDouble("width")));
        }

        if (json.has("height"))
        {
            this.scene.setWidth(Unit.forUnits(json.getDouble("width")));
        }

        loadRectangularComponents(name, json);
        loadLineComponents(name, json);
    }

    protected void loadRectangularComponents(String name, JSONObject json)
    {
        if (json.has("rectangularComponents"))
        {
            JSONObject obj;
            String className;
            Unit x;
            Unit y;
            Unit z;
            Unit w;
            Unit h;

            JSONArray compArray = json.getJSONArray("rectangularComponents");

            for (int i = 0; i < compArray.length(); i++)
            {
                obj = compArray.getJSONObject(i);

                if (obj.has("className"))
                {
                    className = obj.getString("className");
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define className.", name));
                }

                if (obj.has("x"))
                {
                    x = Unit.forUnits(obj.getDouble("x"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define x.", name));
                }

                if (obj.has("y"))
                {
                    y = Unit.forUnits(obj.getDouble("y"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define y.", name));
                }

                if (obj.has("z"))
                {
                    z = Unit.forUnits(obj.getDouble("z"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define z.", name));
                }

                if (obj.has("width"))
                {
                    w = Unit.forUnits(obj.getDouble("width"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define width.", name));
                }

                if (obj.has("height"))
                {
                    h = Unit.forUnits(obj.getDouble("height"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define height.", name));
                }

                JSONObject additionalInfo = null;

                if (obj.has("additionalInfo"))
                {
                    additionalInfo = obj.getJSONObject("additionalInfo");
                }

                try
                {
                    RectangularMapComponent component = createMapComponentInstance(className);
                    component.initMapComponent(this.scene, x, y, z, w, h, additionalInfo);

                    if (obj.has("name"))
                    {
                        this.components.put(obj.getString("name"), component);
                    }

                    System.out.println(String.format("[%s] Initialized map component '%s' with x=%f, y=%f, z=%f, w=%f, h=%f.",
                                                     name,
                                                     className,
                                                     x.units(),
                                                     y.units(),
                                                     z.units(),
                                                     w.units(),
                                                     h.units()));
                }
                catch (Exception e)
                {
                    throw new LoadException(String.format("[%s] Failed to set up component for class %s.", name, className), e);
                }
            }
        }
    }

    protected void loadLineComponents(String name, JSONObject json)
    {
        if (json.has("lineComponents"))
        {
            JSONObject obj;
            JSONObject coordObj;
            String className;
            Unit x;
            Unit y;
            Unit z;
            List<Coordinate> coordinates = new ArrayList<>();

            JSONArray compArray = json.getJSONArray("lineComponents");

            for (int i = 0; i < compArray.length(); i++)
            {
                obj = compArray.getJSONObject(i);

                if (obj.has("className"))
                {
                    className = obj.getString("className");
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define className.", name));
                }

                JSONArray coordArray = obj.getJSONArray("coordinates");

                for (int j = 0; j < coordArray.length(); j++)
                {
                    coordObj = coordArray.getJSONObject(j);

                    if (coordObj.has("x"))
                    {
                        x = Unit.forUnits(coordObj.getDouble("x"));
                    }
                    else
                    {
                        throw new LoadException(String.format("[%s] Component coordinate does not define x.", name));
                    }

                    if (coordObj.has("y"))
                    {
                        y = Unit.forUnits(coordObj.getDouble("y"));
                    }
                    else
                    {
                        throw new LoadException(String.format("[%s] Component coordinate does not define y.", name));
                    }

                    coordinates.add(new Coordinate(x, y));
                }

                if (obj.has("z"))
                {
                    z = Unit.forUnits(obj.getDouble("z"));
                }
                else
                {
                    throw new LoadException(String.format("[%s] Component does not define z.", name));
                }

                JSONObject additionalInfo = null;

                if (obj.has("additionalInfo"))
                {
                    additionalInfo = obj.getJSONObject("additionalInfo");
                }

                try
                {
                    LineMapComponent component = createMapComponentInstance(className);
                    component.initMapComponent(this.scene, z, coordinates.toArray(Coordinate[]::new), additionalInfo);

                    if (obj.has("name"))
                    {
                        this.components.put(obj.getString("name"), component);
                    }

                    System.out.println(String.format("[%s] Initialized map component '%s' with z=%f, coords=%s.",
                                                     name,
                                                     className,
                                                     z.units(),
                                                     coordinates));
                }
                catch (Exception e)
                {
                    throw new LoadException(String.format("[%s] Failed to set up component for class %s.", name, className), e);
                }
            }
        }
    }

    /**
     * Attempts to find a file with the given name inside the defined directory (see the constructor). The first file
     * with the correct (case insensitive) name will be used. This method will try to parse the file content as json and
     * return the created {@link JSONObject}.
     *
     * <p>
     * The resource filer needs to have the file extension .map.
     * </p>
     *
     * @param name The context name = the name of the file (without file ending) to load from.
     * @return The parsed json from the file or null if parsing failed for any reason.
     */
    protected JSONObject getJsonForName(String name)
    {
        String jsonString = null;
        String path = this.resourceDir + "/" + name + ".map";

        try (var stream = getClass().getClassLoader().getResourceAsStream(path))
        {
            jsonString = new BufferedReader(new InputStreamReader(stream)).lines()
                                                                          .collect(Collectors.joining("\n"));
        }
        catch (Exception e)
        {
            throw new LoadException(String.format("[%s] Failed to read JSON file '%s'.", name, path), e);
        }

        return JSON.parse(jsonString);
    }

    protected <T extends MapComponent> T createMapComponentInstance(String className) throws ClassNotFoundException
    {
        T component = null;
        Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(className);
        component = (T)Classes.newInstance(cls);

        return component;
    }
}