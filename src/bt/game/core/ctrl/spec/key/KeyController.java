package bt.game.core.ctrl.spec.key;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author &#8904
 *
 */
public class KeyController implements KeyListener
{
    private static KeyController instance;

    public static KeyController get()
    {
        return instance;
    }

    private static final int KEY_NOT_DOWN = 0;
    private static final int KEY_DOWN = 1;
    private static final int KEY_RELEASED = 2;

    private Map<Integer, Integer> keyValues;
    private Map<Integer, Integer> keyChanges;

    public KeyController(Component component)
    {
        instance = this;
        this.keyValues = new HashMap<>();
        this.keyChanges = new HashMap<>();
        component.addKeyListener(this);
    }

    /**
     * Indicates whether a key is currently being pressed.
     * 
     * @param key
     *            The key code (constant from {@link KeyEvent}) of the key to check.
     * @return true if the key is being pressed.
     */
    public boolean isKeyDown(int key)
    {
        Integer value = this.keyValues.get(key);
        return value != null && value == 1;
    }

    /**
     * Indicates whether a key was recently pressed and is now released.
     * 
     * @param key
     *            The key code (constant from {@link KeyEvent}) of the key to check.
     * @return true if the key has been pressed somewhere in the past and was released during the last tick.
     */
    public boolean isKeyReleased(int key)
    {
        Integer value = this.keyValues.get(key);
        return value != null && value == 2;
    }

    /**
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        synchronized (this.keyChanges)
        {
            this.keyChanges.put(e.getKeyCode(), 1);
        }
    }

    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public void keyReleased(KeyEvent e)
    {
        synchronized (this.keyChanges)
        {
            this.keyChanges.put(e.getKeyCode(), 2);
        }
    }

    public void checkKeyChanges()
    {
        // changing 'recently released' to 'not down'
        this.keyValues.replaceAll((k, v) -> {
            if (v == 2)
            {
                return 0;
            }
            return v;
        });

        synchronized (this.keyChanges)
        {
            for (var key : this.keyChanges.keySet())
            {
                this.keyValues.put(key, this.keyChanges.get(key));
            }

            this.keyChanges.clear();
        }
    }
}