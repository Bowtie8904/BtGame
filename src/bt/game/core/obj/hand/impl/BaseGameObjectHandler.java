package bt.game.core.obj.hand.impl;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bt.game.core.obj.GameObject;
import bt.game.core.obj.hand.GameObjectHandler;
import bt.game.core.obj.intf.ActiveCollider;
import bt.game.core.obj.intf.PassiveCollider;
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
    protected List<GameObject> objects;
    private Comparator<GameObject> zComparator;

    /**
     * Creates a new instance.
     */
    public BaseGameObjectHandler()
    {
        this.objects = new CopyOnWriteArrayList<>();
        this.zComparator = new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2)
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
        this.objects.sort(this.zComparator);
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#addObject(bt.game.core.obj.GameObject)
     */
    @Override
    public synchronized void addObject(GameObject object)
    {
        this.objects.add(object);
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#removeObject(bt.game.core.obj.GameObject)
     */
    @Override
    public synchronized void removeObject(GameObject object)
    {
        this.objects.remove(object);
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#tick()
     */
    @Override
    public void tick()
    {
        for (GameObject object : this.objects)
        {
            object.tick();
        }

        checkCollision();
    }

    /**
     * @see bt.game.core.obj.hand.GameObjectHandler#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics g)
    {
        sortObjects();

        for (GameObject object : this.objects)
        {
            object.render(g);
        }
    }
    
    /**
     * Checks all added game objects that implement the {@link ActiveCollider} interface to see if they have collided
     * with any of the game objects that implement the {@link PassiveCollider} interface. If two of such objects have
     * collided (checked via the {@link GameObject#intersects(GameObject) intersects} method) the
     * {@link ActiveCollider#activeCollision(GameObject) activeCollision} and
     * {@link PassiveCollider#passiveCollision(GameObject) passiveCollision} methods are called respectively.
     */
    public void checkCollision()
    {
        this.objects.stream()
                .parallel()
                .filter(ActiveCollider.class::isInstance)
                .forEach(object1 -> {
                    this.objects.stream()
                            .parallel()
                            .filter(o2 -> !o2.equals(object1)
                                    && o2 instanceof PassiveCollider
                                    && o2.intersects(object1))
                            .forEach(object2 -> {
                                ((ActiveCollider)object1).activeCollision(object2);
                                ((PassiveCollider)object2).passiveCollision(object1);
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

        for (GameObject obj : this.objects)
        {
            if (obj instanceof Killable)
            {
                ((Killable)obj).kill();
            }
        }

        this.objects.clear();
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
