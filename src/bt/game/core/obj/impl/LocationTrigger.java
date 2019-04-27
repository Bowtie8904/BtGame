package bt.game.core.obj.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.contact.ContactPoint;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

import bt.game.core.obj.GameObject;
import bt.game.core.obj.col.CollisionFilter;
import bt.game.core.obj.col.Contacter;
import bt.game.core.scene.Scene;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public abstract class LocationTrigger extends GameObject implements Contacter
{
    protected List<Body> inContact;

    public LocationTrigger(Scene scene, Unit x, Unit y, Unit z, Convex shape, Class<?>... acceptedCollisions)
    {
        super(scene, z);
        this.inContact = new CopyOnWriteArrayList<>();

        BodyFixture bf = new BodyFixture(shape);
        bf.setFilter(new CollisionFilter(this, acceptedCollisions));
        bf.setSensor(true);
        addFixture(bf);
        setMass(MassType.NORMAL);
        translate(x.units(), y.units());

        scene.getGameObjectHandler().addObject(this);
    }

    public LocationTrigger(Scene scene, Unit x, Unit y, Convex shape, Class<?>... acceptedCollisions)
    {
        this(scene, x, y, Unit.zero(), shape, acceptedCollisions);
    }

    /**
     * @see bt.game.core.obj.col.Collider#getBody()
     */
    @Override
    public Body getBody()
    {
        return this;
    }

    /**
     * @see bt.game.core.obj.col.Contacter#onContactBegin(org.dyn4j.dynamics.contact.ContactPoint)
     */
    @Override
    public synchronized boolean onContactBegin(ContactPoint point)
    {
        if (!point.getBody1().equals(getBody()))
        {
            contactWithBody(point.getBody1());
        }
        else
        {
            contactWithBody(point.getBody2());
        }

        return true;
    }

    private void contactWithBody(Body body)
    {
        if (!this.inContact.contains(body))
        {
            this.inContact.add(body);
            execute(body);
        }
    }

    /**
     * @see bt.game.core.obj.col.Contacter#onContactEnd(org.dyn4j.dynamics.contact.ContactPoint)
     */
    @Override
    public synchronized void onContactEnd(ContactPoint point)
    {
        this.inContact.remove(point.getBody1());
        this.inContact.remove(point.getBody2());
    }

    public abstract void execute(Body body);
}