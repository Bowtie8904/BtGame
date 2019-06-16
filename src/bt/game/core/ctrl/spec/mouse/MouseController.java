package bt.game.core.ctrl.spec.mouse;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.dyn4j.geometry.Vector2;

import bt.game.core.container.GameContainer;
import bt.game.core.scene.Scene;
import bt.game.core.scene.cam.Camera;
import bt.game.util.unit.Unit;
import bt.utils.thread.Threads;

/**
 * @author &#8904
 *
 */
public class MouseController extends MouseAdapter
{
    private static MouseController instance;

    public static MouseController get()
    {
        return instance;
    }

    private Consumer<MouseTarget> onRightClick;
    private Consumer<MouseTarget> onLeftClick;
    private Comparator<MouseTarget> zComparator;
    private List<MouseTarget> mouseTargets;
    private GameContainer component;
    private int mouseX;
    private int mouseY;
    private MouseTarget lastClickedTarget;
    private MouseTarget lastHoveredTarget;

    public MouseController(GameContainer component)
    {
        instance = this;
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

    /**
     * Gets the X position of the cursor during the last click event.
     * 
     * @return
     */
    public int getLastMouseX()
    {
        return this.mouseX;
    }

    /**
     * Gets the Y position of the cursor during the last click event.
     * 
     * @return
     */
    public int getLastMouseY()
    {
        return this.mouseY;
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

    public void clearMouseTargets(Scene scene)
    {
        this.mouseTargets
                .stream()
                .filter(m -> m.getScene().equals(scene))
                .forEach(this.mouseTargets::remove);
    }

    public void checkHover()
    {
        if (this.lastClickedTarget != null)
        {
            // dont switch to another hover target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            return;
        }

        if (this.component.getMousePosition() != null && this.component.getMousePosition() != null)
        {
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

                sortTargets();
                
                for (MouseTarget target : this.mouseTargets)
                {
                    if (target.getShape().contains(new Vector2(mx, my)))
                    {
                        if (!target.equals(this.lastHoveredTarget))
                        {
                            if (this.lastHoveredTarget != null)
                            {
                                this.lastHoveredTarget.afterHover();
                            }

                            this.lastHoveredTarget = target;
                            target.onHover();
                        }
                        foundOne = true;
                        break;
                    }
                }

                if (!foundOne && this.lastHoveredTarget != null)
                {
                    this.lastHoveredTarget.afterHover();
                    this.lastHoveredTarget = null;
                }
            }
        }
        else
        {
            if (this.lastHoveredTarget != null)
            {
                this.lastHoveredTarget.afterHover();
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
                if (target.getShape().contains(new Vector2(p.x, p.y)))
                {
                    if (e.getButton() == MouseEvent.BUTTON1)
                    {
                        this.lastClickedTarget = target; // used for dragging. only used with left mouse button
                        onLeftClick(target);

                        Threads.get().executeCached(() -> {
                            target.onLeftClick();
                        });
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3)
                    {
                        onRightClick(target);
                        Threads.get().executeCached(() -> {
                            target.onRightClick();
                        });
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
        {
            this.lastClickedTarget = null;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (this.lastClickedTarget != null)
        {
            // dont switch to another target if we havnt released our last click yet, i.e. are still dragging the
            // target around
            this.lastClickedTarget.onMouseWheelMove(e.getWheelRotation());
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
                if (target.getShape().contains(new Vector2(p.x, p.y)))
                {
                    target.onMouseWheelMove(e.getWheelRotation());
                    return;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (this.lastClickedTarget != null)
        {
            this.lastClickedTarget.onDrag(e,
                    Unit.forPixels(e.getX() - this.mouseX),
                    Unit.forPixels(e.getY() - this.mouseY));

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

    /**
     * Sets the comparator that is used to sort mousetargets before checking hoverring.
     * 
     * <p>
     * If the cursor is hovering over multiple targets, the one with the lowest index in the list after applying the
     * comparator will be used.
     * </p>
     * 
     * @param comp
     */
    public void setTargetComparator(Comparator<MouseTarget> comp)
    {
        this.zComparator = comp;
    }
}