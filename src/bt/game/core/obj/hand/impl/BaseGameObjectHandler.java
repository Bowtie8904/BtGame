package bt.game.core.obj.hand.impl;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bt.game.core.obj.GameObject;
import bt.game.core.obj.hand.GameObjectHandler;
import bt.game.core.obj.intf.ActiveCollider;
import bt.game.core.obj.intf.Bounds;
import bt.game.core.obj.intf.PassiveCollider;
import bt.game.core.obj.intf.Tickable;
import bt.game.resource.render.Renderable;
import bt.runtime.InstanceKiller;
import bt.runtime.Killable;
import bt.utils.log.Logger;

/**
 * A base implementation of the {@link GameObjectHandler} interface.
 * 
 * <p>
 * This implementation holds a list of {@link GameObject game objects} which it iterates over to call their tick and
 * render methods. The list is sorted after the objects Z value (low to high) everytime the render method is called.
 * This behavior can be adapted by overriding {@link #sortObjects()}.
 * </p>
 * 
 * @author &#8904
 */
public class BaseGameObjectHandler implements GameObjectHandler
{
    protected List<Tickable> tickables;
    protected List<Renderable> renderables;
    protected List<ActiveCollider> activeColliders;
    protected List<PassiveCollider> passiveColliders;
    protected List<Killable> killables;
    private Comparator<Renderable> zComparator;

    /**
     * Creates a new instance.
     */
    public BaseGameObjectHandler()
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
     * @see bt.game.core.obj.hand.GameObjectHandler#sortObjects()
     */
    @Override
    public synchronized void sortObjects()
    {
        this.renderables.sort(this.zComparator);
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#addObject(bt.game.core.obj.GameObject)
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
     * @see bt.game.core.obj.hand.GameObjectHandler#removeObject(bt.game.core.obj.GameObject)
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
     * @see bt.game.core.obj.hand.GameObjectHandler#tick()
     */
    @Override
    public void tick()
    {
        this.tickables.stream()
                .parallel()
                .forEach(Tickable::tick);

        long before;
        long after;
        before = System.currentTimeMillis();

        checkCollision();

        after = System.currentTimeMillis();
        System.out.println(after - before);
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#render(java.awt.Graphics)
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
     * {@link ActiveCollider#activeCollision(GameObject) activeCollision} and
     * {@link PassiveCollider#passiveCollision(GameObject) passiveCollision} methods are called respectively.
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
     * @see bt.game.core.obj.hand.GameObjectHandler#init()
     */
    @Override
    public void init()
    {
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 2);
    }
}
