package bt.game.core.obj.hand.impl;

import java.awt.Graphics;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bt.game.core.obj.GameObject;
import bt.game.core.obj.hand.GameObjectHandler;

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
        this.zComparator = new Comparator<GameObject>()
        {
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
}