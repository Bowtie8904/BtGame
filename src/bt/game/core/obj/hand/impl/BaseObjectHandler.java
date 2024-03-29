package bt.game.core.obj.hand.impl;

import bt.game.core.container.abstr.GameContainer;
import bt.game.core.obj.col.intf.*;
import bt.game.core.obj.gravity.GravityAffected;
import bt.game.core.obj.hand.intf.ObjectHandler;
import bt.game.core.obj.intf.Refreshable;
import bt.game.core.obj.intf.Tickable;
import bt.game.core.scene.intf.Scene;
import bt.game.resource.render.intf.Renderable;
import bt.game.resource.render.light.intf.LightSource;
import bt.game.resource.render.light.mask.LightMask;
import bt.log.Log;
import bt.runtime.InstanceKiller;
import bt.types.Killable;
import bt.utils.NumberUtils;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.collision.continuous.TimeOfImpact;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.PhysicsBody;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.dynamics.contact.SolvedContact;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.world.BroadphaseCollisionData;
import org.dyn4j.world.ContactCollisionData;
import org.dyn4j.world.ManifoldCollisionData;
import org.dyn4j.world.NarrowphaseCollisionData;
import org.dyn4j.world.listener.CollisionListener;
import org.dyn4j.world.listener.ContactListener;
import org.dyn4j.world.listener.TimeOfImpactListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

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
 * <li>{@link GravityAffected}: The y velocity of these objects will be adjusted each tick to simulate gravity.</li>
 * <li>{@link Refreshable}: This handlers {@link #refresh()} method is gonna forward the call to all registered
 * refreshables.</li>
 * <li>{@link BroadPhaseCollider}: onCollision is called when collision events are received.</li>
 * <li>{@link NarrowPhaseCollider}: onCollision is called when collision events are received.</li>
 * <li>{@link ManifoldCollider}: onCollision is called when collision events are received.</li>
 * <li>{@link ConstraintCollider}: onCollision is called when collision events are received.</li>
 * <li>{@link Contacter}: onContactBegin and onContactEnd is called when contact events are received.</li>
 * <li>{@link TimeOfImpactCollider}: onCollision is called when collision events are received.</li>
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
public class BaseObjectHandler implements ObjectHandler, CollisionListener, ContactListener, TimeOfImpactListener
{
    /**
     * A list of objects that are meant to be removed with the next tick.
     */
    protected List<Object> toBeRemoved;

    /**
     * A list of objects that are meant to be added with the next tick.
     */
    protected List<Object> toBeAdded;

    /**
     * The list of tickable objects.
     */
    protected List<Tickable> tickables;

    /**
     * The list of refreshable objects.
     */
    protected List<Refreshable> refreshables;

    /**
     * The list of renderable objects.
     */
    protected List<Renderable> renderables;

    /**
     * The list of killable objects.
     */
    protected List<Killable> killables;

    /**
     * The list of GravityAffected objects.
     */
    protected List<GravityAffected> gravityAffecteds;

    /**
     * The list of LightSource objects.
     */
    protected List<LightSource> lightSources;

    /**
     * The map of BroadPhaseCollider objects.
     */
    protected Map<CollisionBody, BroadPhaseCollider> broadColliders;

    /**
     * The map of NarrowPhaseCollider objects.
     */
    protected Map<CollisionBody, NarrowPhaseCollider> narrowColliders;

    /**
     * The map of ManifoldCollider objects.
     */
    protected Map<CollisionBody, ManifoldCollider> manifoldColliders;

    /**
     * The map of ConstraintCollider objects.
     */
    protected Map<CollisionBody, ConstraintCollider> constraintColliders;

    /**
     * The map of Contacter objects.
     */
    protected Map<CollisionBody, Contacter> contacters;

    /**
     * The map of TimeOfImpactCollider objects.
     */
    protected Map<CollisionBody, TimeOfImpactCollider> timeOfImpactColliders;

    /**
     * The comparator to sort renderables after their Z value.
     */
    protected Comparator<Renderable> zComparator;

    /**
     * The scene that uses this handler.
     */
    protected Scene scene;

    /**
     * Creates a new instance.
     */
    public BaseObjectHandler(Scene scene)
    {
        this.scene = scene;
        this.tickables = new CopyOnWriteArrayList<>();
        this.refreshables = new CopyOnWriteArrayList<>();
        this.renderables = new CopyOnWriteArrayList<>();
        this.killables = new CopyOnWriteArrayList<>();
        this.gravityAffecteds = new CopyOnWriteArrayList<>();
        this.lightSources = new CopyOnWriteArrayList<>();
        this.toBeRemoved = new CopyOnWriteArrayList<>();
        this.toBeAdded = new CopyOnWriteArrayList<>();
        this.broadColliders = new Hashtable<>();
        this.narrowColliders = new Hashtable<>();
        this.manifoldColliders = new Hashtable<>();
        this.constraintColliders = new Hashtable<>();
        this.contacters = new Hashtable<>();
        this.timeOfImpactColliders = new Hashtable<>();

        this.zComparator = new Comparator<>()
        {
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
     * @see bt.game.core.obj.hand.intf.ObjectHandler#sortObjects()
     */
    @Override
    public synchronized void sortObjects()
    {
        this.renderables.sort(this.zComparator);
    }

    /**
     * Adds the given object to the held lists based on its implemented interfaces.<br>
     * <br>
     * The actual adding of the object will happen at the start of the next tick iteration.
     *
     * <b>Supported interfaces</b><br>
     * <ul>
     * <li>{@link Renderable}: Will be rendered during the {@link #render(Graphics)} method.</li>
     * <li>{@link Tickable}: The tick method of the object will be called whenever the {@link #tick()} of the handler is
     * called. Tick methods are invoked in a parallel stream so the calls might be out of order.</li>
     * <li>{@link Killable}: The kill method will be called during this handlers {@link #kill()} invokation.</li>
     * <li>{@link GravityAffected}: The y velocity of these objects will be adjusted each tick to simulate gravity.</li>
     * <li>{@link LightSource}: THeir light area will be drawn during rendering..</li>
     * <li>{@link Refreshable}: This handlers {@link #refresh()} method is gonna forward the call to all registered
     * refreshables.</li>
     * <li>{@link BroadPhaseCollider}: onCollision is called when collision events are received.</li>
     * <li>{@link NarrowPhaseCollider}: onCollision is called when collision events are received.</li>
     * <li>{@link ManifoldCollider}: onCollision is called when collision events are received.</li>
     * <li>{@link ConstraintCollider}: onCollision is called when collision events are received.</li>
     * <li>{@link Contacter}: onContactBegin and onContactEnd is called when contact events are received.</li>
     * <li>{@link TimeOfImpactCollider}: onCollision is called when collision events are received.</li>
     * <li>{@link Body}: Added to the world object of the scene.</li>
     * <li>{@link Joint}: Added to the world object of the scene.</li>
     * </ul>
     *
     * <p>
     * Objects that don't implement any of those interfaces are ignored.
     * </p>
     *
     * @see bt.game.core.obj.hand.intf.ObjectHandler#addObject(java.lang.Object)
     */
    @Override
    public synchronized void addObject(Object object)
    {
        Log.entry(object);
        this.toBeAdded.add(object);
        Log.exit();
    }

    protected void addNewObjects()
    {
        for (Object object : this.toBeAdded)
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

            if (object instanceof Refreshable)
            {
                this.refreshables.add(Refreshable.class.cast(object));
            }

            if (object instanceof Renderable)
            {
                this.renderables.add(Renderable.class.cast(object));
            }

            if (object instanceof Killable)
            {
                this.killables.add(Killable.class.cast(object));
            }

            if (object instanceof GravityAffected)
            {
                this.gravityAffecteds.add(GravityAffected.class.cast(object));
            }

            if (object instanceof LightSource)
            {
                this.lightSources.add(LightSource.class.cast(object));
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

            if (object instanceof Contacter)
            {
                Contacter collider = Contacter.class.cast(object);
                if (collider.getBody() != null)
                {
                    this.contacters.put(collider.getBody(), collider);
                }
            }

            if (object instanceof TimeOfImpactCollider)
            {
                TimeOfImpactCollider collider = TimeOfImpactCollider.class.cast(object);
                if (collider.getBody() != null)
                {
                    this.timeOfImpactColliders.put(collider.getBody(), collider);
                }
            }
        }

        if (!this.toBeAdded.isEmpty())
        {
            Log.debug("Added {} objects", this.toBeAdded.size());

            Log.debug("tickables: {}", this.tickables.size());
            Log.debug("refreshables: {}", this.refreshables.size());
            Log.debug("renderables: {}", this.renderables.size());
            Log.debug("killables: {}", this.killables.size());
            Log.debug("gravityAffecteds: {}", this.gravityAffecteds.size());
            Log.debug("lightSources: {}", this.lightSources.size());
            Log.debug("broadColliders: {}", this.broadColliders.size());
            Log.debug("narrowColliders: {}", this.narrowColliders.size());
            Log.debug("manifoldColliders: {}", this.manifoldColliders.size());
            Log.debug("constraintColliders: {}", this.constraintColliders.size());
            Log.debug("contacters: {}", this.contacters.size());
            Log.debug("timeOfImpactColliders: {}", this.timeOfImpactColliders.size());
        }

        this.toBeAdded.clear();
    }

    /**
     * Marks the given object for removal from all lists that it is a part of based on its interfaces.
     * <p>
     * The removal will happen at the start of the next tick iteration.
     *
     * @see bt.game.core.obj.hand.intf.ObjectHandler#removeObject(java.lang.Object)
     */
    @Override
    public synchronized void removeObject(Object object)
    {
        Log.entry(object);
        this.toBeRemoved.add(object);
        Log.exit();
    }

    protected synchronized void removeMarkedObjects()
    {
        for (Object object : this.toBeRemoved)
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

            if (object instanceof Refreshable)
            {
                this.refreshables.remove(object);
            }

            if (object instanceof Renderable)
            {
                this.renderables.remove(object);
            }

            if (object instanceof Killable)
            {
                this.killables.remove(object);
            }

            if (object instanceof GravityAffected)
            {
                this.gravityAffecteds.remove(object);
            }

            if (object instanceof LightSource)
            {
                this.lightSources.remove(object);
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

            if (object instanceof Contacter)
            {
                Contacter collider = Contacter.class.cast(object);
                this.contacters.remove(collider.getBody());
            }

            if (object instanceof TimeOfImpactCollider)
            {
                TimeOfImpactCollider collider = TimeOfImpactCollider.class.cast(object);
                this.timeOfImpactColliders.remove(collider.getBody());
            }
        }

        if (!this.toBeRemoved.isEmpty())
        {
            Log.debug("Removed {} objects", this.toBeRemoved.size());

            Log.debug("tickables: {}", this.tickables.size());
            Log.debug("refreshables: {}", this.refreshables.size());
            Log.debug("renderables: {}", this.renderables.size());
            Log.debug("killables: {}", this.killables.size());
            Log.debug("gravityAffecteds: {}", this.gravityAffecteds.size());
            Log.debug("lightSources: {}", this.lightSources.size());
            Log.debug("broadColliders: {}", this.broadColliders.size());
            Log.debug("narrowColliders: {}", this.narrowColliders.size());
            Log.debug("manifoldColliders: {}", this.manifoldColliders.size());
            Log.debug("constraintColliders: {}", this.constraintColliders.size());
            Log.debug("contacters: {}", this.contacters.size());
            Log.debug("timeOfImpactColliders: {}", this.timeOfImpactColliders.size());
        }

        this.toBeRemoved.clear();
    }

    /**
     * Calls the tick method of every added {@link Tickable} and checks for collisions between {@link ActiveCollider
     * active} and {@link PassiveCollider passive} colliders.
     *
     * <p>
     * The tick methods are invoked in a parallel stream, so an order is not guaranteed.
     * </p>
     *
     * @see bt.game.core.obj.hand.intf.ObjectHandler#tick()
     */
    @Override
    public void tick(double delta)
    {
        removeMarkedObjects();
        addNewObjects();

        this.tickables.stream()
                      .parallel()
                      .forEach(t -> t.tick(delta));
    }

    /**
     * Sorts the held {@link Renderable renderables} via {@link #sortObjects()} and calls their render methods after.
     *
     * @see bt.game.core.obj.hand.intf.ObjectHandler#render(java.awt.Graphics)
     */
    @Override
    public void render(Graphics2D g, boolean debugRendering)
    {
        sortObjects();

        this.renderables.stream()
                        .filter(Renderable::shouldRender)
                        .forEach(r -> r.render(g, debugRendering));
    }

    @Override
    public void renderLightSources(Graphics2D g, boolean debugRendering)
    {
        BufferedImage mask = new BufferedImage((int)GameContainer.width().pixels(),
                                               (int)GameContainer.height().pixels(),
                                               BufferedImage.TYPE_INT_ARGB);

        Graphics2D maskG = mask.createGraphics();

        for (LightSource light : this.lightSources)
        {
            light.getLightMask().apply(maskG, light.getLightX(), light.getLightY());
        }

        mask.getAlphaRaster();

        int[] maskPixels = (int[])mask.getRaster().getDataElements(0, 0, mask.getWidth(), mask.getHeight(), null);

        int R = LightMask.DARKNESS.getRed();
        int G = LightMask.DARKNESS.getGreen();
        int B = LightMask.DARKNESS.getBlue();

        for (int i = 0; i < maskPixels.length; i++)
        {
            int A = (maskPixels[i] >> 24) & 255;

            if (A != 0)
            {
                A = Math.max(LightMask.DARKNESS.getAlpha() - A, 0);

                maskPixels[i] = (A) << 24 |
                        0x00FF0000 & (B) << 16 |
                        0x0000FF00 & (G) << 8 |
                        0x000000FF & (R);
            }
            else
            {
                maskPixels[i] = LightMask.DARKNESS.getRGB();
            }
        }

        mask.getRaster().setDataElements(0, 0, mask.getWidth(), mask.getHeight(), maskPixels);

        g.drawImage(mask, 0, 0, null);
    }

    @Override
    public void updateGravityVelocities(double delta)
    {
        this.gravityAffecteds.stream()
                             .parallel()
                             .filter(g -> g.getGravityVelocityGain() > 0)
                             .forEach(g -> {
                                 double newV = NumberUtils.clamp(g.getVelocityY() + g.getGravityVelocityGain() * delta,
                                                                 Long.MIN_VALUE,
                                                                 g.getMaxGravityVelocity());
                                 g.setVelocityY(newV);
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
        Log.debug("Killing game object handler.");

        for (Killable obj : this.killables)
        {
            obj.kill();
        }

        this.tickables.clear();
        this.refreshables.clear();
        this.renderables.clear();
        this.killables.clear();
        this.gravityAffecteds.clear();
        this.broadColliders.clear();
        this.narrowColliders.clear();
        this.manifoldColliders.clear();
        this.constraintColliders.clear();
        this.contacters.clear();
        this.timeOfImpactColliders.clear();
    }

    /**
     * Registers this instance to the {@link InstanceKiller}.
     *
     * @see bt.game.core.obj.hand.intf.ObjectHandler#init()
     */
    @Override
    public void init()
    {
        Log.entry();
        InstanceKiller.killOnShutdown(this, Integer.MIN_VALUE + 102);

        if (this.scene.getWorld() != null)
        {
            this.scene.getWorld().addCollisionListener(this);
            this.scene.getWorld().addContactListener(this);
            this.scene.getWorld().addTimeOfImpactListener(this);
        }

        Log.exit();
    }

    /**
     * @see bt.game.core.obj.hand.intf.ObjectHandler#refresh()
     */
    @Override
    public void refresh()
    {
        Log.entry();

        for (Refreshable refr : this.refreshables)
        {
            refr.refresh();
        }

        Log.exit();
    }

    @Override
    public boolean collision(BroadphaseCollisionData broadphaseCollisionData)
    {
        BroadPhaseCollider collider1 = this.broadColliders.get(broadphaseCollisionData.getBody1());
        BroadPhaseCollider collider2 = this.broadColliders.get(broadphaseCollisionData.getBody2());

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(broadphaseCollisionData, broadphaseCollisionData.getBody2());
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(broadphaseCollisionData, broadphaseCollisionData.getBody1());
        }

        return proceed;
    }

    @Override
    public boolean collision(NarrowphaseCollisionData narrowphaseCollisionData)
    {
        NarrowPhaseCollider collider1 = this.narrowColliders.get(narrowphaseCollisionData.getBody1());
        NarrowPhaseCollider collider2 = this.narrowColliders.get(narrowphaseCollisionData.getBody2());

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(narrowphaseCollisionData, narrowphaseCollisionData.getBody2());
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(narrowphaseCollisionData, narrowphaseCollisionData.getBody1());
        }

        return proceed;
    }

    @Override
    public boolean collision(ManifoldCollisionData manifoldCollisionData)
    {
        ManifoldCollider collider1 = this.manifoldColliders.get(manifoldCollisionData.getBody1());
        ManifoldCollider collider2 = this.manifoldColliders.get(manifoldCollisionData.getBody2());

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(manifoldCollisionData, manifoldCollisionData.getBody2());
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(manifoldCollisionData, manifoldCollisionData.getBody1());
        }

        return proceed;
    }

    @Override
    public void begin(ContactCollisionData contactCollisionData, Contact contact)
    {
        Contacter contacter1 = this.contacters.get(contactCollisionData.getBody1());
        Contacter contacter2 = this.contacters.get(contactCollisionData.getBody2());

        if (contacter1 != null)
        {
            contacter1.onContactBegin(contactCollisionData, contact, contactCollisionData.getBody2());
        }

        if (contacter2 != null)
        {
            contacter2.onContactBegin(contactCollisionData, contact, contactCollisionData.getBody1());
        }
    }

    @Override
    public void persist(ContactCollisionData contactCollisionData, Contact contact, Contact contact1)
    {
        Contacter contacter1 = this.contacters.get(contactCollisionData.getBody1());
        Contacter contacter2 = this.contacters.get(contactCollisionData.getBody2());

        if (contacter1 != null)
        {
            contacter1.persist(contactCollisionData, contact, contact1, contactCollisionData.getBody2());
        }

        if (contacter2 != null)
        {
            contacter2.persist(contactCollisionData, contact, contact1, contactCollisionData.getBody1());
        }
    }

    @Override
    public void end(ContactCollisionData contactCollisionData, Contact contact)
    {
        Contacter contacter1 = this.contacters.get(contactCollisionData.getBody1());
        Contacter contacter2 = this.contacters.get(contactCollisionData.getBody2());

        if (contacter1 != null)
        {
            contacter1.onContactEnd(contactCollisionData, contact, contactCollisionData.getBody2());
        }

        if (contacter2 != null)
        {
            contacter2.onContactEnd(contactCollisionData, contact, contactCollisionData.getBody1());
        }
    }

    @Override
    public void destroyed(ContactCollisionData contactCollisionData, Contact contact)
    {

    }

    @Override
    public void collision(ContactCollisionData contactCollisionData)
    {
        ConstraintCollider collider1 = this.constraintColliders.get(contactCollisionData.getBody1());
        ConstraintCollider collider2 = this.constraintColliders.get(contactCollisionData.getBody2());

        if (collider1 != null)
        {
            collider1.onCollision(contactCollisionData, contactCollisionData.getBody2());
        }

        if (collider2 != null)
        {
            collider2.onCollision(contactCollisionData, contactCollisionData.getBody1());
        }
    }

    @Override
    public void preSolve(ContactCollisionData contactCollisionData, Contact contact)
    {

    }

    @Override
    public void postSolve(ContactCollisionData contactCollisionData, SolvedContact solvedContact)
    {

    }

    @Override
    public boolean collision(PhysicsBody physicsBody1, PhysicsBody physicsBody2)
    {
        return true;
    }

    @Override
    public boolean collision(PhysicsBody physicsBody1, BodyFixture bodyFixture1, PhysicsBody physicsBody2, BodyFixture bodyFixture2)
    {
        return true;
    }

    @Override
    public boolean collision(PhysicsBody physicsBody1, BodyFixture bodyFixture1, PhysicsBody physicsBody2, BodyFixture bodyFixture2, TimeOfImpact timeOfImpact)
    {
        TimeOfImpactCollider collider1 = this.timeOfImpactColliders.get(physicsBody1);
        TimeOfImpactCollider collider2 = this.timeOfImpactColliders.get(physicsBody2);

        boolean proceed = true;

        if (collider1 != null)
        {
            proceed = proceed && collider1.onCollision(physicsBody1, bodyFixture1, physicsBody2, bodyFixture2, timeOfImpact);
        }

        if (collider2 != null)
        {
            proceed = proceed && collider2.onCollision(physicsBody1, bodyFixture1, physicsBody2, bodyFixture2, timeOfImpact);
        }

        return proceed;
    }
}