package bt.game.core.obj.intf;

/**
 * Defines actions of an object that are executed every tick.
 * 
 * @author &#8904
 */
@FunctionalInterface
public interface Tickable
{
    /**
     * Defines actions of an object that are executed every tick.
     */
    public void tick();
}