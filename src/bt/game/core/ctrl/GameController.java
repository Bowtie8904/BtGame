package bt.game.core.ctrl;

import bt.game.core.container.GameContainer;
import bt.game.core.ctrl.spec.key.KeyController;
import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.ctrl.spec.mouse.MouseTarget;
import bt.game.core.loop.GameLoop;
import bt.key.KeyAction;

/**
 * A class to handle key and mouse controls. Actions defined in runnables can be defined for each key or mouse button.
 * 
 * <p>
 * This class is created for convinient access as it is a singleton which holds all relevant controller instances.
 * </p>
 * 
 * @author &#8904
 */
public class GameController
{
    private static GameController instance;
    private KeyController keyController;
    private MouseController mouseController;

    public static GameController get()
    {
        if (instance == null)
        {
            instance = new GameController();
        }

        return instance;
    }

    protected GameController()
    {
    }

    public void addMouseTarget(MouseTarget target)
    {
        this.mouseController.addMouseTarget(target);
    }

    public void removeMouseTarget(MouseTarget target)
    {
        this.mouseController.removeMouseTarget(target);
    }

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
        this.keyController.onKeyPress(keyCode, modifier, action);
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
        this.keyController.onKeyRelease(keyCode, modifier, action);
    }

    /**
     * Initialises all sub contrllers with the given component.
     * 
     * @param comp
     */
    public void init(GameContainer comp)
    {
        this.keyController = new KeyController();
        this.keyController.doInitialMapping(comp.getFrame());

        this.mouseController = new MouseController(comp);

        GameLoop hoverLoop = new GameLoop(this.mouseController::checkHover, null);
        hoverLoop.setFrameRate(60);
        hoverLoop.start();
    }
}