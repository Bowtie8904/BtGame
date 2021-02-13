package bt.game.resource.load.impl;

import bt.game.resource.load.intf.Loadable;
import bt.game.resource.load.intf.ResourceLoader;
import bt.game.resource.render.impl.RenderableGif;
import bt.game.resource.render.impl.RenderableImage;
import bt.game.resource.render.impl.anim.Animation;
import bt.io.json.JSON;
import bt.io.sound.SoundSupplier;
import bt.runtime.InstanceKiller;
import bt.types.Killable;
import bt.utils.ImageUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ResourceLoader} which offers the same functionalities as {@link BaseResourceLoader} but
 * can additionally load resources that were defined in a json (.res) file.
 *
 * <h3>The json (.res) file needs to be in the following format:</h3>
 *
 * <p>
 * The arrays 'images', 'gifs', 'sounds', 'files', 'fonts' and 'animations' are all optional. They can be empty, contain
 * multiple entries or not exist at all. <br>
 * The alias will be the resource name that the resource is mapped by.
 * </p>
 *
 * <pre>
 * {
 * "resource":
 * {
 * "images":
 * [
 * {
 * "path":"resource/images/test.png",
 * "alias":"test_image"
 * },
 * ...
 * ],
 * "gifs":
 * [
 * {
 * "path":"resource/images/test.gif",
 * "alias":"test_gif"
 * },
 * ...
 * ],
 * "sounds":
 * [
 * {
 * "path":"resource/sounds/test.wav",
 * "alias":"test_sound"
 * },
 * ...
 * ],
 * "files":
 * [
 * {
 * "path":"resource/files/test.txt",
 * "alias":"test_file"
 * },
 * ...
 * ],
 * "fonts":
 * [
 * {
 * "path":"resource/fonts/test.ttf",
 * "alias":"test_font",
 * "type":"truetype" //either 'truetype' or 'type1'
 * },
 * ...
 * ],
 * "animations":
 * [
 * {
 * "interval":1000,
 * "alias":"test_font",
 * "images":
 * [
 * "test_image",
 * ...
 * ]
 * },
 * ...
 * ]
 * }
 * }
 * </pre>
 *
 * @author &#8904
 */
public class JsonResourceLoader extends BaseResourceLoader
{
    private String resourceDir;
    private File lastResourceFile;

    /**
     * Creates a new instance and sets the directory that contains the json files for the {@link #load(String)}
     * implementation.
     *
     * <p>
     * This constructor will add the instance to the {@link InstanceKiller} via
     * {@link InstanceKiller#killOnShutdown(Killable) killOnShutdown} to close resources on application shutdown. The
     * one controlling this resource loader should however call {@link #kill()} and
     * {@link InstanceKiller#unregister(Killable) unregister} the instance as soon as the resources are not needed
     * anymore.
     * </p>
     *
     * @param resourceDir The directory which contains the json (.res) files that define additional resources to load. If the
     *                    directory does not exist it will be created.
     */
    public JsonResourceLoader(File resourceDir)
    {
        this(resourceDir.getAbsolutePath());
    }

    public JsonResourceLoader(String resourcePath)
    {
        this.resourceDir = resourcePath;
    }

    /**
     * Loads all {@link #register(Loadable) registered} {@link Loadable}s by calling their load methods and mapping
     * their returned values in this instance.
     *
     * <p>
     * Additionally resources defined in a json file with the same name as the given context name and the file ending
     * .res will be loaded.
     * </p>
     *
     * <h3>The json (.res) file needs to be in the following format:</h3>
     *
     * <p>
     * The arrays 'images', 'gifs', 'sounds', 'files', 'fonts' and 'animations' are all optional. They can be empty,
     * contain multiple entries or not exist at all. <br>
     * The alias will be the resource name that the resource is mapped by. This is case insensitive.
     * </p>
     *
     * <pre>
     * {
     * "resource":
     * {
     * "images":
     * [
     * {
     * "path":"resource/images/test.png",
     * "alias":"test_image"
     * },
     * ...
     * ],
     * "gifs":
     * [
     * {
     * "path":"resource/images/test.gif",
     * "alias":"test_gif"
     * },
     * ...
     * ],
     * "sounds":
     * [
     * {
     * "path":"resource/sounds/test.wav",
     * "alias":"test_sound"
     * },
     * ...
     * ],
     * "files":
     * [
     * {
     * "path":"resource/files/test.txt",
     * "alias":"test_file"
     * },
     * ...
     * ],
     * "fonts":
     * [
     * {
     * "path":"resource/fonts/test.ttf",
     * "alias":"test_font",
     * "type":"truetype" //either 'truetype' or 'type1'
     * },
     * ...
     * ],
     * "animations":
     * [
     * {
     * "interval":1000,
     * "alias":"test_font",
     * "images":
     * [
     * "test_image",
     * ...
     * ]
     * },
     * ...
     * ]
     * }
     * }
     * </pre>
     *
     * @see bt.game.resource.load.intf.ResourceLoader#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        super.load(name);

        JSONObject json = getJsonForName(name);

        if (json == null)
        {
            return;
        }

        json = json.getJSONObject("resource");

        JSONObject obj;
        String alias;
        String path;

        if (json.has("sounds"))
        {
            JSONArray soundArray = json.getJSONArray("sounds");

            for (int i = 0; i < soundArray.length(); i++)
            {
                obj = soundArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                add(alias,
                    new SoundSupplier(
                            new BufferedInputStream(JsonResourceLoader.class.getResourceAsStream(path))));
                System.out.println(String.format("[%s] Loaded sound '%s' from path '%s'.",
                                                 name,
                                                 alias,
                                                 path));
            }
        }

        if (json.has("images"))
        {
            JSONArray imageArray = json.getJSONArray("images");

            for (int i = 0; i < imageArray.length(); i++)
            {
                obj = imageArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                try
                {
                    add(alias,
                        new RenderableImage(ImageIO.read(JsonResourceLoader.class.getResourceAsStream(path))));
                    System.out.println(String.format("[%s] Loaded image '%s' from path '%s'.",
                                                     name,
                                                     alias,
                                                     path));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (json.has("gifs"))
        {
            JSONArray imageArray = json.getJSONArray("gifs");

            for (int i = 0; i < imageArray.length(); i++)
            {
                obj = imageArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                try
                {
                    add(alias,
                        new RenderableGif(ImageUtils.getImageIcon(JsonResourceLoader.class.getResourceAsStream(path))));
                    System.out.println(String.format("[%s] Loaded gif '%s' from path '%s'.",
                                                     name,
                                                     alias,
                                                     path));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (json.has("files"))
        {
            JSONArray fileArray = json.getJSONArray("files");

            for (int i = 0; i < fileArray.length(); i++)
            {
                obj = fileArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                add(alias,
                    new File(path));
                System.out.println(String.format("[%s] Loaded file '%s' from path '%s'.",
                                                 name,
                                                 alias,
                                                 path));
            }
        }

        if (json.has("fonts"))
        {
            JSONArray fontArray = json.getJSONArray("fonts");
            String type;

            for (int i = 0; i < fontArray.length(); i++)
            {
                obj = fontArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                type = obj.getString("type");
                try
                {
                    add(alias,
                        Font.createFont(type.equalsIgnoreCase("truetype") ? Font.TRUETYPE_FONT : Font.TYPE1_FONT,
                                        JsonResourceLoader.class.getResourceAsStream(path)));
                    System.out.println(String.format("[%s] Loaded font '%s' from path '%s'.",
                                                     name,
                                                     alias,
                                                     path));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        if (json.has("animations"))
        {
            JSONArray animationArray = json.getJSONArray("animations");
            JSONArray imageArray = null;
            JSONObject jsonImage;
            String type;
            int interval = 0;
            String[] images;

            for (int i = 0; i < animationArray.length(); i++)
            {
                obj = animationArray.getJSONObject(i);
                alias = obj.getString("alias");
                interval = obj.getInt("interval");
                imageArray = obj.getJSONArray("images");
                images = new String[imageArray.length()];

                for (int j = 0; j < imageArray.length(); j++)
                {
                    images[j] = imageArray.getString(j);
                }

                try
                {
                    add(alias,
                        new Animation(this,
                                      interval,
                                      images));
                    System.out.println(String.format("[%s] Loaded animation '%s' defined in '%s'.",
                                                     name,
                                                     alias,
                                                     this.lastResourceFile.getAbsolutePath()));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
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
     * The resource filer needs to have the file extension .res.
     * </p>
     *
     * @param name The context name = the name of the file (without file ending) to load from.
     * @return The parsed json from the file or null if parsing failed for any reason.
     */
    private JSONObject getJsonForName(String name)
    {
        String jsonString = null;
        String path = this.resourceDir + "/" + name + ".res";

        try (var stream = getClass().getClassLoader().getResourceAsStream(path))
        {
            jsonString = new BufferedReader(new InputStreamReader(stream)).lines()
                                                                          .collect(Collectors.joining("\n"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.lastResourceFile = new File(path);

        return JSON.parse(jsonString);
    }
}