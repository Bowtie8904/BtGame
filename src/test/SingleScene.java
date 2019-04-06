package test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import bt.game.core.container.GameContainer;
import bt.game.core.ctrl.GameController;
import bt.game.core.scene.impl.BaseScene;
import bt.game.resource.load.Loadable;
import bt.game.resource.render.Renderable;
import bt.game.resource.render.impl.RenderableGif;
import bt.game.resource.render.impl.RenderableText;
import bt.game.util.unit.Unit;
import bt.types.sound.Sound;
import bt.types.sound.SoundSupplier;
import bt.utils.log.Logger;

/**
 * @author &#8904
 *
 */
public class SingleScene extends BaseScene
{
    /**
     * @param gameContainer
     */
    public SingleScene(GameContainer gameContainer)
    {
        super(gameContainer);
    }

    private Unit x;
    private Unit y;
    private Unit w;
    private Unit h;
    private boolean right;
    private Sound sound;
    private Sound sound2;
    private RenderableText text;

    @Override
    public synchronized void tick()
    {
        if (right)
        {
            x = x.addUnits(2f);
        }
        else
        {
            y = y.subtractUnits(0.1f);
            x = x.subtractUnits(0.1f);
            w = w.addUnits(0.2f);
            h = h.addUnits(0.2f);
        }
        super.tick();
    }

    @Override
    public void load(String name)
    {
        isLoaded = false;

        GameController.get().onKeyRelease(KeyEvent.VK_ESCAPE, () -> System.exit(0));
        GameController.get().onKeyPress(KeyEvent.VK_1, () -> this.gameContainer.setScene("test"));
        GameController.get().onKeyPress(KeyEvent.VK_2, () -> this.gameContainer.setScene("load"));
        GameController.get().onKeyPress(KeyEvent.VK_3, () -> this.gameContainer.setScene("load2"));

        Loadable loadable = new Loadable() {

            @Override
            public Map<String, Renderable> loadRenderables(String name)
            {
                Map<String, Renderable> renderables = new HashMap<>();
                try
                {
                    renderables.put("loading",
                            new RenderableGif(new ImageIcon(new File("resource/images/loading.gif").toURI().toURL())));
                }
                catch (Exception e)
                {
                    Logger.global().print(e);
                }
                return renderables;
            }

            @Override
            public Map<String, SoundSupplier> loadSounds(String name)
            {
                Map<String, SoundSupplier> sounds = new HashMap<>();
                sounds.put("elevator", new SoundSupplier(new File("resource/sounds/elevator.wav")));
                sounds.put("water", new SoundSupplier(new File("resource/sounds/waterdrop_sound.wav")));
                sounds.put("music", new SoundSupplier(new File("resource/sounds/music.wav")));
                return sounds;
            }

            @Override
            public Map<String, File> loadFiles(String name)
            {
                return null;
            }

            @Override
            public Map<String, Font> loadFonts(String name)
            {
                Map<String, Font> fonts = new HashMap<>();
                try
                {
                    fonts.put("font1", Font.createFont(Font.TRUETYPE_FONT, new File("resource/fonts/test_font.ttf")));
                }
                catch (FontFormatException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return fonts;
            }

            @Override
            public Map<String, Object> loadObjects(String name)
            {
                return null;
            }
        };

        this.resourceLoader.register(loadable);

        x = Unit.forUnits(932);
        y = Unit.forUnits(436);
        w = Unit.forUnits(37);
        h = Unit.forUnits(28);

        int z = 0;

        new Owl(this, Unit.forUnits(932), Unit.forUnits(436), Unit.forUnits(z ++ ));

        new Wall(this,
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(0),
                GameContainer.width(),
                Unit.forUnits(5));

        new Wall(this,
                Unit.forUnits(0),
                GameContainer.height().subtractUnits(5),
                Unit.forUnits(0),
                GameContainer.width(),
                Unit.forUnits(5));

        new Wall(this,
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(5),
                GameContainer.height());

        new Wall(this,
                GameContainer.width().subtractUnits(5),
                Unit.forUnits(0),
                Unit.forUnits(0),
                Unit.forUnits(5),
                GameContainer.height());

        super.load(name);
    }

    @Override
    public synchronized void render(Graphics g)
    {
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, (int)GameContainer.width().pixels(), (int)GameContainer.height().pixels());

        super.render(g);

        Renderable load = this.resourceLoader.getRenderable("loading");
        load.render(g,
                x.addUnits(w.divideBy(4)),
                y.addUnits(h.divideBy(4).units()),
                w.divideBy(2),
                h.divideBy(2));

        load.render(g,
                Unit.forUnits(0), Unit.forUnits(860), Unit.forUnits(100), Unit.forUnits(40));

        g.setColor(Color.BLUE);
        g.setFont(this.resourceLoader.getFont("font1"));
        text.render(g, Unit.forUnits(0), Unit.forUnits(860), Unit.forUnits(100), Unit.forUnits(40));
        text.setRotation(text.getRotation() - 20);
    }

    @Override
    public void start()
    {
        text = new RenderableText("Test");

        sound = this.resourceLoader.getSound("music");
        sound.setVolume(0.5f);
        sound.loop();

        sound2 = this.resourceLoader.getSound("elevator");
        sound2.setVolume(0.5f);
        sound2.loop();
    }

    @Override
    public void kill()
    {
        super.kill();
        GameController.get().clearKeyMappings();
        GameController.get().clearMouseTargets();
    }
}