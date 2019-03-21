package bt.game.resource.load;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.sound.sampled.Clip;

/**
 * @author &#8904
 *
 */
public interface ResourceLoader
{
    public void load(String name);

    public BufferedImage getImage(String resourceName);
    public Clip getSound(String resourceName);
    public File getFile(String resourceName);
    public Font getFont(String resourceName);
    public Object get(String resourceName);
}