package bt.game.resource.sound;

import bt.game.core.obj.hand.intf.ObjectHandler;
import bt.game.core.obj.intf.GameObject;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import bt.io.sound.Sound;
import bt.utils.NumberUtils;

/**
 * A class that represents a location based sound source whichs volume can be updated depending on the distance to a
 * given game object.
 *
 * <p>
 * This class implements the {@link Tickable} interface to allow recalculating of the volume on a fixed rate.
 * </p>
 *
 * @author &#8904
 */
public class SoundSource implements Tickable
{
    protected Scene scene;
    protected Unit x;
    protected Unit y;
    protected GameObject volumeTarget;
    protected GameObject attachTarget;
    protected Sound sound;
    protected Unit maxDistance;
    protected float maxVolume;
    protected float minVolume;

    /**
     * Creates a new instance with the given parameters.
     *
     * <p>
     * This will add this instance to the {@link ObjectHandler} of the given scene via
     * {@link ObjectHandler#addObject(Object)}.
     * </p>
     *
     * @param scene
     *            The scene that this instance is used for.
     * @param volumeTarget
     *            The game object whichs distance to this instance will be used to update the volume.
     * @param sound
     *            The sound that is played.
     * @param x
     *            The x position of this instance used to calculate the distance to the volumeTarget.
     * @param y
     *            The y position of this instance used to calculate the distance to the volumeTarget.
     * @param maxDistance
     *            The maximum distance to the volumeTarget. If the distance between the objects is larger than this
     *            parameter, the volume will drop to the {@link #setMinVolume(float) minimum volume}.
     */
    public SoundSource(Scene scene, GameObject volumeTarget, Sound sound, Unit x, Unit y, Unit maxDistance)
    {
        this.scene = scene;
        this.volumeTarget = volumeTarget;
        this.sound = sound;
        this.x = x;
        this.y = y;
        this.maxDistance = maxDistance;
        this.scene.getObjectHandler().addObject(this);
        this.minVolume = 0f;
        this.maxVolume = 1f;
    }

    /**
     * Creates a new instance with the given parameters.
     *
     * <p>
     * This will add this instance to the {@link ObjectHandler} of the given scene via
     * {@link ObjectHandler#addObject(Object)}.
     * </p>
     *
     * @param scene
     *            The scene that this instance is used for.
     * @param volumeTarget
     *            The game object whichs distance to this instance will be used to update the volume.
     * @param attachTarget
     *            The game object that this source is attached to. The position of this source will adjust
     *            to the center position of the attachTarget every tick.
     * @param sound
     *            The sound that is played.
     * @param maxDistance
     *            The maximum distance to the volumeTarget. If the distance between the objects is larger than this
     *            parameter, the volume will drop to the {@link #setMinVolume(float) minimum volume}.
     */
    public SoundSource(Scene scene, GameObject volumeTarget, GameObject attachTarget, Sound sound, Unit maxDistance)
    {
        this(scene, volumeTarget, sound, attachTarget.getCenterX(), attachTarget.getCenterY(), maxDistance);
        this.attachTarget = attachTarget;
    }

    /**
     * Updates the volume of the used sound based on the distance to the volumeTarget.
     *
     * <p>
     * This method does nothing if the volumeTarget or sound are null.
     * </p>
     *
     * <p>
     * This method will never set the volume to something lower than what is specified via {@link #setMinVolume(float)
     * setMinVolume}, and never to something higher than what is specified via {@link #setMaxVolume(float)
     * setMaxVolume}.
     * </p>
     */
    public void updateVolume()
    {
        if (this.volumeTarget != null && this.sound != null)
        {
            Unit targetX = this.volumeTarget.getCenterX();
            Unit targetY = this.volumeTarget.getCenterY();

            double distance = Math
                                  .sqrt((targetX.units() - this.x.units()) * (targetX.units() - this.x.units())
                                        + (targetY.units() - this.y.units()) * (targetY.units() - this.y.units()));

            if (distance > this.maxDistance.units())
            {
                this.sound.setVolume(this.minVolume);
            }
            else
            {
                this.sound.setVolume(NumberUtils.clamp(
                                                       this.maxVolume - (float)(distance / this.maxDistance.units()),
                                                       this.minVolume,
                                                       this.maxVolume));
            }
        }
    }

    /**
     * Sets the lowest possible volume this instance will use.
     *
     * <p>
     * The value will be clamped between 0 and 1.
     * </p>
     *
     * @param minVolume
     */
    public void setMinVolume(float minVolume)
    {
        this.minVolume = NumberUtils.clamp(minVolume,
                                           0f,
                                           1f);
    }

    /**
     * Sets the highest possible volume this instance will use.
     *
     * <p>
     * The value will be clamped between 0 and 1.
     * </p>
     *
     * @param maxVolume
     */
    public void setMaxVolume(float maxVolume)
    {
        this.maxVolume = NumberUtils.clamp(maxVolume,
                                           0f,
                                           1f);
    }

    /**
     * Starts playing the sound if it is not null.
     *
     * <p>
     * This method will make a call to {@link #updateVolume()} before the sound is played.
     * </p>
     *
     * @see Sound#start()
     */
    public void start()
    {
        updateVolume();
        if (this.sound != null)
        {
            this.sound.start();
        }
    }

    /**
     * Starts looping the sound if it is not null.
     *
     * <p>
     * This method will make a call to {@link #updateVolume()} before the sound is played.
     * </p>
     *
     * @see Sound#loop()
     */
    public void loop()
    {
        updateVolume();
        if (this.sound != null)
        {
            this.sound.loop();
        }
    }

    /**
     * Starts playing the sound <i>count + 1</i> times if it is not null.
     *
     * <p>
     * This method will make a call to {@link #updateVolume()} before the sound is played.
     * </p>
     *
     * @see Sound#loop(int)
     */
    public void loop(int count)
    {
        updateVolume();
        if (this.sound != null)
        {
            this.sound.loop(count);
        }
    }

    /**
     * Stops the sound if it is not null.
     *
     * @see Sound#stop()
     */
    public void stop()
    {
        if (this.sound != null)
        {
            this.sound.stop();
        }
    }

    /**
     * Gets the scene this instance is used in.
     *
     * @return
     */
    public Scene getScene()
    {
        return this.scene;
    }

    /**
     * The x position of this instance used to calculate the distance to the volumeTarget.
     *
     * @return
     */
    public Unit getX()
    {
        return this.x;
    }

    /**
     * Sets the x position of this instance.
     *
     * @param x
     */
    public void setX(Unit x)
    {
        this.x = x;
    }

    /**
     * The y position of this instance used to calculate the distance to the volumeTarget.
     *
     * @return
     */
    public Unit getY()
    {
        return this.y;
    }

    /**
     * Sets the y position of this instance.
     *
     * @param y
     */
    public void setY(Unit y)
    {
        this.y = y;
    }

    /**
     * Gets the game object which is used to update the volume of the played sound based on its distance to this
     * instance.
     *
     * @return
     */
    public GameObject getVolumeTarget()
    {
        return this.volumeTarget;
    }

    /**
     * Calls {@link #updateVolume()}.
     *
     * @see bt.game.core.obj.intf.Tickable#tick(double)
     */
    @Override
    public void tick(double delta)
    {
        if (this.attachTarget != null)
        {
            setX(this.attachTarget.getX());
            setY(this.attachTarget.getY());
        }

        updateVolume();
    }
}