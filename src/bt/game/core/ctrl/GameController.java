package bt.game.core.ctrl;

import java.util.function.Consumer;

import bt.game.core.container.GameContainer;
import bt.game.core.ctrl.spec.key.KeyController;
import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.ctrl.spec.mouse.MouseTarget;
import bt.game.core.loop.GameLoop;
import bt.game.core.loop.impl.FixedGameLoop;
import bt.key.KeyAction;
import bt.utils.log.Logger;

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

    /**
     * {@link MouseController#addMouseTarget(MouseTarget)}
     */
    public void addMouseTarget(MouseTarget target)
    {
        this.mouseController.addMouseTarget(target);
    }

    /**
     * {@link MouseController#removeMouseTarget(MouseTarget)}
     */
    public void removeMouseTarget(MouseTarget target)
    {
        this.mouseController.removeMouseTarget(target);
    }

    /**
     * {@link MouseController#setGlobalOnLeftClick(Consumer)}
     */
    public void setGlobalOnLeftClick(Consumer<MouseTarget> onClick)
    {
        this.mouseController.setGlobalOnLeftClick(onClick);
    }

    /**
     * {@link MouseController#setGlobalOnRightClick(Consumer)}
     */
    public void setGlobalOnRightClick(Consumer<MouseTarget> onClick)
    {
        this.mouseController.setGlobalOnRightClick(onClick);
    }

    /**
     * {@link MouseController#clearMouseTargets()}
     */
    public void clearMouseTargets()
    {
        this.mouseController.clearMouseTargets();
    }

    /**
     * {@link KeyController#onKeyPress(int, Runnable)}
     */
    public void onKeyPress(int keyCode, Runnable action)
    {
        onKeyPress(keyCode, KeyAction.NO_MODIFIER, action);
    }

    /**
     * {@link KeyController#onKeyPress(int, int, Runnable)}
     */
    public void onKeyPress(int keyCode, int modifier, Runnable action)
    {
        this.keyController.onKeyPress(keyCode, modifier, action);
    }

    /**
     * {@link KeyController#onKeyRelease(int, Runnable)}
     */
    public void onKeyRelease(int keyCode, Runnable action)
    {
        onKeyRelease(keyCode, KeyAction.NO_MODIFIER, action);
    }

    /**
     * {@link KeyController#onKeyRelease(int, int, Runnable)}
     */
    public void onKeyRelease(int keyCode, int modifier, Runnable action)
    {
        this.keyController.onKeyRelease(keyCode, modifier, action);
    }

    /**
     * {@link KeyController#clearMappings()}
     */
    public void clearKeyMappings()
    {
        this.keyController.clearMappings();
    }

    /**
     * Initialises all sub contrllers with the given container.
     * 
     * @param comp
     */
    public void init(GameContainer comp)
    {
        this.keyController = new KeyController();
        this.keyController.doInitialMapping(comp.getFrame());

        this.mouseController = new MouseController(comp);

        GameLoop hoverLoop = new FixedGameLoop(i -> this.mouseController.checkHover(), null) {
            @Override
            public void kill()
            {
                Logger.global().print("Killing hover check loop.");
                stop();
            }
        };
        hoverLoop.setFrameRate(60);
        hoverLoop.start();
    }
}