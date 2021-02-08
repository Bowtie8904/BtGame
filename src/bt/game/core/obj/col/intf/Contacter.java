package bt.game.core.obj.col.intf;

import org.dyn4j.collision.CollisionBody;
import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.world.ContactCollisionData;

/**
 * @author &#8904
 */
public interface Contacter extends Collider
{
    /**
     * Called at the first detection of a contact.
     *
     * @param contactCollisionData
     * @param contact
     * @return
     */
    public boolean onContactBegin(ContactCollisionData contactCollisionData, Contact contact, CollisionBody body);

    /**
     * Called when a contact ended.
     *
     * @param contactCollisionData
     * @param contact
     */
    public void onContactEnd(ContactCollisionData contactCollisionData, Contact contact, CollisionBody body);

    /**
     * Called if a contact persists for longer than one world step.
     *
     * @param contactCollisionData
     * @param contact
     * @param contact1
     */
    public void persist(ContactCollisionData contactCollisionData, Contact contact, Contact contact1, CollisionBody body);
}