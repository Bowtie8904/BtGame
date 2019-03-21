package bt.game.resource.load.impl;

import java.awt.Font;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import bt.game.resource.type.Sound;
import bt.utils.files.FileUtils;
import bt.utils.json.JSON;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public class JsonResourceLoader extends BaseResourceLoader
{
    private File resourceDir;

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
     * @see bt.game.resource.load.ResourceLoader#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
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
                this.sounds.put(alias, new Sound(new File(path)));
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
                    this.images.put(alias, ImageIO.read(new File(path)));
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
                this.files.put(alias, new File(path));
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
                    this.fonts.put(alias, Font.createFont(
                            type.equalsIgnoreCase("truetype") ? Font.TRUETYPE_FONT : Font.TYPE1_FONT, new File(path)));
                    Logger.global().print("Loaded font '" + alias + "' from path '" + path + "'.");
                }
                catch (Exception e)
                {
                    Logger.global().print(e);
                }
            }
        }
    }

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