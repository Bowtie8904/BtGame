package bt.game.core.container;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import bt.game.core.ctrl.GameController;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;
import bt.utils.thread.Threads;

/**
 * A frame including a canvas to display a game. This class handles the starting and ending of {@link Scene scenes}.
 * 
 * @author &#8904
 */
public abstract class GameContainer extends Canvas
{
    /** The currently active scene. */
    protected Scene currentScene;

    /** The frame that contains the game canvas. */
    protected JFrame frame;

    /**
     * A collection mapping entries with scenes to unique names. The entries will hold the main scene as a key and an
     * optional (may be null) loading scene as a value.
     */
    private Map<String, Entry<Scene, Scene>> scenes;

    /** The pixel to {@link Unit} ratio that is calcualted when the frame is set up. */
    protected float ratio;
    
    /** The width in units. */
    private float unitWidth;
    
    /** The height in units. */
    private float unitHeight;

    /** The width in units. */
    private static Unit width;

    /** The height in units. */
    private static Unit height;

    /**
     * The {@link Unit} that describes the X axis of the game.
     * 
     * @return The unit instance.
     */
    public static Unit width()
    {
        return width;
    }

    /**
     * The {@link Unit} that describes the Y axis of the game.
     * 
     * @return The unit instance.
     */
    public static Unit height()
    {
        return height;
    }

    /**
     * Creates a new instance and uses the given settings. This will setup the frame, calculate and set the ratio for
     * {@link Unit units} and call {@link #createScenes()}.
     * 
     * @param settings
     *            The settings to use for this game container.
     */
    public GameContainer(ContainerSettings settings)
    {
        this.unitWidth = settings.getUnitWidth();
        this.unitHeight = settings.getUnitHeight();
        this.scenes = new HashMap<>();
        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setUndecorated(settings.isUndecorated());
        this.frame.setResizable(false);

        if (!settings.isFullscreen())
        {
            this.frame.setSize(settings.getFrameWidth(), settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
        }

        if (settings.isFullscreen())
        {
            this.frame.setVisible(true);
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        else
        {
            this.frame.setSize(settings.getFrameWidth(), settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
        }

        calculateRatio(this.frame);

        GameController.get().doInitialMapping(this.frame);

        setupFrame();

        createScenes();
    }

    /**
     * Calculates the pixel per unit ratio by using the width and height of the given component and the units set in the
     * {@link ContainerSettings settings} given to the constructor. This method will call {@link Unit#setRatio(float)}
     * with the result.
     * 
     * @param comp
     *            The component whichs width and height are used to calculate the ration.
     */
    private void calculateRatio(Component comp)
    {
        if (((float)comp.getWidth() / (float)comp.getHeight()) / (this.unitWidth / this.unitHeight) == 1f)
        {
            this.ratio = (float)comp.getWidth() / this.unitWidth;
        }
        else
        {
            float difX = (float)comp.getWidth() / this.unitWidth;
            float difY = (float)comp.getHeight() / this.unitHeight;
            this.ratio = difX < difY ? difX : difY;
        }

        Unit.setRatio(this.ratio);
    }

    /**
     * Sets up the frame abd the canvas. This will set the frame background color to black and position the canvas in
     * the middle of the frame. At the end of this method the frame will be made visible.
     */
    private void setupFrame()
    {
        this.frame.getContentPane().setBackground(Color.BLACK);

        width = Unit.forUnits(this.unitWidth);
        height = Unit.forUnits(this.unitHeight);

        setSize(new Dimension((int)width.pixels(), (int)height.pixels()));
        setPreferredSize(new Dimension((int)width.pixels(), (int)height.pixels()));
        setMaximumSize(new Dimension((int)width.pixels(), (int)height.pixels()));

        if (getWidth() < this.frame.getWidth())
        {
            this.frame.getContentPane().setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.X_AXIS));
            this.frame.getContentPane().add(Box.createHorizontalGlue());
            this.frame.getContentPane().add(this);
            this.frame.getContentPane().add(Box.createHorizontalGlue());
        }
        else if (this.getHeight() < this.frame.getHeight())
        {
            this.frame.getContentPane().setLayout(new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS));
            this.frame.getContentPane().add(Box.createVerticalGlue());
            this.frame.getContentPane().add(this);
            this.frame.getContentPane().add(Box.createVerticalGlue());
        }
        else
        {
            this.frame.getContentPane().add(this);
        }

        this.frame.revalidate();
        this.frame.repaint();

        this.frame.setVisible(true);
    }

    /**
     * Gets the frame that contains this games canvas.
     * 
     * @return
     */
    public JFrame getFrame()
    {
        return this.frame;
    }

    /**
     * Sets the {@link Scene} to be displayed. This will properly {@link Scene#kill() kill} the current scene. The new
     * main scene will be loaded in a different thread. During the loading of the main scene the set loading scene is
     * played (if it exists).
     * 
     * @param name
     *            The name of the scene that should be played.
     */
    public void setScene(String name)
    {
        if (this.currentScene != null)
        {
            this.currentScene.kill();
        }

        Entry<Scene, Scene> entry = this.scenes.get(name.toUpperCase());
        Scene mainScene = entry.getKey();
        Scene loadingScene = entry.getValue();

        if (loadingScene != null)
        {
            loadingScene.load(name);
            this.currentScene = loadingScene;
            loadingScene.start();
        }

        Threads.get().executeCached(() ->
        {
            mainScene.load(name.toUpperCase());
            setScene(mainScene);
            mainScene.start();
        });
    }

    /**
     * Sets the given scene. This kills the current scene if it does not equal the given one.
     * 
     * @param scene
     */
    private void setScene(Scene scene)
    {
        if (this.currentScene != null && !currentScene.equals(scene))
        {
            this.currentScene.kill();
        }

        this.currentScene = scene;
    }

    /**
     * Adds the given scene and maps it to the given name.
     * 
     * <p>
     * This is a convinience method for
     * 
     * <pre>
     * {@link #addScene(String, Scene, Scene) addScene(name, scene, null);}
     * </pre>
     * </p>
     * 
     * @param name
     * @param scene
     */
    protected void addScene(String name, Scene scene)
    {
        addScene(name, scene, null);
    }

    /**
     * Adds the given main scene and loading scene and maps them to the given name.
     * 
     * <p>
     * The loading scene may be null. If it is not null it will be displayed while the main scene is loading.
     * </p>
     * 
     * @param name
     * @param mainScene
     * @param loadingScene
     */
    protected void addScene(String name, Scene mainScene, Scene loadingScene)
    {
        this.scenes.put(name.toUpperCase(), new SimpleEntry<Scene, Scene>(mainScene, loadingScene));
    }

    /**
     * Calls {@link Scene#tick() tick} of the current scene as soon as {@link Scene#isLoaded() isLoaded} returns true.
     */
    public void tick()
    {
        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            this.currentScene.tick();
        }
    }

    /**
     * Calls {@link Scene#render(Graphics) render} of the current scene as soon as {@link Scene#isLoaded() isLoaded}
     * returns true.
     */
    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null)
        {
            this.createBufferStrategy(4);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            this.currentScene.render(g);
        }

        g.dispose();
        bs.show();
    }

    /**
     * Defines the scenes that are used within this game.
     */
    protected abstract void createScenes();
}