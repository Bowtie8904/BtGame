package bt.game.core.obj.hand.impl;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bt.game.core.obj.hand.ObjectHandler;
import bt.game.core.obj.intf.ActiveCollider;
import bt.game.core.obj.intf.Bounds;
import bt.game.core.obj.intf.PassiveCollider;
import bt.game.core.obj.intf.Tickable;
import bt.game.resource.render.Renderable;
import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.utils.log.Logger;

/**
 * A base implementation of the {@link ObjectHandler} interface.
 * 
 * <p>
 * This handler holds lists of different interface implementations that have been added via the
 * {@link #addObject(Object)} method to handle their specififc needs. A single object may implement multiple of these
 * interfaces.<br>
 * <br>
 * <b>Supported interfaces</b><br>
 * <ul>
 * <li>{@link Renderable}: Will be rendered during the {@link #render(Graphics)} method.</li>
 * <li>{@link Tickable}: The tick method of the object will be called whenever the {@link #tick()} of the handler is
 * called. Tick methods are invoked in a parallel stream so the calls might be out of order.</li>
 * <li>{@link Killable}: The kill method will be called during this handlers {@link #kill()} invokation.</li>
 * <li>{@link ActiveCollider}: Collision with {@link PassiveCollider passive colliders} is checked during every
 * {@link #tick()} call via the {@link #checkCollision()} method.</li>
 * <li>{@link PassiveCollider}: Collision with {@link ActiveCollider active colliders} is checked during every
 * {@link #tick()} call via the {@link #checkCollision()} method.</li>
 * </ul>
 * </p>
 * <p>
 * The list of {@link Renderable renderables} is sorted after the objects Z value (low to high) everytime the render
 * method is called. This behavior can be adapted by overriding {@link #sortObjects()}.
 * </p>
 * 
 * @author &#8904
 */
public class BaseObjectHandler implements ObjectHandler
{
    /** The list of tickable objects. */
    protected List<Tickable> tickables;

    /** The list of renderable objects. */
    protected List<Renderable> renderables;

    /** The list of active colliders. */
    protected List<ActiveCollider> activeColliders;

    /** The list of passive colliders. */
    protected List<PassiveCollider> passiveColliders;

    /** The list of killable objects. */
    protected List<Killable> killables;

    /** The comparator to sort renderables after their Z value. */
    protected Comparator<Renderable> zComparator;

    /**
     * Creates a new instance.
     */
    public BaseObjectHandler()
    {
        this.tickables = new CopyOnWriteArrayList<>();
        this.renderables = new CopyOnWriteArrayList<>();
        this.activeColliders = new CopyOnWriteArrayList<>();
        this.passiveColliders = new CopyOnWriteArrayList<>();
        this.killables = new CopyOnWriteArrayList<>();
        this.zComparator = new Comparator<Renderable>() {
            @Override
            public int compare(Renderable o1, Renderable o2)
            {
                if (o1.getZ().units() == o2.getZ().units())
                {
                    return 0;
                }
                else if (o1.getZ().units() < o2.getZ().units())
                {
                    return -1;
                }
                else if (o1.getZ().units() > o2.getZ().units())
                {
                    return 1;
                }
                return 0;
            }
        };
    }

    /**
     * Sorts the list of {@link Renderable}s by using the {@link #zComparator}.
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#sortObjects()
     */
    @Override
    public synchronized void sortObjects()
    {
        this.renderables.sort(this.zComparator);
    }

    /**
     * Adds the given object to the held lists based on its implemented interfaces.<br>
     * <br>
     * 
     * <b>Supported interfaces</b><br>
     * <ul>
     * <li>{@link Renderable}: Will be rendered during the {@link #render(Graphics)} method.</li>
     * <li>{@link Tickable}: The tick method of the object will be called whenever the {@link #tick()} of the handler is
     * called. Tick methods are invoked in a parallel stream so the calls might be out of order.</li>
     * <li>{@link Killable}: The kill method will be called during this handlers {@link #kill()} invokation.</li>
     * <li>{@link ActiveCollider}: Collision with {@link PassiveCollider passive colliders} is checked during every
     * {@link #tick()} call via the {@link #checkCollision()} method.</li>
     * <li>{@link PassiveCollider}: Collision with {@link ActiveCollider active colliders} is checked during every
     * {@link #tick()} call via the {@link #checkCollision()} method.</li>
     * </ul>
     * 
     * <p>
     * Objects that don't implement any of those interfaces are ignored.
     * </p>
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#addObject(java.lang.Object)
     */
    @Override
    public synchronized void addObject(Object object)
    {
        if (object instanceof Tickable)
        {
            this.tickables.add(Tickable.class.cast(object));
        }

        if (object instanceof Renderable)
        {
            this.renderables.add(Renderable.class.cast(object));
        }

        if (object instanceof Killable)
        {
            this.killables.add(Killable.class.cast(object));
        }

        if (object instanceof PassiveCollider)
        {
            this.passiveColliders.add(PassiveCollider.class.cast(object));
        }

        if (object instanceof ActiveCollider)
        {
            this.activeColliders.add(ActiveCollider.class.cast(object));
        }
    }

    /**
     * Removes the given object from all lists that it is a part of based on its interfaces.
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#removeObject(java.lang.Object)
     */
    @Override
    public synchronized void removeObject(Object object)
    {
        if (object instanceof Tickable)
        {
            this.tickables.remove(object);
        }

        if (object instanceof Renderable)
        {
            this.renderables.remove(object);
        }

        if (object instanceof Killable)
        {
            this.killables.remove(object);
        }

        if (object instanceof PassiveCollider)
        {
            this.passiveColliders.remove(object);
        }

        if (object instanceof ActiveCollider)
        {
            this.activeColliders.remove(object);
        }
    }

    /**
     * Calls the tick method of every added {@link Tickable} and checks for collisions between {@link ActiveCollider
     * active} and {@link PassiveCollider passive} colliders.
     * 
     * <p>
     * The tick methods are invoked in a parallel stream, so an order is not guaranteed.
     * </p>
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#tick()
     */
    @Override
    public void tick()
    {
        this.tickables.stream()
                .parallel()
                .forEach(Tickable::tick);

        checkCollision();
    }

    /**
     * Sorts the held {@link Renderable renderables} via {@link #sortObjects()} and calls their render methods after.
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g)
    {
        sortObjects();

        for (Renderable renderable : this.renderables)
        {
            renderable.render(g);
        }
    }
    
    /**
     * Checks all added game objects that implement the {@link ActiveCollider} interface to see if they have collided
     * with any of the game objects that implement the {@link PassiveCollider} interface. If two of such objects have
     * collided (checked via the {@link Bounds#intersects(Bounds) intersects} method) the
     * {@link ActiveCollider#activeCollision(PassiveCollider) activeCollision} and
     * {@link PassiveCollider#passiveCollision(ActiveCollider) passiveCollision} methods are called respectively.
     */
    public void checkCollision()
    {
        this.activeColliders.stream()
                .parallel()
                .forEach(object1 -> {
                    this.passiveColliders.stream()
                            .parallel()
                            .filter(o2 -> !o2.equals(object1) && o2.intersects(object1))
                            .forEach(object2 -> {
                                object1.activeCollision(object2);
                                object2.passiveCollision(object1);
                            });
                });
    }

    /**
     * Calls {@link Killable#kill() kill} on every added killable and clears all held lists.
     * 
     * @see bt.runtime.Killable#kill()
     */
    @Override
    public void kill()
    {
        Logger.global().print("Killing game object handler.");

        for (Killable obj : this.killables)
        {
            obj.kill();
        }

        this.tickables.clear();
        this.renderables.clear();
        this.activeColliders.clear();
        this.passiveColliders.clear();
        this.killables.clear();
    }

    /**
     * Registers this instance to the {@link InstanceKiller}.
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#init()
     */
    @Override
    public void init()
    {
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 2);
    }
}
