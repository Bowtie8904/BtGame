package bt.game.core.obj.col;

import java.util.Arrays;

import org.dyn4j.collision.Filter;

/**
 * A collision filter which allows to specify classes whichs instances can cause a physical collision.
 * 
 * <p>
 * If a single class uses this type of collision filter, then all classes should use it.
 * </p>
 * 
 * <p>
 * The conditions for a successful collision are described {@link #isAllowed(Filter) here}.
 * </p>
 * 
 * @author &#8904
 */
public class CollisionFilter implements Filter
{
    /** The classes that should trigger a collision. */
    private Class<?>[] classes;

    /** The object that is used by other CollisionFilters to see whether a collision should be triggered. */
    private Object collider;

    /**
     * Creates a new instance.
     * 
     * <p>
     * The given collider object is used for collision filtering by other filter instances.
     * </p>
     * 
     * <p>
     * This instance will only allow collisions with objects of types (or subtypes) from the given class array.
     * </p>
     */
    public CollisionFilter(Object collider, Class<?>... classes)
    {
        this.classes = classes == null ? new Class[] {} : classes;
        this.collider = collider;
    }

    /**
     * Creates a new instance.
     * 
     * <p>
     * The given collider object is used for collision filtering by other filter instances.
     * </p>
     */
    public CollisionFilter(Object collider)
    {
        this(collider, new Class[] {});
    }

    /**
     * Creates a new instance.
     * 
     * <p>
     * This instance will only allow collisions with objects of types (or subtypes) from the given class array.
     * </p>
     */
    public CollisionFilter(Class<?>... classes)
    {
        this(null, classes);
    }

    /**
     * Creates a new instance.
     * 
     * <p>
     * The array of allowed classes will be empty and the collider object will be null, therefore allowing collisions
     * with all types.
     * </p>
     */
    public CollisionFilter()
    {
        this(null, new Class[] {});
    }

    /**
     * Gets the classes whichs instances are allowed in collisions by this filter.
     * 
     * <p>
     * If the array is empty every class can be involved in a collision.
     * </p>
     * 
     * @return
     */
    public Class<?>[] getClasses()
    {
        return this.classes;
    }

    /**
     * Gets the object that defines the type of this filter. Used to check whether a collision between this filter and
     * another one is allowed.
     * 
     * @return
     */
    public Object getCollider()
    {
        return this.collider;
    }

    /**
     * Returns true if any of these conditions is met:
     * <ul>
     * <li>the array of allowed classes is empty</li>
     * <li>the given collider object is null</li>
     * <li>the type of the given collider object is contained in the specified class array, so that
     * {@link Class#isInstance(Object) Class.isInstance(collider)} returns true for any of the contained types</li>
     * </ul>
     * 
     * @param collider
     * @return
     */
    public boolean allowsCollision(Object collider)
    {
        return this.classes.length == 0
                || collider == null
                || Arrays.stream(this.classes)
                        .anyMatch(c -> c.isInstance(collider));
    }

    /**
     * Returns true and therefore allows a collision if any of these conditions is met:
     * 
     * <ul>
     * <li>the given filter is an instance of CollisionFilter and both implementations of
     * {@link #allowsCollision(Object)} (of this instance and the given one) return true</li>
     * <li>if the given filter is not an instance of CollisionFilter and its {@link #isAllowed(Filter)} method returns
     * true</li>
     * </ul>
     * 
     * @see org.dyn4j.collision.Filter#isAllowed(org.dyn4j.collision.Filter)
     */
    @Override
    public boolean isAllowed(Filter filter)
    {
        boolean allowed = false;

        if (filter instanceof CollisionFilter)
        {
            CollisionFilter colFilter = (CollisionFilter)filter;
            allowed = allowsCollision(colFilter.getCollider()) && colFilter.allowsCollision(this.collider);
        }
        else
        {
            allowed = filter.isAllowed(this);
        }

        return allowed;
    }
}