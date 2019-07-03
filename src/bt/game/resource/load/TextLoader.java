package bt.game.resource.load;

import bt.game.resource.text.Text;
import bt.runtime.Killable;

/**
 * @author &#8904
 */
public interface TextLoader extends Killable, Loader
{
    /** Indicattes that all language variants of the texts should be loaded. */
    public static final int EAGER_LOADING = 1;

    /** Indicates that only the required language variant of the texts should be loaded. */
    public static final int LAZY_LOADING = 2;

    public String getLanguage();

    public void setLanguage(String language);

    public void setLoadMode(int mode);

    public int getLoadMode();

    public Text get(int id);

    public void add(int id, Text text);

    public void register(Loadable loadable);
}