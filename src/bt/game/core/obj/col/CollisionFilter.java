package bt.game.core.obj.col;

import java.util.Arrays;

import org.dyn4j.collision.Filter;

/**
 * A collision filter which allows to specify classes whichs instances can cause a physical collision.
 * 
 * @author &#8904
 */
public class CollisionFilter implements Filter
{
    /** The classes that should trigger a collision. */
    private Class<?>[] classes;

    /** The object that is used by other CollisionFilters to see whether a collision should be triggered. */
    private Object collider;

    public CollisionFilter(Object collider, Class<?>... classes)
    {
        this.classes = classes;
        this.collider = collider;
    }

    public CollisionFilter(Object collider)
    {
        this(collider, new Class[] {});
    }

    public CollisionFilter()
    {
        this(null, new Class[] {});
    }

    public void setClasses(Class<?>... classes)
    {
        this.classes = classes;
    }

    public Class<?>[] getClasses()
    {
        return this.classes;
    }

    public Object getCollider()
    {
        return this.collider;
    }

    /**
     * @see org.dyn4j.collision.Filter#isAllowed(org.dyn4j.collision.Filter)
     */
    @Override
    public boolean isAllowed(Filter filter)
    {
        boolean allowed = this.classes.length == 0 && this.collider != null;

        if (!allowed && this.collider != null && filter instanceof CollisionFilter)
        {
            CollisionFilter colFilter = (CollisionFilter)filter;

            allowed = Arrays.stream(colFilter.getClasses())
                    .anyMatch(c -> c.isInstance(this.collider));

            if (!allowed)
            {
                allowed = Arrays.stream(this.classes)
                        .anyMatch(c -> c.isInstance(colFilter.getCollider()));
            }
        }

        return allowed;
    }
}