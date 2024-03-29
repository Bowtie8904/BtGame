package bt.game.core.container.abstr;

import bt.game.core.container.ContainerSettings;
import bt.game.core.ctrl.spec.key.KeyController;
import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.ctrl.spec.mouse.obj.Cursor;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import bt.io.sound.Sound;
import bt.log.Log;
import bt.scheduler.Threads;
import bt.utils.Exceptions;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A frame including a canvas to display a game. This class handles the starting and ending of {@link Scene scenes}.
 *
 * @author &#8904
 */
public abstract class GameContainer extends Canvas
{
    /**
     * The currently active scene.
     */
    protected Scene currentScene;

    /**
     * The name of the currently active scene.
     */
    protected String currentSceneName;

    /**
     * Indicates whether a new scene was requested and should be loaded.
     */
    protected boolean sceneRequested;

    /**
     * The frame that contains the game canvas.
     */
    protected JFrame frame;

    /**
     * A collection mapping entries with scenes to unique names. The entries will hold the main scene as a key and an
     * optional (may be null) loading scene as a value.
     */
    private Map<String, Entry<Scene, Scene>> scenes;

    /**
     * The pixel to {@link Unit} ratio that is calcualted when the frame is set up.
     */
    protected double ratio;

    /**
     * The width in units.
     */
    protected double unitWidth;

    /**
     * The height in units.
     */
    protected double unitHeight;

    /**
     * Indicates whether this container was set to fullscreen.
     */
    protected boolean isFullScreen;

    /**
     * The settings used to intitialize this container.
     */
    protected ContainerSettings settings;

    /**
     * Indicates whether this container is in a valid state to perfom rendering.
     */
    protected boolean canRender;

    /**
     * Indictaes whether this game is currently paused.
     */
    protected volatile boolean isPaused;

    /**
     * The width in units.
     */
    protected static Unit width;

    /**
     * The height in units.
     */
    protected static Unit height;

    /**
     * The {@link Unit} that describes the X axis of the game.
     *
     * @return The unit instance.
     */
    public static Unit width()
    {
        return GameContainer.width;
    }

    /**
     * The {@link Unit} that describes the Y axis of the game.
     *
     * @return The unit instance.
     */
    public static Unit height()
    {
        return GameContainer.height;
    }

    /**
     * Creates a new instance and uses the given settings. This will setup the frame, calculate and set the ratio for
     * {@link Unit units} and call {@link #createScenes()}.
     *
     * @param settings The settings to use for this game container.
     */
    public GameContainer(ContainerSettings settings)
    {
        this.settings = settings;
        this.unitWidth = settings.getUnitWidth();
        this.unitHeight = settings.getUnitHeight();
        this.scenes = new HashMap<>();

        setIgnoreRepaint(true);

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
     * @param comp The component whichs width and height are used to calculate the ration.
     */
    protected void calculateRatio(Component comp)
    {
        Log.entry(comp);

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

        Log.exit();
    }

    /**
     * Sets up the frame and the canvas. This will set the frame background color to black and position the canvas in
     * the middle of the frame. At the end of this method the frame will be made visible.
     */
    protected void setupFrame()
    {
        Log.entry();

        this.frame.getContentPane().setBackground(Color.BLACK);

        GameContainer.width = Unit.forUnits(this.settings.getUnitWidth());
        GameContainer.height = Unit.forUnits(this.settings.getUnitHeight());

        setSize(this.frame.getSize());
        setPreferredSize(this.frame.getSize());
        setMaximumSize(this.frame.getSize());

        if (getWidth() < this.frame.getWidth())
        {
            this.frame.getContentPane()
                      .setLayout(new BoxLayout(this.frame.getContentPane(),
                                               BoxLayout.X_AXIS));
            this.frame.getContentPane().add(Box.createHorizontalGlue());
            this.frame.getContentPane().add(this);
            this.frame.getContentPane().add(Box.createHorizontalGlue());
        }
        else if (getHeight() < this.frame.getHeight())
        {
            this.frame.getContentPane()
                      .setLayout(new BoxLayout(this.frame.getContentPane(),
                                               BoxLayout.Y_AXIS));
            this.frame.getContentPane().add(Box.createVerticalGlue());
            this.frame.getContentPane().add(this);
            this.frame.getContentPane().add(Box.createVerticalGlue());
        }
        else
        {
            this.frame.getContentPane().add(this);
        }

        this.frame.revalidate();
        this.frame.pack();
        this.frame.repaint();

        this.frame.setVisible(true);

        calculateRatio(this.frame.getContentPane());

        Log.exit();
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
    protected synchronized void createFrame()
    {
        Log.entry();
        if (this.frame != null)
        {
            this.frame.dispose();
        }

        this.frame = new JFrame();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setUndecorated(this.settings.isUndecorated());
        this.frame.setResizable(false);

        if (this.settings.getTitle() != null)
        {
            this.frame.setTitle(this.settings.getTitle());
        }

        if (this.settings.getIcon() != null)
        {
            this.frame.setIconImage(this.settings.getIcon());
        }

        if (this.settings.getCursor() != null)
        {
            setCursor(this.settings.getCursor());
        }

        if (!this.settings.isFullscreen())
        {
            this.frame.setSize(this.settings.getFrameWidth(),
                               this.settings.getFrameHeight());
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
            this.frame.setSize(this.settings.getFrameWidth(),
                               this.settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
        }

        setupFrame();
        this.createBufferStrategy(4);
        this.requestFocus();
        this.canRender = true;

        Log.exit();
    }

    /**
     * Sets the cursor of the game.
     *
     * @param cursor
     */
    public void setCursor(Cursor cursor)
    {
        this.frame.setCursor(Toolkit.getDefaultToolkit()
                                    .createCustomCursor(cursor.getCursorImage(),
                                                        cursor.getHotspot(),
                                                        "game cursor"));
    }

    /**
     * Resets the games cursor to the default one that is set in the {@link ContainerSettings settings}.
     */
    public void resetToDefaultCursor()
    {
        this.frame.setCursor(Toolkit.getDefaultToolkit()
                                    .createCustomCursor(this.settings.getCursor().getCursorImage(),
                                                        this.settings.getCursor().getHotspot(),
                                                        "game cursor"));
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
        this.settings.frameSize(frameWidth,
                                frameHeight);
        createFrame();
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
        Log.entry(name);
        this.currentSceneName = name;
        this.sceneRequested = true;
        Log.exit();
    }

    /**
     * Sets the {@link Scene} to be displayed. This will properly {@link Scene#kill() kill} the current scene. The new
     * main scene will be loaded in a different thread. During the loading of the main scene the set loading scene is
     * played (if it exists).
     * <p>
     * The given scene can not be the same one as the currently active one.
     *
     * @param name The name of the scene that should be played.
     */
    protected void setScene(String name)
    {
        Log.entry(name);
        if (this.currentScene != null)
        {
            this.currentScene.kill();
        }

        Entry<Scene, Scene> entry = this.scenes.get(name);
        Scene mainScene = entry.getKey();
        Scene loadingScene = entry.getValue();

        if (loadingScene != null)
        {
            try
            {
                loadingScene.load(name);
            }
            catch (Exception e)
            {
                Log.error("Error", e);
                exit();
            }

            this.currentScene = loadingScene;
            loadingScene.start();
        }

        Threads.get().executeCached(() ->
                                    {
                                        try
                                        {
                                            mainScene.load(name);
                                        }
                                        catch (Exception e)
                                        {
                                            Log.error("Error", e);
                                            exit();
                                        }

                                        setScene(mainScene);
                                        mainScene.start();
                                    }, "Load-" + name);

        Log.exit();
    }

    /**
     * Sets the given scene. This kills the current scene if it does not equal the given one.
     *
     * @param scene
     */
    protected void setScene(Scene scene)
    {
        Log.entry(scene);

        if (this.currentScene != null && !this.currentScene.equals(scene))
        {
            this.currentScene.kill();
        }

        this.currentScene = scene;

        Log.exit();
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
    public void addScene(String name, Scene scene)
    {
        addScene(name,
                 scene,
                 null);
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
    public void addScene(String name, Scene mainScene, Scene loadingScene)
    {
        Log.entry(name, mainScene, loadingScene);
        this.scenes.put(name,
                        new SimpleEntry<>(mainScene,
                                          loadingScene));

        Log.exit();
    }

    public Scene getCurrentScene()
    {
        return this.currentScene;
    }

    /**
     * Attempts to return the main scene that is registered with the given name.
     *
     * @param name
     *
     * @return The scene with the given name or null.
     */
    public Scene getScene(String name)
    {
        var entry = this.scenes.get(name);
        return entry == null ? null : entry.getKey();
    }

    /**
     * Calls {@link Scene#tick() tick} of the current scene as soon as {@link Scene#isLoaded() isLoaded} returns true.
     */
    public void tick(double delta)
    {
        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            if (!this.isPaused)
            {
                this.currentScene.tick(delta);
            }

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
        if (this.canRender)
        {
            BufferStrategy bs = getBufferStrategy();

            if (bs == null)
            {
                createBufferStrategy(4);
                return;
            }

            Graphics2D g = (Graphics2D)bs.getDrawGraphics();

            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                               RenderingHints.VALUE_STROKE_PURE);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);

            if (this.currentScene != null && this.currentScene.isLoaded())
            {
                this.currentScene.render(g, this.settings.isDebugRendering());
            }

            render(g, this.settings.isDebugRendering());

            g.dispose();
            bs.show();
        }
        else
        {
            // notifying to alert the wait call in exit()
            synchronized (this)
            {
                notifyAll();
            }
        }

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
    protected void render(Graphics2D g, boolean debugRendering)
    {

    }

    /**
     * Called after a change in frame size.
     *
     * <p>
     * Refreshes the current scene.
     * </p>
     */
    protected void refresh()
    {
        Log.entry();

        if (this.currentScene != null)
        {
            this.currentScene.refresh();
        }

        Log.exit();
    }

    /**
     * Exits the application after the next render call or after 500 ms.
     */
    public void exit()
    {
        Log.entry();
        this.canRender = false;

        synchronized (this)
        {
            Exceptions.ignoreThrow(this::wait, 500);
        }

        Log.exit();

        System.exit(0);
    }

    /**
     * Sets the pause state of this GameContainer.
     * <p>
     * If a container is paused it will no longer call the tick method of the current scene.
     * Mouse and key actions are still being registered.
     *
     * @param paused
     */
    public void setPaused(boolean paused)
    {
        Log.entry(paused);
        this.isPaused = paused;
        pauseSounds(paused);
        Log.exit();
    }

    /**
     * Indictaes whether this GameContainer is currently paused.
     * <p>
     * If a container is paused it will no longer call the tick method of the current scene.
     * Mouse and key actions are still being registered.
     * <p>
     * This will call {@link #pauseSounds(boolean)}.
     *
     * @param paused
     */
    public boolean isPaused()
    {
        return this.isPaused;
    }

    /**
     * The default implementation will pause/resume all currently playing sounds.
     * <p>
     * Subclasses should override this to adjust which categories should be paused/resumed.
     *
     * @param paused true to pause, false to resume.
     */
    public void pauseSounds(boolean paused)
    {
        Log.entry(paused);
        if (paused)
        {
            Sound.pauseAll();
        }
        else
        {
            Sound.resumeAll();
        }
        Log.exit();
    }

    /**
     * Defines the scenes that are used within this game.
     */
    protected abstract void createScenes();
}