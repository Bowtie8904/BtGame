package bt.game.core.loop;

import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.utils.log.Logger;
import bt.utils.thread.Threads;

/**
 * @author &#8904
 *
 */
public class GameLoop implements Runnable, Killable
{
    protected boolean running;
    protected int framesPerSecond = -1;
    protected double ticksPerSecond = 60.0;
    protected int frameCheckInterval = 200;
    protected int threadSleepers;
    protected int desiredFramesPerSecond = 60;
    protected Runnable tick;
    protected Runnable render;
    protected Runnable onFpsUpdate;

    public GameLoop(Runnable tick, Runnable render)
    {
        this.tick = tick;
        this.render = render;
        InstanceKiller.killOnShutdown(this, 1);
    }

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
                if (this.tick != null)
                {
                    this.tick.run();
                }
                delta -- ;
            }

            if (this.render != null)
            {
                this.render.run();
            }
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

                if (this.framesPerSecond > this.desiredFramesPerSecond)
                {
                    this.threadSleepers ++ ;
                }
                else if (this.framesPerSecond < this.desiredFramesPerSecond)
                {
                    this.threadSleepers -- ;
                }
            }

            try
            {
                Thread.sleep(this.threadSleepers);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

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

    public int getFramesPerSecond()
    {
        return this.framesPerSecond;
    }

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
}