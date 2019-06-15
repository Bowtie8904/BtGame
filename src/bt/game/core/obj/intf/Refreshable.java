package bt.game.core.obj.intf;

/**
 * Interface defining an object that can be refreshed.
 * 
 * @author &#8904
 */
public interface Refreshable
{
    /**
     * Called when the object should refresh its data.
     * 
     * <p>
     * This can for example be the case when the window size changes and objects should recalculate their hitboxes.
     * </p>
     */
    public void refresh();
}