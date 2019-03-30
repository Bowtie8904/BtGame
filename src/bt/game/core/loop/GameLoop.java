package bt.game.core.loop;

import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.utils.log.Logger;
import bt.utils.thread.Threads;

/**
 * A class to handle tick and render method calls at a (nearly) fix tick and frame rate.
 * 
 * @author &#8904
 */
public class GameLoop implements Runnable, Killable
{
    /**
     * Indicates whether this loop is currently running. Setting this to false is the easiest way to terminate the loop.
     */
    protected volatile boolean running;

    /** The current frames per second. */
    protected int framesPerSecond = -1;

    /** The number of times the {@link #tick} should be called per second. */
    protected double ticksPerSecond = 60.0;

    /** The interval (in milliseconds) at which the {@link #framesPerSecond frame rate} is updated. */
    protected int frameCheckInterval = 200;

    /**
     * The amount of milliseconds that the loop thread will sleep after each iteration to keep the frame rate stable.
     */
    protected int threadSleepers;

    /**
     * The target frame rate that the loop should aim for. Setting this to -1 causes the loop to try and achieve the
     * highest frame rate possible.
     */
    protected int desiredFramesPerSecond = -1;

    /** The set tick runnable that is called {@link #ticksPerSecond n} times per second. */
    protected Runnable tick;

    /** The set render runnable that is called {@link #framesPerSecond n} times per second. */
    protected Runnable render;

    /** The set runnable that is executed whenever the frame rate is updated. See {@link #frameCheckInterval}. */
    protected Runnable onFpsUpdate;

    /**
     * Creates a new instance and sets the runnables for tick and render methods.
     * 
     * <p>
     * This constructor registers the loop to the {@link InstanceKiller} with a priority of 1.
     * </p>
     * 
     * @param tick
     * @param render
     */
    public GameLoop(Runnable tick, Runnable render)
    {
        this.tick = tick;
        this.render = render;
        InstanceKiller.killOnShutdown(this, 1);
    }

    /**
     * Kills the loop. This method just adds logging to the {@link #stop()} call.
     * 
     * <p>
     * This will not cancel the current iteration. The tick and render methods might still be called once before the
     * loop truly terminates.
     * </p>
     * 
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Killing game loop.");
        stop();
    }

    /**
     * Starts this loop in a new thread if it is not started yet.
     */
    public void start()
    {
        if (!this.running)
        {
            this.running = true;
            Threads.get().execute(this, "GAME_LOOP");
        }
    }

    /**
     * Stops the loop if it is currently running.
     * 
     * <p>
     * This will not cancel the current iteration. The tick and render methods might still be called once before the
     * loop truly terminates.
     * </p>
     */
    public void stop()
    {
        this.running = false;
    }

    /**
     * The core of the loop.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        long lastTime = System.nanoTime();
        double ns;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        long now;

        while (this.running)
        {
            ns = 1000000000 / this.ticksPerSecond;
            now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1)
            {
                runTick();
                delta -- ;
            }

            runRender();
            frames ++ ;

            if (System.currentTimeMillis() - timer > this.frameCheckInterval)
            {
                timer += this.frameCheckInterval;
                frames *= 1000 / this.frameCheckInterval;
                this.framesPerSecond = frames;
                frames = 0;

                if (this.onFpsUpdate != null)
                {
                    this.onFpsUpdate.run();
                }

                if (this.desiredFramesPerSecond > -1)
                {
                    if (this.framesPerSecond > this.desiredFramesPerSecond)
                    {
                        this.threadSleepers ++ ;
                    }
                    else if (this.framesPerSecond < this.desiredFramesPerSecond)
                    {
                        this.threadSleepers -- ;
                    }
                }
            }

            if (this.desiredFramesPerSecond > -1 && this.threadSleepers > 0)
            {
                try
                {
                    Thread.sleep(this.threadSleepers);
                }
                catch (InterruptedException e)
                {
                }
            }
        }
    }

    /**
     * Sets the target frame rate that this loop will try to hold.
     * 
     * @param desiredFramesPerSecond
     *            The target frame rate. Setting this to -1 causes the loop to try and achieve the highest frame rate
     *            possible.
     */
    public void setFrameRate(int desiredFramesPerSecond)
    {
        this.desiredFramesPerSecond = desiredFramesPerSecond;
    }

    /**
     * Defines an action that is executed when the frames per second are updated.
     * 
     * @param onUpdate
     */
    public void onFpsUpdate(Runnable onUpdate)
    {
        this.onFpsUpdate = onUpdate;
    }

    /**
     * Sets how many times the frames per second value (accessible via {@link #getFramesPerSecond()}) will be updated.
     * 
     * @param updatesPerSecond
     */
    public void setFpsUpdateRate(int updatesPerSecond)
    {
        this.frameCheckInterval = 1000 / updatesPerSecond;
    }

    /**
     * Gets the current frame rate. This value is updated {@link #setFpsUpdateRate(int) n} times per second.
     * 
     * @return
     */
    public int getFramesPerSecond()
    {
        return this.framesPerSecond;
    }

    /**
     * Gets how many times the tick method is called per second.
     * 
     * @return
     */
    public double getTicksPerSecond()
    {
        return this.ticksPerSecond;
    }

    /**
     * Sets how many times per second the given tick method should be called.
     * 
     * @param ticks
     */
    public void setTicksPerSecond(double ticks)
    {
        this.ticksPerSecond = ticks;
    }

    /**
     * Runs the render runnable if it is not null.
     */
    private void runRender()
    {
        if (this.render != null)
        {
            this.render.run();
        }
    }

    /**
     * Runs the tick runnable if it is not null.
     */
    private void runTick()
    {
        if (this.tick != null)
        {
            this.tick.run();
        }
    }
}