package bt.game.core.loop.impl;

import java.util.function.Consumer;

import bt.game.core.loop.abstr.GameLoop;
import bt.utils.Exceptions;

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
        super(tick,
              render);
    }

    @Override
    protected void tickLoop()
    {
        long last = System.nanoTime();

        while (this.running)
        {
            long time = System.nanoTime();
            // get the elapsed time from the last iteration
            long diff = time - last;
            // set the last time
            last = time;
            // convert from nanoseconds to seconds
            this.delta = (double)diff / NANO_TO_BASE;

            if (!this.isPaused)
            {
                runTick(this.delta);
            }
        }
    }

    @Override
    protected void renderLoop()
    {
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (this.running)
        {
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
                Exceptions.uncheck(() -> Thread.sleep(this.threadSleepers));
            }
        }
    }

    public double getLastDelta()
    {
        return this.delta;
    }
}