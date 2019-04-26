package bt.game.core.obj.col;

import org.dyn4j.dynamics.contact.ContactPoint;

/**
 * @author &#8904
 *
 */
public interface Contacter extends Collider
{
    /**
     * @see org.dyn4j.dynamics.contact.ContactListener#begin(org.dyn4j.dynamics.contact.ContactPoint)
     */
    public boolean onContactBegin(ContactPoint point);

    /**
     * @see org.dyn4j.dynamics.contact.ContactListener#end(org.dyn4j.dynamics.contact.ContactPoint)
     */
    public void onContactEnd(ContactPoint point);
}