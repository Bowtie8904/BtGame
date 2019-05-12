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
    private static final int KEY_JUST_DOWN = 2;
    private static final int KEY_RELEASED = 3;

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
     * Indicates whether a key is just now being pressed. This state does not last longer than one tick.
     * 
     * @param key
     *            The key code (constant from {@link KeyEvent}) of the key to check.
     * @return true if the key was just pressed.
     */
    public boolean isKeyJustDown(int key)
    {
        Integer value = this.keyValues.get(key);
        return value != null && value == KEY_JUST_DOWN;
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
        return value != null && (value == KEY_DOWN || value == KEY_JUST_DOWN);
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
        return value != null && value == KEY_RELEASED;
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
            if (!isKeyDown(e.getKeyCode()))
            {
                this.keyChanges.put(e.getKeyCode(), KEY_JUST_DOWN);
            }
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
            this.keyChanges.put(e.getKeyCode(), KEY_RELEASED);
        }
    }

    public void checkKeyChanges()
    {
        // changing 'recently released' to 'not down' and 'just down' to 'down'
        this.keyValues.replaceAll((k, v) -> {
            if (v == KEY_RELEASED)
            {
                return KEY_NOT_DOWN;
            }

            if (v == KEY_JUST_DOWN)
            {
                return KEY_DOWN;
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