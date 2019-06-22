package bt.game.resource.load.impl;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import bt.game.resource.text.Text;
import bt.utils.files.FileUtils;
import bt.utils.json.JSON;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public class JsonTextLoader extends BaseTextLoader
{
    private File resourceDir;

    public JsonTextLoader(File resourceDir)
    {
        super();
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
     * Attempts to find a file with the given name inside the defined directory (see the constructor). The first file
     * with the correct (case insensitive) name will be used. This method will try to parse the file content as json and
     * return the created {@link JSONObject}.
     * 
     * <p>
     * The resource filer needs to have the file extension .lang.
     * </p>
     * 
     * @param name
     *            The context name = the name of the file (without file ending) to load from.
     * @return The parsed json from the file or null if parsing failed for any reason.
     */
    private JSONObject getJsonForName(String name)
    {
        File[] files = FileUtils.getFiles(this.resourceDir.getAbsolutePath(), "lang");
        File jsonFile = null;

        for (File file : files)
        {
            if (file.getName().equalsIgnoreCase(name + ".lang"))
            {
                jsonFile = file;
                break;
            }
        }

        return JSON.parse(jsonFile);
    }

    /**
     * 
     * 
     * 
     * <pre>
     {
        "texts":
        [
            {
                "id":123,
                "languages":
                [
                    {
                        "language":"en",
                        "text":"Play"
                    },
                    {
                        "language":"de",
                        "text":"Spielen"
                    },
                    ...
                ]
            },
            {
                "id":157,
                "languages":
                [
                    {
                        "language":"en",
                        "text":"Exit"
                    },
                    {
                        "language":"de",
                        "text":"Beenden"
                    }
                ]
            },
            ...
        ]
     }
     * </pre>
     */
    @Override
    public void load(String name)
    {
        super.load(name);
        JSONObject jsonFile = getJsonForName(name);

        if (jsonFile == null)
        {
            return;
        }

        JSONArray jsonTextArray = jsonFile.getJSONArray("texts");

        JSONObject jsonTextObj = null;
        JSONArray jsonLanguageArray = null;
        JSONObject jsonLanguageObj = null;
        String text = null;

        for (int i = 0; i < jsonTextArray.length(); i ++ )
        {
            text = null;
            jsonLanguageArray = null;
            jsonLanguageObj = null;
            jsonTextObj = jsonTextArray.getJSONObject(i);

            if (jsonTextObj.has("id"))
            {
                int id = jsonTextObj.getInt("id");

                if (jsonTextObj.has("languages"))
                {
                    jsonLanguageArray = jsonTextObj.getJSONArray("languages");

                    for (int k = 0; k < jsonLanguageArray.length(); k ++ )
                    {
                        jsonLanguageObj = jsonLanguageArray.getJSONObject(k);

                        if (this.language.equalsIgnoreCase(jsonLanguageObj.getString("language")))
                        {
                            if (jsonLanguageObj.has("text"))
                            {
                                text = jsonLanguageObj.getString("text");
                                break;
                            }
                        }
                    }
                }

                if (text == null)
                {
                    text = "* " + id + " *";
                }

                add(id, new Text(id, text));
            }
        }
    }
}