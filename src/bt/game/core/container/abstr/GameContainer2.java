package bt.game.core.container.abstr;

import bt.game.core.container.ContainerSettings;
import bt.game.core.container.exc.InitException;
import bt.game.core.ctrl.spec.key.KeyController;
import bt.game.core.ctrl.spec.mouse.MouseController;
import bt.game.core.loop.impl.DefaultGameLoop;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import bt.io.sound.Sound;
import bt.scheduler.Threads;
import bt.utils.Exceptions;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A frame including a canvas to display a game. This class handles the starting and ending of {@link Scene scenes}.
 *
 * @author &#8904
 */
public abstract class GameContainer2
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

    protected long windowRef;

    protected boolean glContextLoaded;

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
        return GameContainer2.width;
    }

    /**
     * The {@link Unit} that describes the Y axis of the game.
     *
     * @return The unit instance.
     */
    public static Unit height()
    {
        return GameContainer2.height;
    }

    /**
     * Creates a new instance and uses the given settings. This will setup the frame, calculate and set the ratio for
     * {@link Unit units} and call {@link #createScenes()}.
     *
     * @param settings The settings to use for this game container.
     */
    public GameContainer2(ContainerSettings settings)
    {
        this.settings = settings;
        this.unitWidth = settings.getUnitWidth();
        this.unitHeight = settings.getUnitHeight();
        this.scenes = new HashMap<>();
        createScenes();
    }

    /**
     * Calculates the pixel per unit ratio by using the width and height of the given component and the units set in the
     * {@link ContainerSettings settings} given to the constructor. This method will call {@link Unit#setRatio(float)}
     * with the result.
     *
     * @param comp The component whichs width and height are used to calculate the ration.
     */
    protected void calculateRatio(int pixelWidth, int pixelHeight)
    {
        if ((pixelWidth / pixelHeight) / (this.settings.getUnitWidth() / this.settings.getUnitHeight()) == 1f)
        {
            this.ratio = pixelWidth / this.settings.getUnitWidth();
        }
        else
        {
            double difX = pixelWidth / this.settings.getUnitWidth();
            double difY = pixelHeight / this.settings.getUnitHeight();
            this.ratio = difX < difY ? difX : difY;
        }

        Unit.setRatio(this.ratio);

        GameContainer2.width = Unit.forUnits(this.settings.getUnitWidth());
        GameContainer2.height = Unit.forUnits(this.settings.getUnitHeight());
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
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
        {
            throw new InitException("Unable to initialize GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
        glfwWindowHint(GLFW_FOCUSED, GLFW_TRUE);
        glfwWindowHint(GLFW_DECORATED, this.settings.isUndecorated() ? GLFW_FALSE : GLFW_TRUE);

        this.windowRef = glfwCreateWindow(this.settings.getFrameWidth(), this.settings.getFrameHeight(), this.settings.getTitle(), NULL, NULL);

        if (this.settings.isFullscreen())
        {
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowMonitor(this.windowRef,
                                 glfwGetPrimaryMonitor(),
                                 0,
                                 0,
                                 vidmode.width(),
                                 vidmode.height(),
                                 GLFW_DONT_CARE);
        }

        if (this.windowRef == NULL)
        {
            throw new InitException("Failed to create the GLFW window.");
        }

        try (MemoryStack stack = stackPush())
        {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.windowRef, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(this.windowRef,
                             (vidmode.width() - pWidth.get(0)) / 2,
                             (vidmode.height() - pHeight.get(0)) / 2
            );

            calculateRatio(pWidth.get(0), pHeight.get(0));
        }

        glfwMakeContextCurrent(this.windowRef);
        // Enable v-sync
        glfwSwapInterval(1);
        glfwShowWindow(this.windowRef);

        GL.createCapabilities();
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glOrtho(0.f, GameContainer2.width().units(), GameContainer2.height().units(), 0.f, 0.f, 1.f);
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
        this.currentSceneName = name;
        this.sceneRequested = true;
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
                e.printStackTrace();
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
                                            e.printStackTrace();
                                            exit();
                                        }

                                        setScene(mainScene);
                                        mainScene.start();
                                    });
    }

    /**
     * Sets the given scene. This kills the current scene if it does not equal the given one.
     *
     * @param scene
     */
    protected void setScene(Scene scene)
    {
        if (this.currentScene != null && !this.currentScene.equals(scene))
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
        this.scenes.put(name,
                        new SimpleEntry<>(mainScene,
                                          loadingScene));
    }

    public Scene getCurrentScene()
    {
        return this.currentScene;
    }

    /**
     * Attempts to return the main scene that is registered with the given name.
     *
     * @param name
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

        glfwPollEvents();

        if (glfwWindowShouldClose(this.windowRef))
        {
            exit();
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
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glColor4f(1, 0, 0, 0);

            glBegin(GL_LINES);
            glVertex3f(0, 0, 0);
            glVertex3f((float)GameContainer2.width().divideBy(2).units(), (float)GameContainer2.height().divideBy(2).units(), 0);
            glEnd();

            glBegin(GL_LINES);
            glVertex3f((float)GameContainer2.width().units(), 0, 0);
            glVertex3f((float)GameContainer2.width().divideBy(2).units(), (float)GameContainer2.height().divideBy(2).units(), 0);
            glEnd();

            glBegin(GL_LINES);
            glVertex3f((float)GameContainer2.width().divideBy(2).units(), (float)GameContainer2.height().divideBy(2).units(), 0);
            glVertex3f((float)GameContainer2.width().divideBy(2).units(), (float)GameContainer2.height().units(), 0);
            glEnd();

            glfwSwapBuffers(this.windowRef);
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
        if (this.currentScene != null)
        {
            this.currentScene.refresh();
        }
    }

    /**
     * Exits the application after the next render call or after 500 ms.
     */
    public void exit()
    {
        this.canRender = false;

        synchronized (this)
        {
            Exceptions.ignoreThrow(this::wait, 500);
        }

        if (this.windowRef != NULL)
        {
            glfwFreeCallbacks(this.windowRef);
            glfwDestroyWindow(this.windowRef);
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }

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
        this.isPaused = paused;
        pauseSounds(paused);
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
        if (paused)
        {
            Sound.pauseAll();
        }
        else
        {
            Sound.resumeAll();
        }
    }

    protected DefaultGameLoop createDefaultGameLoop()
    {
        return new DefaultGameLoop(this::tick,
                                   this::render,
                                   this::createFrame);
    }

    /**
     * Defines the scenes that are used within this game.
     */
    protected abstract void createScenes();
}