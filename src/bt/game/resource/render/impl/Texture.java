package bt.game.resource.render.impl;

import bt.types.Killable;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class Texture implements Killable
{
    private int textureObject;
    private int width;
    private int height;

    public Texture(String filename)
    {
        try
        {
            URI file = getClass().getResource(filename).toURI();
            BufferedImage bufferedImage = ImageIO.read(new File(file));
            this.width = bufferedImage.getWidth();
            this.height = bufferedImage.getHeight();

            int[] pixels_raw = new int[this.width * this.height * 4];
            pixels_raw = bufferedImage.getRGB(0, 0, this.width, this.height, null, 0, this.width);

            ByteBuffer pixels = BufferUtils.createByteBuffer(this.width * this.height * 4);

            for (int i = 0; i < this.width; i++)
            {
                for (int j = 0; j < this.height; j++)
                {
                    int pixel = pixels_raw[i * this.width + j];
                    pixels.put((byte)((pixel >> 16) & 0xFF)); // RED
                    pixels.put((byte)((pixel >> 8) & 0xFF));  // GREEN
                    pixels.put((byte)(pixel & 0xFF));         // BLUE
                    pixels.put((byte)((pixel >> 24) & 0xFF)); // ALPHA
                }
            }

            pixels.flip();

            this.textureObject = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, this.textureObject);

            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

            bufferedImage.flush();
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    public void bind(int sampler)
    {
        if (sampler >= 0 && sampler <= 31)
        {
            glActiveTexture(GL_TEXTURE0 + sampler);
            glBindTexture(GL_TEXTURE_2D, this.textureObject);
        }
    }

    @Override
    public void kill()
    {
        glDeleteTextures(this.textureObject);
    }
}