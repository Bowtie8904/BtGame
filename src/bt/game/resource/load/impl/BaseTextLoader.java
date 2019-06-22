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
    protected String language = "EN";
    protected Map<Integer, Text> texts;
    protected List<Loadable> loadables;

    public BaseTextLoader()
    {
        this.texts = new HashMap<>();
        this.loadables = new ArrayList<>();
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
        this.language = language;
    }

    /**
     * @see bt.game.resource.load.TextLoader#get(int)
     */
    @Override
    public Text get(int id)
    {
        Text text = this.texts.get(id);

        if (text == null)
        {
            text = new Text(id, "* " + id + " *");
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
        this.texts.put(id, text);
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

            for (Integer id : container.getTexts().keySet())
            {
                text = container.getTexts().get(id);

                if (this.language.equalsIgnoreCase(text.getLanguage()))
                {
                    add(id, text);
                    count ++ ;
                }
            }

            Logger.global()
                    .print("Loaded " + count + " texts for " + loadable.getClass().getName()
                            + ".");
        }
    }

    /**
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        this.loadables.clear();
        this.texts.clear();
    }
}