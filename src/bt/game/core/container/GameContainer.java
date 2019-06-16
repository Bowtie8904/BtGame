package bt.game.core.container;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import bt.game.core.ctrl.spec.key.KeyController;
import bt.game.core.ctrl.spec.mouse.MouseController;
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

    /** The name of the currently active scene. */
    protected String currentSceneName;

    /** Indicates whether a new scene was requested and should be loaded. */
    protected boolean sceneRequested;

    /** The frame that contains the game canvas. */
    protected JFrame frame;

    /**
     * A collection mapping entries with scenes to unique names. The entries will hold the main scene as a key and an
     * optional (may be null) loading scene as a value.
     */
    private Map<String, Entry<Scene, Scene>> scenes;

    /** The pixel to {@link Unit} ratio that is calcualted when the frame is set up. */
    protected double ratio;

    /** The width in units. */
    protected double unitWidth;

    /** The height in units. */
    protected double unitHeight;

    /** Indicates whether this container was set to fullscreen. */
    protected boolean isFullScreen;

    /** The settings used to intitialize this container. */
    protected ContainerSettings settings;

    /** Indicates whether this container is in a valid state to perfom rendering. */
    protected boolean canRender;

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
        this.settings = settings;
        this.unitWidth = settings.getUnitWidth();
        this.unitHeight = settings.getUnitHeight();
        this.scenes = new HashMap<>();

        createFrame();
        createScenes();

        new MouseController(this);
        new KeyController(this);
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
        if ((comp.getWidth() / comp.getHeight()) / (this.settings.getUnitWidth() / this.settings.getUnitHeight()) == 1f)
        {
            this.ratio = comp.getWidth() / this.settings.getUnitWidth();
        }
        else
        {
            double difX = comp.getWidth() / this.settings.getUnitWidth();
            double difY = comp.getHeight() / this.settings.getUnitHeight();
            this.ratio = difX < difY ? difX : difY;
        }

        Unit.setRatio(this.ratio);
    }

    /**
     * Sets up the frame and the canvas. This will set the frame background color to black and position the canvas in
     * the middle of the frame. At the end of this method the frame will be made visible.
     */
    private void setupFrame()
    {
        this.frame.getContentPane().setBackground(Color.BLACK);

        width = Unit.forUnits(this.settings.getUnitWidth());
        height = Unit.forUnits(this.settings.getUnitHeight());

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
     * Gets whether this container is currently in full screen mode.
     * 
     * @return true if the container is in full screen.
     */
    public boolean isFullScreen()
    {
        return this.isFullScreen;
    }

    /**
     * Creates a new frame and sets it up correctly corresponding to the held {@link ContainerSettings} object.
     */
    private synchronized void createFrame()
    {
        if (this.frame != null)
        {
            this.frame.dispose();
        }

        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setUndecorated(settings.isUndecorated());
        this.frame.setResizable(false);

        if (this.settings.getTitle() != null)
        {
            this.frame.setTitle(this.settings.getTitle());
        }

        if (this.settings.getIcon() != null)
        {
            this.frame.setIconImage(this.settings.getIcon());
        }

        if (!this.settings.isFullscreen())
        {
            this.frame.setSize(settings.getFrameWidth(), settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
            this.isFullScreen = false;
        }

        if (this.settings.isFullscreen())
        {
            this.frame.setVisible(true);
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.isFullScreen = true;
        }
        else
        {
            this.frame.setSize(settings.getFrameWidth(), settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
        }

        calculateRatio(this.frame);
        setupFrame();
        this.createBufferStrategy(4);
        this.requestFocus();
        this.canRender = true;
    }

    /**
     * Sets this container to full screen.
     * 
     * <p>
     * A new frame will be created.
     * </p>
     * 
     * @param fullscreen
     */
    public void setFullScreen(boolean fullscreen)
    {
        this.canRender = false;
        this.settings.fullscreen(fullscreen);
        createFrame();

        if (this.currentScene != null)
        {
            this.currentScene.refresh();
        }
        refresh();
    }

    /**
     * Sets the size of this container in pixels.
     * 
     * <p>
     * A new frame will be created.
     * </p>
     * 
     * @param frameWidth
     * @param frameHeight
     */
    public void setFrameSize(int frameWidth, int frameHeight)
    {
        this.canRender = false;
        this.settings.frameSize(frameWidth, frameHeight);
        createFrame();

        if (this.currentScene != null)
        {
            this.currentScene.refresh();
        }
        refresh();
    }

    /**
     * Requests a new scene to be loaded after the current render iteration.
     * 
     * <p>
     * This will cause the container to properly {@link Scene#kill() kill} the current scene. The new main scene will be
     * loaded in a different thread. During the loading of the main scene the set loading scene is played (if it
     * exists).
     * </p>
     * 
     * @param name
     */
    public void requestScene(String name)
    {
        this.currentSceneName = name;
        this.sceneRequested = true;
    }

    /**
     * Sets the {@link Scene} to be displayed. This will properly {@link Scene#kill() kill} the current scene. The new
     * main scene will be loaded in a different thread. During the loading of the main scene the set loading scene is
     * played (if it exists).
     * 
     * @param name
     *            The name of the scene that should be played.
     */
    private void setScene(String name)
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

        Threads.get().executeCached(() -> {
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

    public Scene getCurrentScene()
    {
        return this.currentScene;
    }

    /**
     * Calls {@link Scene#tick() tick} of the current scene as soon as {@link Scene#isLoaded() isLoaded} returns true.
     */
    public void tick(double delta)
    {
        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            this.currentScene.tick(delta);
            MouseController.get().checkHover();
            KeyController.get().checkKeyChanges();
        }
    }

    /**
     * Calls {@link Scene#render(Graphics) render} of the current scene as soon as {@link Scene#isLoaded() isLoaded}
     * returns true.
     */
    public synchronized void render()
    {
        if (!this.canRender)
        {
            return;
        }

        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null)
        {
            this.createBufferStrategy(4);
            return;
        }

        Graphics2D g = (Graphics2D)bs.getDrawGraphics();

        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            this.currentScene.render(g);
        }

        render(g);

        g.dispose();
        bs.show();

        // if a new scene was requested switch now
        // to avoid complications during the current render process and the killing of the old scene at the same time
        if (this.sceneRequested)
        {
            setScene(this.currentSceneName);
            this.sceneRequested = false;
        }
    }

    /**
     * A method called from inside the {@link #render()} method.
     * 
     * <p>
     * This method is called AFTER the current scene was rendered.
     * </p>
     * 
     * <p>
     * This can be used to render stuff that will be globally used across scenes, i.e. frame decoration.
     * </p>
     * 
     * @param g
     */
    protected void render(Graphics2D g)
    {

    }

    /**
     * Called after a change in frame size.
     */
    protected void refresh()
    {

    }

    /**
     * Defines the scenes that are used within this game.
     */
    protected abstract void createScenes();
}