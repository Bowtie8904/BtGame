package bt.game.core.ctrl.spec;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import bt.key.KeyAction;
import bt.key.KeyBoardHook;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public class KeyController
{
    private Map<Integer, KeyAction[]> keyMappings;

    /**
     * Sets the action that is executed when the key with the given key code is pressed without any modifiers (shift,
     * ctrl or alt). This method will override any action that was set for this specifid condition before.
     * 
     * @param keyCode
     * @param action
     */
    public void onKeyPress(int keyCode, Runnable action)
    {
        onKeyPress(keyCode, KeyAction.NO_MODIFIER, action);
    }

    /**
     * Sets the action that is executed when the key with the given key code is pressed together with the given modifier
     * ({@link KeyAction#SHIFT_MODIFIER shift}, {@link KeyAction#CTRL_MODIFIER ctrl} or {@link KeyAction#ALT_MODIFIER
     * alt}). This method will override any action that was set for this specifid condition before.
     * 
     * @param keyCode
     * @param modifier
     * @param action
     */
    public void onKeyPress(int keyCode, int modifier, Runnable action)
    {
        KeyAction[] actions = this.keyMappings.get(keyCode);

        if (actions == null)
        {
            throw new IllegalArgumentException("No mapping for key code = " + keyCode);
        }

        actions[modifier].setKeyPressedAction((e) -> action.run());
    }

    /**
     * Sets the action that is executed when the key with the given key code is released without any modifiers (shift,
     * ctrl or alt). This method will override any action that was set for this specifid condition before.
     * 
     * @param keyCode
     * @param action
     */
    public void onKeyRelease(int keyCode, Runnable action)
    {
        onKeyRelease(keyCode, KeyAction.NO_MODIFIER, action);
    }

    /**
     * Sets the action that is executed when the key with the given key code is released together with the given
     * modifier ({@link KeyAction#SHIFT_MODIFIER shift}, {@link KeyAction#CTRL_MODIFIER ctrl} or
     * {@link KeyAction#ALT_MODIFIER alt}). This method will override any action that was set for this specifid
     * condition before.
     * 
     * @param keyCode
     * @param modifier
     * @param action
     */
    public void onKeyRelease(int keyCode, int modifier, Runnable action)
    {
        KeyAction[] actions = this.keyMappings.get(keyCode);

        if (actions == null)
        {
            throw new IllegalArgumentException("No mapping for key code = " + keyCode);
        }

        actions[modifier].setKeyReleasedAction((e) -> action.run());
    }

    public void doInitialMapping(Component comp)
    {
        this.keyMappings = new HashMap<>();
        int count = 0;
        Field[] fields = KeyEvent.class.getDeclaredFields();

        for (Field f : fields)
        {
            if (Modifier.isStatic(f.getModifiers())
                    && Modifier.isPublic(f.getModifiers())
                    && Modifier.isFinal(f.getModifiers())
                    && f.getName().startsWith("VK_"))
            {
                try
                {
                    count ++ ;
                    int code = f.getInt(null);

                    KeyAction[] actions = new KeyAction[]
                    {
                            new KeyAction(comp, code, KeyAction.NO_MODIFIER, (e) ->
                            {
                            }, (e) ->
                            {
                            }),
                            new KeyAction(comp, code, KeyAction.SHIFT_MODIFIER, (e) ->
                            {
                            }, (e) ->
                            {
                            }),
                            new KeyAction(comp, code, KeyAction.ALT_MODIFIER, (e) ->
                            {
                            }, (e) ->
                            {
                            }),
                            new KeyAction(comp, code, KeyAction.CTRL_MODIFIER, (e) ->
                            {
                            }, (e) ->
                            {
                            }),
                    };

                    this.keyMappings.put(code, actions);

                    for (KeyAction a : actions)
                    {
                        KeyBoardHook.get().addKeyAction(a);
                    }
                }
                catch (Exception e)
                {
                    Logger.global().print(e);
                }
            }
        }

        Logger.global().print("Mapped " + count + " keys.");
    }
}