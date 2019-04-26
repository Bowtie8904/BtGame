package bt.game.core.obj.hand.impl;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dyn4j.collision.manifold.Manifold;
import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.contact.ContactConstraint;
import org.dyn4j.dynamics.joint.Joint;

import bt.game.core.obj.col.BroadPhaseCollider;
import bt.game.core.obj.col.ConstraintCollider;
import bt.game.core.obj.col.ManifoldCollider;
import bt.game.core.obj.col.NarrowPhaseCollider;
import bt.game.core.obj.hand.ObjectHandler;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.Scene;
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
 * <b>Supported types</b><br>
 * <ul>
 * <li>{@link Renderable}: Will be rendered during the {@link #render(Graphics)} method.</li>
 * <li>{@link Tickable}: The tick method of the object will be called whenever the {@link #tick()} of the handler is
 * called. Tick methods are invoked in a parallel stream so the calls might be out of order.</li>
 * <li>{@link Killable}: The kill method will be called during this handlers {@link #kill()} invokation.</li>
 * <li>{@link BroadPhaseCollider}: onCollide is called when collision events are received.</li>
 * <li>{@link NarrowPhaseCollider}: onCollide is called when collision events are received.</li>
 * <li>{@link ManifoldCollider}: onCollide is called when collision events are received.</li>
 * <li>{@link ConstraintCollider}: onCollide is called when collision events are received.</li>
 * <li>{@link Body}: Added to the world object of the scene.</li>
 * <li>{@link Joint}: Added to the world object of the scene.</li>
 * </ul>
 * </p>
 * <p>
 * The list of {@link Renderable renderables} is sorted after the objects Z value (low to high) everytime the render
 * method is called. This behavior can be adapted by overriding {@link #sortObjects()}.
 * </p>
 * 
 * @author &#8904
 */
public class BaseObjectHandler implements ObjectHandler, CollisionListener
{
    /** The list of tickable objects. */
    protected List<Tickable> tickables;

    /** The list of renderable objects. */
    protected List<Renderable> renderables;

    /** The list of killable objects. */
    protected List<Killable> killables;

    /** The map of BroadPhaseCollider objects. */
    protected Map<Body, BroadPhaseCollider> broadColliders;

    /** The map of NarrowPhaseCollider objects. */
    protected Map<Body, NarrowPhaseCollider> narrowColliders;

    /** The map of ManifoldCollider objects. */
    protected Map<Body, ManifoldCollider> manifoldColliders;

    /** The map of ConstraintCollider objects. */
    protected Map<Body, ConstraintCollider> constraintColliders;

    /** The comparator to sort renderables after their Z value. */
    protected Comparator<Renderable> zComparator;

    /** The scene that uses this handler. */
    protected Scene scene;

    /**
     * Creates a new instance.
     */
    public BaseObjectHandler(Scene scene)
    {
        this.scene = scene;
        this.tickables = new CopyOnWriteArrayList<>();
        this.renderables = new CopyOnWriteArrayList<>();
        this.killables = new CopyOnWriteArrayList<>();
        this.broadColliders = new Hashtable<>();
        this.narrowColliders = new Hashtable<>();
        this.manifoldColliders = new Hashtable<>();
        this.constraintColliders = new Hashtable<>();
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
     * <li>{@link BroadPhaseCollider}: onCollide is called when collision events are received.</li>
     * <li>{@link NarrowPhaseCollider}: onCollide is called when collision events are received.</li>
     * <li>{@link ManifoldCollider}: onCollide is called when collision events are received.</li>
     * <li>{@link ConstraintCollider}: onCollide is called when collision events are received.</li>
     * <li>{@link Body}: Added to the world object of the scene.</li>
     * <li>{@link Joint}: Added to the world object of the scene.</li>
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
        if (object instanceof Body)
        {
            this.scene.getWorld().addBody(Body.class.cast(object));
        }

        if (object instanceof Joint)
        {
            this.scene.getWorld().addJoint(Joint.class.cast(object));
        }

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

        if (object instanceof BroadPhaseCollider)
        {
            BroadPhaseCollider collider = BroadPhaseCollider.class.cast(object);
            if (collider.getBody() != null)
            {
                this.broadColliders.put(collider.getBody(), collider);
            }
        }

        if (object instanceof NarrowPhaseCollider)
        {
            NarrowPhaseCollider collider = NarrowPhaseCollider.class.cast(object);
            if (collider.getBody() != null)
            {
                this.narrowColliders.put(collider.getBody(), collider);
            }
        }

        if (object instanceof ManifoldCollider)
        {
            ManifoldCollider collider = ManifoldCollider.class.cast(object);
            if (collider.getBody() != null)
            {
                this.manifoldColliders.put(collider.getBody(), collider);
            }
        }

        if (object instanceof ConstraintCollider)
        {
            ConstraintCollider collider = ConstraintCollider.class.cast(object);
            if (collider.getBody() != null)
            {
                this.constraintColliders.put(collider.getBody(), collider);
            }
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
        if (object instanceof Body)
        {
            this.scene.getWorld().removeBody(Body.class.cast(object));
        }

        if (object instanceof Joint)
        {
            this.scene.getWorld().removeJoint(Joint.class.cast(object));
        }

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

        if (object instanceof BroadPhaseCollider)
        {
            BroadPhaseCollider collider = BroadPhaseCollider.class.cast(object);
            this.broadColliders.remove(collider.getBody());
        }

        if (object instanceof NarrowPhaseCollider)
        {
            NarrowPhaseCollider collider = NarrowPhaseCollider.class.cast(object);
            this.narrowColliders.remove(collider.getBody());
        }

        if (object instanceof ManifoldCollider)
        {
            ManifoldCollider collider = ManifoldCollider.class.cast(object);
            this.manifoldColliders.remove(collider.getBody());
        }

        if (object instanceof ConstraintCollider)
        {
            ConstraintCollider collider = ConstraintCollider.class.cast(object);
            this.constraintColliders.remove(collider.getBody());
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
    public void tick(double delta)
    {
        this.tickables.stream()
                .parallel()
                .forEach(t -> t.tick(delta));
    }

    /**
     * Sorts the held {@link Renderable renderables} via {@link #sortObjects()} and calls their render methods after.
     * 
     * @see bt.game.core.obj.hand.ObjectHandler#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g)
    {
        sortObjects();

        for (Renderable renderable : this.renderables)
        {
            renderable.render(g);
        }
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
        this.killables.clear();
        this.broadColliders.clear();
        this.narrowColliders.clear();
        this.manifoldColliders.clear();
        this.constraintColliders.clear();
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

        if (this.scene.getWorld() != null)
        {
            this.scene.getWorld().addListener(this);
        }
    }

    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture)
     */
    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2)
    {
        BroadPhaseCollider collider1 = this.broadColliders.get(body1);
        BroadPhaseCollider collider2 = this.broadColliders.get(body2);

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(body1, fixture1, body2, fixture2);
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(body1, fixture1, body2, fixture2);
        }

        return proceed;
    }

    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.narrowphase.Penetration)
     */
    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2,
            Penetration penetration)
    {
        NarrowPhaseCollider collider1 = this.narrowColliders.get(body1);
        NarrowPhaseCollider collider2 = this.narrowColliders.get(body2);

        boolean proceed = true;
        
        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(body1, fixture1, body2, fixture2, penetration);
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(body1, fixture1, body2, fixture2, penetration);
        }

        return proceed;
    }

    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture,
     *      org.dyn4j.dynamics.Body, org.dyn4j.dynamics.BodyFixture, org.dyn4j.collision.manifold.Manifold)
     */
    @Override
    public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Manifold manifold)
    {
        ManifoldCollider collider1 = this.manifoldColliders.get(body1);
        ManifoldCollider collider2 = this.manifoldColliders.get(body2);

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(body1, fixture1, body2, fixture2, manifold);
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(body1, fixture1, body2, fixture2, manifold);
        }

        return proceed;
    }

    /**
     * @see org.dyn4j.dynamics.CollisionListener#collision(org.dyn4j.dynamics.contact.ContactConstraint)
     */
    @Override
    public boolean collision(ContactConstraint contactConstraint)
    {
        ConstraintCollider collider1 = this.constraintColliders.get(contactConstraint.getBody1());
        ConstraintCollider collider2 = this.constraintColliders.get(contactConstraint.getBody2());

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(contactConstraint);
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(contactConstraint);
        }

        return proceed;
    }
}
