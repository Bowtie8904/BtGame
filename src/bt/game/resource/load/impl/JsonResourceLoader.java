package bt.game.resource.load.impl;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import bt.game.resource.load.Loadable;
import bt.game.resource.load.ResourceLoader;
import bt.types.sound.Sound;
import bt.utils.files.FileUtils;
import bt.utils.json.JSON;
import bt.utils.log.Logger;

/**
 * An implementation of {@link ResourceLoader} which offers the same functionalities as {@link BaseResourceLoader} but
 * can additionally load resources that were defined in a json file.
 * 
 * <h3>The json file needs to be in the following format:</h3>
 * 
 * <p>
 * The arrays 'images', 'sounds', 'files' and 'fonts' are all optional. They can be empty, contain multiple entries or
 * not exist at all. <br>
 * The alias will be the resource name that the resource is mapped by.
 * </p>
 * 
 * <pre>
 * {
    "resource":
    {
        "images":
        [
            {
                "path":"resource/images/test.png",
                "alias":"test_image"
            },
            ...
        ],
        "sounds":
        [
            {
                "path":"resource/sounds/test.wav",
                "alias":"test_sound"
            },
            ...
        ],
        "files":
        [
            {
                "path":"resource/files/test.txt",
                "alias":"test_file"
            },
            ...
        ],
        "fonts":
        [
            {
                "path":"resource/fonts/test.ttf",
                "alias":"test_font",
                "type":"truetype" //either 'truetype' or 'type1'
            },
            ...
        ]
    }
}
 * </pre>
 * 
 * 
 * @author &#8904
 */
public class JsonResourceLoader extends BaseResourceLoader
{
    private File resourceDir;

    /**
     * Creates a new instance and sets the directory that contains the json files for the {@link #load(String)}
     * implementation.
     * 
     * @param resourceDir
     *            The directory which contains the json files that define additional resources to load. If the directory
     *            does not exist it will be created.
     */
    public JsonResourceLoader(File resourceDir)
    {
        if (!resourceDir.exists())
        {
            try
            {
                resourceDir.mkdirs();
            }
            catch (Exception e)
            {
                Logger.global().print(e);
            }
        }

        if (!resourceDir.isDirectory())
        {
            throw new IllegalArgumentException("The given file must be a directory.");
        }

        this.resourceDir = resourceDir;
    }

    /**
     * Loads all {@link #register(Loadable) registered} {@link Loadable}s by calling their load methods and mapping
     * their returned values in this instance.
     * 
     * <p>
     * Additionally resources defined in a json file with the same name as the given context name and the file ending
     * .json will be loaded.
     * </p>
     * 
     * <h3>The json file needs to be in the following format:</h3>
     * 
     * <p>
     * The arrays 'images', 'sounds', 'files' and 'fonts' are all optional. They can be empty, contain multiple entries
     * or not exist at all. <br>
     * The alias will be the resource name that the resource is mapped by. This is case insensitive.
     * </p>
     * 
     * <pre>
     * {
        "resource":
        {
            "images":
            [
                {
                    "path":"resource/images/test.png",
                    "alias":"test_image"
                },
                ...
            ],
            "sounds":
            [
                {
                    "path":"resource/sounds/test.wav",
                    "alias":"test_sound"
                },
                ...
            ],
            "files":
            [
                {
                    "path":"resource/files/test.txt",
                    "alias":"test_file"
                },
                ...
            ],
            "fonts":
            [
                {
                    "path":"resource/fonts/test.ttf",
                    "alias":"test_font",
                    "type":"truetype" //either 'truetype' or 'type1'
                },
                ...
            ]
        }
    }
     * </pre>
     * 
     * @see bt.game.resource.load.ResourceLoader#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        super.load(name);

        JSONObject json = getJsonForName(name).getJSONObject("resource");

        JSONObject obj;
        String alias;
        String path;

        if (json.has("sounds"))
        {
            JSONArray soundArray = json.getJSONArray("sounds");

            for (int i = 0; i < soundArray.length(); i ++ )
            {
                obj = soundArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                add(alias, new Sound(new File(path)));
                Logger.global().print("Loaded sound '" + alias + "' from path '" + path + "'.");
            }
        }

        if (json.has("images"))
        {
            JSONArray imageArray = json.getJSONArray("images");

            for (int i = 0; i < imageArray.length(); i ++ )
            {
                obj = imageArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                try
                {
                    add(alias, ImageIO.read(new File(path)));
                    Logger.global().print("Loaded image '" + alias + "' from path '" + path + "'.");
                }
                catch (IOException e)
                {
                    Logger.global().print(e);
                }
            }
        }

        if (json.has("files"))
        {
            JSONArray fileArray = json.getJSONArray("files");

            for (int i = 0; i < fileArray.length(); i ++ )
            {
                obj = fileArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                add(alias, new File(path));
                Logger.global().print("Loaded file '" + alias + "' from path '" + path + "'.");
            }
        }

        if (json.has("fonts"))
        {
            JSONArray fontArray = json.getJSONArray("fonts");
            String type;

            for (int i = 0; i < fontArray.length(); i ++ )
            {
                obj = fontArray.getJSONObject(i);
                alias = obj.getString("alias");
                path = obj.getString("path");
                type = obj.getString("type");
                try
                {
                    add(alias, Font.createFont(type.equalsIgnoreCase("truetype") ? Font.TRUETYPE_FONT : Font.TYPE1_FONT,
                            new File(path)));
                    Logger.global().print("Loaded font '" + alias + "' from path '" + path + "'.");
                }
                catch (Exception e)
                {
                    Logger.global().print(e);
                }
            }
        }
    }

    /**
     * Attempts to find a file with the given name inside the defined directory (see the constructor). The first file
     * with the correct (case insensitive) name will be used. This method will try to parse the file content as json and
     * return the created {@link JSONObject}.
     * 
     * @param name
     *            The context name = the name of the file (without file ending) to load from.
     * @return The parsed json from the file or null if parsing failed for any reason.
     */
    private JSONObject getJsonForName(String name)
    {
        File[] files = FileUtils.getFiles(this.resourceDir.getAbsolutePath(), "json");
        File jsonFile = null;

        for (File file : files)
        {
            if (file.getName().equalsIgnoreCase(name + ".json"))
            {
                jsonFile = file;
                break;
            }
        }

        return JSON.parse(jsonFile);
    }
}