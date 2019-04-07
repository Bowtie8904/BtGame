package bt.game.core.ctrl.spec.mouse;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import bt.game.core.scene.cam.Camera;
import bt.game.util.unit.Unit;

/**
 * @author &#8904
 *
 */
public class MouseController extends MouseAdapter
{
    private Consumer<MouseTarget> onRightClick;
    private Consumer<MouseTarget> onLeftClick;
    private Comparator<MouseTarget> zComparator;
    private List<MouseTarget> mouseTargets;
    private Component component;
    private int mouseX;
    private int mouseY;
    private MouseTarget lastClicked;
    private MouseTarget lastHovered;

    public MouseController(Component component)
    {
        this.component = component;
        this.component.addMouseListener(this);
        this.component.addMouseMotionListener(this);
        this.component.addMouseWheelListener(this);
        this.mouseTargets = new CopyOnWriteArrayList<>();

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

    public void setGlobalOnRightClick(Consumer<MouseTarget> onClick)
    {
        this.onRightClick = onClick;
    }

    public void setGlobalOnLeftClick(Consumer<MouseTarget> onClick)
    {
        this.onLeftClick = onClick;
    }

    public void clearMouseTargets()
    {
        this.mouseTargets.clear();
    }

    public void checkHover()
    {
        if (this.lastClicked != null)
        {
            // dont switch to another hover target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            return;
        }

        if (this.component.getMousePosition() != null && this.component.getMousePosition() != null)
        {
            sortTargets();

            Point p = this.component.getMousePosition();
            if (p != null)
            {
                double mx = p.getX();
                double my = p.getY();

                if (Camera.currentCamera != null)
                {
                    mx += Camera.currentCamera.getX().pixels();
                    my += Camera.currentCamera.getY().pixels();
                }

                boolean foundOne = false;

                for (MouseTarget target : this.mouseTargets)
                {
                    if (target.getArea().contains(mx, my))
                    {
                        if (!target.equals(this.lastHovered))
                        {
                            if (this.lastHovered != null)
                            {
                                this.lastHovered.afterHover();
                            }

                            this.lastHovered = target;
                            target.onHover();
                        }
                        foundOne = true;
                        break;
                    }
                }

                if (!foundOne && this.lastHovered != null)
                {
                    this.lastHovered.afterHover();
                    this.lastHovered = null;
                }
            }
        }
        else
        {
            if (this.lastHovered != null)
            {
                this.lastHovered.afterHover();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        this.mouseX = e.getX();
        this.mouseY = e.getY();

        Point p = new Point(this.mouseX, this.mouseY);

        if (Camera.currentCamera != null)
        {
            p = new Point((int)(this.mouseX + Camera.currentCamera.getX().pixels()),
                    (int)(this.mouseY + Camera.currentCamera.getY().pixels()));
        }

        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3)
        {
            sortTargets();

            for (MouseTarget target : this.mouseTargets)
            {
                if (target.getArea().contains(p))
                {
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        this.lastClicked = target; // used for dragging. only used with left mouse button
                        onLeftClick(target);
                        target.onLeftClick();
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3)
                    {
                        onRightClick(target);
                        target.onRightClick();
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            this.lastClicked = null;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (this.lastClicked != null)
        {
            // dont switch to another target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            this.lastClicked.onMouseWheelMove(e.getWheelRotation());
        }
        else
        {
            this.mouseX = e.getX();
            this.mouseY = e.getY();

            Point p = new Point(this.mouseX, this.mouseY);

            if (Camera.currentCamera != null)
            {
                p = new Point((int)(this.mouseX + Camera.currentCamera.getX().pixels()),
                        (int)(this.mouseY + Camera.currentCamera.getY().pixels()));
            }

            sortTargets();

            for (MouseTarget target : this.mouseTargets)
            {
                if (target.getArea().contains(p))
                {
                    target.onMouseWheelMove(e.getWheelRotation());
                    break;
                }
            }
        }
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

    private void onRightClick(MouseTarget target)
    {
        if (this.onRightClick != null)
        {
            this.onRightClick.accept(target);
        }
    }

    private void onLeftClick(MouseTarget target)
    {
        if (this.onLeftClick != null)
        {
            this.onLeftClick.accept(target);
        }
    }
}