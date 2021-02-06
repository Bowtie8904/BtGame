package bt.game.core.obj.impl;

import bt.game.core.obj.col.filter.CollisionFilter;
import bt.game.core.obj.col.intf.Contacter;
import bt.game.core.scene.intf.Scene;
import bt.game.util.unit.Unit;
import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.world.ContactCollisionData;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A basic implementation of a location based trigger that will execute an action if one of the specified types enters
 * the area of the trigger.
 * 
 * @author &#8904
 */
public abstract class LocationTrigger extends GameBody implements Contacter
{
    /** A list containing all objects that should currently not trigger an action. */
    protected List<CollisionBody> inContact;

    /** Indicates whether objects that leave the area and enter it again should trigger the action again. */
    protected boolean repeat;

    /**
     * Creates a new instance.
     * 
     * @param scene
     *            The scene that uses this trigger.
     * @param x
     *            The x position.
     * @param y
     *            The y position.
     * @param shape
     *            The area of the trigger.
     * @param acceptedCollisions
     *            The class types that are allowed to trigger this instance. Note that this trigger uses a
     *            {@link CollisionFilter} to filter for those types. Due to the implementation of that filter it might
     *            be needed that the triggering object also allows collisions with this trigger type.
     */
    public LocationTrigger(Scene scene, Unit x, Unit y, Convex shape, Class<?>... acceptedCollisions)
    {
        super(scene);
        this.inContact = new CopyOnWriteArrayList<>();
        this.repeat = true;

        BodyFixture bf = new BodyFixture(shape);
        bf.setFilter(new CollisionFilter(this,
                                         acceptedCollisions));
        bf.setSensor(true);
        addFixture(bf);
        setMass(MassType.FIXED_ANGULAR_VELOCITY);
        translate(x.units(),
                  y.units());

        scene.getObjectHandler().addObject(this);
    }

    /**
     * Sets whether this instance should be triggered again by the same object if it left the area and entered it again.
     * 
     * @param repeat
     *            true if an object should be able to trigger this instance multiple times by leaving and reentering the
     *            area.
     */
    public void setRepeat(boolean repeat)
    {
        this.repeat = repeat;
    }

    /**
     * @see bt.game.core.obj.col.intf.Collider#getBody()
     */
    @Override
    public CollisionBody getBody()
    {
        return this;
    }

    /**
     * @see bt.game.core.obj.col.intf.Contacter#onContactBegin(org.dyn4j.dynamics.contact.ContactPoint)
     */
    @Override
    public synchronized boolean onContactBegin(ContactCollisionData contactCollisionData, Contact contact)
    {
        if (!contactCollisionData.getBody1().equals(getBody()))
        {
            contactWithBody(contactCollisionData.getBody1());
        }
        else
        {
            contactWithBody(contactCollisionData.getBody2());
        }

        return true;
    }

    private void contactWithBody(CollisionBody body)
    {
        if (!this.inContact.contains(body))
        {
            this.inContact.add(body);
            execute(body);
        }
    }

    /**
     * @see bt.game.core.obj.col.intf.Contacter#onContactEnd(org.dyn4j.dynamics.contact.ContactPoint)
     */
    @Override
    public synchronized void onContactEnd(ContactCollisionData contactCollisionData, Contact contact)
    {
        if (this.repeat)
        {
            this.inContact.remove(contactCollisionData.getBody1());
            this.inContact.remove(contactCollisionData.getBody2());
        }
    }

    /**
     * Defines an action that is executed when the given body enters the area of this trigger.
     * 
     * <p>
     * Note that this method can be called multiple times for a single object if {@link #setRepeat(boolean) setRepeat}
     * is set to true (default).
     * </p>
     * 
     * @param body
     */
    public abstract void execute(CollisionBody body);
}