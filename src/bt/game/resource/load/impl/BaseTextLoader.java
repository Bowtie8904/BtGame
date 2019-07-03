package bt.game.resource.load.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bt.game.resource.load.Loadable;
import bt.game.resource.load.ResourceContainer;
import bt.game.resource.load.TextLoader;
import bt.game.resource.text.Text;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public class BaseTextLoader implements TextLoader
{
    protected int loadMode;
    protected String language = "EN";
    protected Map<String, Map<Integer, Text>> texts;
    protected List<Loadable> loadables;

    public BaseTextLoader()
    {
        this.texts = new HashMap<>();
        this.loadables = new ArrayList<>();
        this.loadMode = TextLoader.LAZY_LOADING;
    }

    /**
     * @see bt.game.resource.load.TextLoader#getLanguage()
     */
    @Override
    public String getLanguage()
    {
        return this.language;
    }

    /**
     * @see bt.game.resource.load.TextLoader#setLanguage(java.lang.String)
     */
    @Override
    public void setLanguage(String language)
    {
        this.language = language.toUpperCase();
    }

    /**
     * @see bt.game.resource.load.TextLoader#get(int)
     */
    @Override
    public Text get(int id)
    {
        var textsForLanguage = this.texts.get(this.language);

        Text text = null;

        if (textsForLanguage != null)
        {
            text = textsForLanguage.get(id);
        }

        if (text == null)
        {
            text = new Text(id, "* " + id + " *");
            text.setLanguage(this.language);
            add(id, text);
        }

        return text;
    }

    /**
     * @see bt.game.resource.load.TextLoader#add(int, bt.game.resource.text.Text)
     */
    @Override
    public void add(int id, Text text)
    {
        var textsForLanguage = this.texts.get(text.getLanguage());

        if (textsForLanguage == null)
        {
            textsForLanguage = new HashMap<Integer, Text>();
            this.texts.put(text.getLanguage(), textsForLanguage);
        }

        this.texts.get(text.getLanguage()).put(id, text);
    }

    /**
     * @see bt.game.resource.load.TextLoader#register(bt.game.resource.load.Loadable)
     */
    @Override
    public void register(Loadable loadable)
    {
        this.loadables.add(loadable);
    }

    /**
     * @see bt.game.resource.load.TextLoader#load(java.lang.String)
     */
    @Override
    public void load(String name)
    {
        this.texts = new HashMap<>();
        List<String> loadedClasses = new ArrayList<>();

        ResourceContainer container;

        for (Loadable loadable : this.loadables)
        {
            if (loadedClasses.contains(loadable.getClass().getName()))
            {
                continue;
            }
            else
            {
                loadedClasses.add(loadable.getClass().getName());
            }
            
            container = new ResourceContainer();
            loadable.load(name, container);

            int count = 0;
            Text text = null;

            var mappedTexts = container.getTexts();

            if (this.loadMode == TextLoader.LAZY_LOADING)
            {
                var textsForLanguage = mappedTexts.get(this.language);

                if (textsForLanguage != null)
                {
                    for (Integer id : textsForLanguage.keySet())
                    {
                        text = textsForLanguage.get(id);

                        if (this.language.equalsIgnoreCase(text.getLanguage()))
                        {
                            add(id, text);
                            count ++ ;
                        }
                    }
                }
            }
            else if (this.loadMode == TextLoader.EAGER_LOADING)
            {
                for (String lang : mappedTexts.keySet())
                {
                    var textsForLanguage = mappedTexts.get(lang);

                    for (Integer id : textsForLanguage.keySet())
                    {
                        add(id, textsForLanguage.get(id));
                        count ++ ;
                    }
                }
            }

            Logger.global()
                    .print("[" + name + "] Loaded " + count + " texts for " + loadable.getClass().getName()
                            + ".");
        }
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Clearing texts.");
        this.loadables.clear();
        this.texts.clear();
    }

    /**
     * @see bt.game.resource.load.TextLoader#setLoadMode(int)
     */
    @Override
    public void setLoadMode(int mode)
    {
        this.loadMode = mode;
    }

    /**
     * @see bt.game.resource.load.TextLoader#getLoadMode()
     */
    @Override
    public int getLoadMode()
    {
        return this.loadMode;
    }
}