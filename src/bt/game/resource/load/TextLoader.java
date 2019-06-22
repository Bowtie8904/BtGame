package bt.game.resource.load;

import bt.game.resource.text.Text;
import bt.runtime.Killable;

/**
 * @author &#8904
 */
public interface TextLoader extends Killable, Loader
{
    public String getLanguage();

    public void setLanguage(String language);

    public Text get(int id);

    public void add(int id, Text text);

    public void register(Loadable loadable);
}