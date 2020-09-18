package bt.game.resource.load.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import bt.game.resource.load.intf.TextLoader;
import bt.game.resource.text.Text;
import bt.io.json.JSON;
import bt.log.Logger;

/**
 * @author &#8904
 *
 */
public class JsonTextLoader extends BaseTextLoader
{
    private String resourceDir;

    public JsonTextLoader(File resourceDir)
    {
        this(resourceDir.getAbsolutePath());
    }

    public JsonTextLoader(String resourcePath)
    {
        this.resourceDir = resourcePath;
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
        String jsonString = null;
        String path = this.resourceDir + "/" + name + ".lang";

        try (var stream = getClass().getClassLoader().getResourceAsStream(path))
        {
            jsonString = new BufferedReader(new InputStreamReader(stream))
                                                                          .lines()
                                                                          .collect(Collectors.joining("\n"));
        }
        catch (Exception e)
        {
            Logger.global().print(e);
        }

        return JSON.parse(jsonString);
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
        String language = null;
        Text textObj = null;
        int count = 0;

        for (int i = 0; i < jsonTextArray.length(); i ++ )
        {
            text = null;
            jsonLanguageArray = null;
            jsonLanguageObj = null;
            jsonTextObj = jsonTextArray.getJSONObject(i);

            if (jsonTextObj.has("id"))
            {
                int id = jsonTextObj.getInt("id");

                if (this.loadMode == TextLoader.LAZY_LOADING)
                {
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
                                    language = jsonLanguageObj.getString("language");
                                    break;
                                }
                            }
                        }
                    }

                    if (text == null)
                    {
                        text = "* " + id + " *";
                    }

                    textObj = new Text(id,
                                       text);
                    textObj.setLanguage(language == null ? "EN" : language);
                    add(id,
                        textObj);
                    count ++ ;
                }
                else if (this.loadMode == TextLoader.EAGER_LOADING)
                {
                    if (jsonTextObj.has("languages"))
                    {
                        jsonLanguageArray = jsonTextObj.getJSONArray("languages");

                        for (int k = 0; k < jsonLanguageArray.length(); k ++ )
                        {
                            jsonLanguageObj = jsonLanguageArray.getJSONObject(k);

                            if (jsonLanguageObj.has("text"))
                            {
                                text = jsonLanguageObj.getString("text");
                                language = jsonLanguageObj.getString("language");
                            }
                            else
                            {
                                text = null;
                            }

                            if (text == null)
                            {
                                text = "* " + id + " *";
                            }

                            textObj = new Text(id,
                                               text);
                            textObj.setLanguage(language == null ? "EN" : language);
                            add(id,
                                textObj);
                            count ++ ;
                        }
                    }
                }
            }
        }

        Logger.global()
              .printf("[%s] Loaded %d texts from language file.",
                      name,
                      count);
    }
}