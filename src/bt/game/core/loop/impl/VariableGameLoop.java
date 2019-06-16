package bt.game.core.loop.impl;

import java.util.function.Consumer;

import bt.game.core.loop.GameLoop;

/**
 * @author &#8904
 *
 */
public class VariableGameLoop extends GameLoop
{
    protected double delta = 0;

    /**
     * @param tick
     * @param render
     */
    public VariableGameLoop(Consumer<Double> tick, Runnable render)
    {
        super(tick, render);
    }

    public double getLastDelta()
    {
        return this.delta;
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
        this.delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        long now;

        while (this.running)
        {
            ns = 1000000000 / this.ticksPerSecond;
            now = System.nanoTime();
            delta = (now - lastTime) / ns;
            lastTime = now;

            if (!this.isPaused)
            {
                runTick(this.delta);
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
                {}
            }
        }
    }
}