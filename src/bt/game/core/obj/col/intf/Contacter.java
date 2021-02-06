package bt.game.core.obj.col.intf;

import org.dyn4j.dynamics.contact.Contact;
import org.dyn4j.world.ContactCollisionData;

/**
 * @author &#8904
 *
 */
public interface Contacter extends Collider
{
    /**
     * @see org.dyn4j.dynamics.contact.ContactListener#begin(org.dyn4j.dynamics.contact.ContactPoint)
     */
    public boolean onContactBegin(ContactCollisionData contactCollisionData, Contact contact);

    /**
     * @see org.dyn4j.dynamics.contact.ContactListener#end(org.dyn4j.dynamics.contact.ContactPoint)
     */
    public void onContactEnd(ContactCollisionData contactCollisionData, Contact contact);
}