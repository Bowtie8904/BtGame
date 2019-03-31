package bt.game.core.ctrl.spec.mouse;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class MouseController extends MouseAdapter
{
    private Runnable onClick;
    private Comparator<MouseTarget> zComparator;
    private List<MouseTarget> mouseTargets;
    private Component component;
    private int mouseX;
    private int mouseY;
    private MouseTarget lastClicked;

    public MouseController(Component component)
    {
        this.component = component;
        this.component.addMouseListener(this);
        this.component.addMouseMotionListener(this);
        this.mouseTargets = new CopyOnWriteArrayList<>();

        this.onClick = () -> {};

        this.zComparator = new Comparator<MouseTarget>() {
            @Override
            public int compare(MouseTarget o1, MouseTarget o2)
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
        }.reversed();
    }

    public synchronized void addMouseTarget(MouseTarget target)
    {
        this.mouseTargets.add(target);
    }

    public synchronized void removeMouseTarget(MouseTarget target)
    {
        this.mouseTargets.remove(target);
    }

    public void checkHover()
    {
        if (this.lastClicked != null)
        {
            // dont switch to another hover target if we havtn released our last click yet, i.e. are still dragging the
            // target around
            this.lastClicked.onHover();
            return;
        }

        if (this.component.getMousePosition() != null && this.component.getMousePosition() != null)
        {
            sortTargets();

            Point p = this.component.getMousePosition();
            if (p != null)
            {
                int mx = (int)p.getX();
                int my = (int)p.getY();

                boolean foundOne = false;

                int hover = 0;
                int noHover = 0;

                for (MouseTarget target : this.mouseTargets)
                {
                    if (!foundOne && mouseOver(mx, my, target.getX(), target.getY(), target.getW(), target.getH()))
                    {
                        target.onHover();
                        foundOne = true;
                        hover ++ ;
                    }
                    else
                    {
                        target.afterHover();
                        noHover ++ ;
                    }
                }
            }
        }
        else
        {
            for (MouseTarget target : this.mouseTargets)
            {
                target.afterHover();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        sortTargets();

        for (MouseTarget target : this.mouseTargets)
        {
            if (mouseOver(this.mouseX, this.mouseY, target.getX(), target.getY(), target.getW(), target.getH()))
            {
                this.lastClicked = target;
                target.onClick();
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        this.lastClicked = null;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (this.lastClicked != null)
        {
            this.lastClicked.onDrag(Unit.forPixels(e.getX() - this.mouseX), Unit.forPixels(e.getY() - this.mouseY));
            this.mouseX = e.getX();
            this.mouseY = e.getY();
        }
    }

    private synchronized void sortTargets()
    {
        this.mouseTargets.sort(this.zComparator);
    }

    private boolean mouseOver(int mx, int my, Unit x, Unit y, Unit width, Unit height)
    {
        return (mx > x.pixels() && mx < x.pixels() + width.pixels())
                && (my > y.pixels() && my < y.pixels() + height.pixels());
    }
}