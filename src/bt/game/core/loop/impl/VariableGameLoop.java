package bt.game.core.loop.impl;

import bt.game.core.loop.abstr.GameLoop;
import bt.utils.Exceptions;

import java.util.function.Consumer;

/**
 * @author &#8904
 */
public class VariableGameLoop extends GameLoop
{
    protected double delta = 0;
    protected long loopTimeout = 0;

    /**
     * @param tick
     * @param render
     */
    public VariableGameLoop(Consumer<Double> tick, Runnable render)
    {
        super(tick, render);
    }

    @Override
    protected void loop()
    {
        long last = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;
        double renderIntervalDeltaSum = 0;
        double frameUpdateDeltaSum = 0;
        double frameCounterDeltaSum = 0;

        while (this.running)
        {
            long time = System.nanoTime();
            // get the elapsed time from the last iteration
            long diff = time - last;
            // set the last time
            last = time;
            // convert from nanoseconds to seconds
            this.delta = (double)diff / GameLoop.NANO_TO_BASE;
            renderIntervalDeltaSum += this.delta;
            frameUpdateDeltaSum += this.delta;
            frameCounterDeltaSum += this.delta;

            if (!this.isPaused)
            {
                runTick(this.delta);
            }

            if (this.renderInterval == 0 || renderIntervalDeltaSum >= this.renderInterval)
            {
                runRender();
                frames++;
                renderIntervalDeltaSum = 0;
            }

            if (frameUpdateDeltaSum >= this.frameCheckInterval)
            {
                if (this.onFpsUpdate != null)
                {
                    this.onFpsUpdate.run();
                }

                frameUpdateDeltaSum = 0;
            }

            if (frameCounterDeltaSum >= 0.1)
            {
                this.framesPerSecond = (int)(frames / frameCounterDeltaSum);
                frames = 0;
                frameCounterDeltaSum = 0;
            }

            if (this.loopTimeout > 0)
            {
                Exceptions.uncheck(() -> Thread.sleep(this.loopTimeout));
            }
        }
    }

    public double getLastDelta()
    {
        return this.delta;
    }

    public long getLoopTimeout()
    {
        return this.loopTimeout;
    }

    public void setLoopTimeout(long loopTimeout)
    {
        this.loopTimeout = loopTimeout;
    }
}