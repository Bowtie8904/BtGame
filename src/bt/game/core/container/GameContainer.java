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

import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;
import bt.utils.thread.Threads;

/**
 * @author &#8904
 *
 */
public abstract class GameContainer extends Canvas
{
    protected Scene currentScene;
    protected JFrame frame;
    private Map<String, Entry<Scene, Scene>> scenes;
    protected float ratio;
    private float unitWidth;
    private float unitHeight;
    private static Unit width;
    private static Unit height;

    public static Unit width()
    {
        return width;
    }

    public static Unit height()
    {
        return height;
    }

    public GameContainer(int frameWidth, int frameHeight, float unitWidth, float unitHeight)
    {
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        this.scenes = new HashMap<>();
        this.frame = new JFrame();
        this.frame.setSize(frameWidth, frameHeight);
        this.frame.setUndecorated(true);
        this.frame.setResizable(false);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
        calculateRatio(this.frame);
        setupFrame();
        createScenes();
    }

    public GameContainer(float unitWidth, float unitHeight)
    {
        this.unitWidth = unitWidth;
        this.unitHeight = unitHeight;
        this.scenes = new HashMap<>();
        this.frame = new JFrame();
        this.frame.setUndecorated(true);
        this.frame.setResizable(false);
        this.frame.setVisible(true);
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        calculateRatio(this.frame);
        setupFrame();
        createScenes();
    }

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

        this.frame.setVisible(true);

        if (settings.isFullscreen())
        {
            this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        else
        {
            this.frame.setSize(settings.getFrameWidth(), settings.getFrameHeight());
            this.frame.setLocationRelativeTo(null);
        }

        calculateRatio(this.frame);
        setupFrame();

        createScenes();
    }

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

    private void setupFrame()
    {
        this.frame.getContentPane().setBackground(Color.BLACK);

        width = Unit.forUnits(this.unitWidth);
        height = Unit.forUnits(this.unitHeight);

        this.setSize(new Dimension((int)width.pixels(), (int)height.pixels()));
        this.setPreferredSize(new Dimension((int)width.pixels(), (int)height.pixels()));
        this.setMaximumSize(new Dimension((int)width.pixels(), (int)height.pixels()));

        if (this.getWidth() < this.frame.getWidth())
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
    }

    public JFrame getFrame()
    {
        return this.frame;
    }

    public void setFullScreen()
    {

    }

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

    private void setScene(Scene scene)
    {
        if (this.currentScene != null && !currentScene.equals(scene))
        {
            this.currentScene.kill();
        }

        this.currentScene = scene;
    }

    protected void addScene(String name, Scene scene)
    {
        addScene(name, scene, null);
    }

    protected void addScene(String name, Scene mainScene, Scene loadingScene)
    {
        this.scenes.put(name.toUpperCase(), new SimpleEntry<Scene, Scene>(mainScene, loadingScene));
    }

    public void tick()
    {
        if (this.currentScene != null && this.currentScene.isLoaded())
        {
            this.currentScene.tick();
        }
    }

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

    protected abstract void createScenes();
}